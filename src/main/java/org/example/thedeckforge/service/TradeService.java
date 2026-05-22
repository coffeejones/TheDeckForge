package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.Trade;
import org.example.thedeckforge.entity.TradeCard;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.TradeStatus;
import org.example.thedeckforge.entity.interfaces.ICollectionRepository;
import org.example.thedeckforge.entity.interfaces.ITradeRepository;
import org.example.thedeckforge.entity.interfaces.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TradeService {

    private static final int MAX_CARDS_PER_SIDE = 3;

    private final ITradeRepository tradeRepository;
    private final ICollectionRepository collectionRepository;
    private final IUserRepository userRepository;

    @Autowired
    public TradeService(ITradeRepository tradeRepository, ICollectionRepository collectionRepository, IUserRepository userRepository) {
        this.tradeRepository = tradeRepository;
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
    }


    public List<Trade> listOpenTrades() {
        List<Trade> trades = tradeRepository.findOpenTrades();
        for (Trade t : trades) {
            t.setProposerName(userRepository.findNameById(t.getProposerId()));
            if (t.getResponderId() != null) {
                t.setResponderName(userRepository.findNameById(t.getResponderId()));
            }
        }
        return trades;
    }

    public List<Trade> listMyTrades(User user) {
        return tradeRepository.findByProposerId(user.getId());
    }

    public List<Trade> listCountersAwaitingMe(User user) {
        return tradeRepository.findCountersAwaitingUser(user.getId());
    }

    public Trade getTrade(long tradeId) {
        return tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalStateException("Handel ikke fundet"));
    }


    @Transactional
    public long proposeTrade(User proposer, List<Long> offeredIds, List<Long> wantedIds) {

        // 1. Antal-validering
        if (offeredIds.isEmpty() || wantedIds.isEmpty()) {
            throw new IllegalStateException("Du skal vælge mindst ét kort på hver side");
        }
        if (offeredIds.size() > MAX_CARDS_PER_SIDE || wantedIds.size() > MAX_CARDS_PER_SIDE) {
            throw new IllegalStateException("Maks " + MAX_CARDS_PER_SIDE + " kort på hver side");
        }

        // 2. Ingen duplikater
        if (new HashSet<>(offeredIds).size() != offeredIds.size()) {
            throw new IllegalStateException("Du kan ikke vælge det samme kort to gange på offer-siden");
        }

        // 3. Proposer skal eje alle offered cards
        for (Long cardId : offeredIds) {
            if (!collectionRepository.userHasCard(proposer.getId(), cardId)) {
                throw new IllegalStateException("Du ejer ikke kort #" + cardId);
            }
        }

        // 4. Ingen af offered cards må være låst i en anden handel
        for (Long cardId : offeredIds) {
            if (tradeRepository.isCardLocked(cardId)) {
                throw new IllegalStateException("Kort #" + cardId + " er allerede i en aktiv handel");
            }
        }

        // 5. Opret handlen
        return tradeRepository.createTrade(proposer.getId(), offeredIds, wantedIds);
    }
    @Transactional
    public void acceptExactly(long tradeId, User responder, List<Long> responderCardIds) {

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalStateException("Handel ikke fundet"));

        // 1. Status-tjek
        if (trade.getStatus() != TradeStatus.OPEN) {
            throw new IllegalStateException("Handlen er ikke åben");
        }

        // 2. Responder må ikke være proposer
        if (trade.getProposerId().equals(responder.getId())) {
            throw new IllegalStateException("Du kan ikke acceptere din egen handel");
        }

        // 3. Antal-tjek
        if (responderCardIds.size() > MAX_CARDS_PER_SIDE) {
            throw new IllegalStateException("Maks " + MAX_CARDS_PER_SIDE + " kort");
        }

        // 4. Tjek at responder giver præcis det proposer ville have
        Set<Long> wantedCardIds = trade.getWanted().stream()
                .map(tc -> tc.getCard().getId())
                .collect(java.util.stream.Collectors.toSet());

        Set<Long> givenCardIds = new HashSet<>(responderCardIds);

        if (!wantedCardIds.equals(givenCardIds)) {
            throw new IllegalStateException(
                    "Kortene matcher ikke det der blev ønsket. " +
                            "Brug counterPropose() i stedet hvis du vil foreslå noget andet.");
        }

        // 5. Responder skal eje kortene + de må ikke være låst
        for (Long cardId : responderCardIds) {
            if (!collectionRepository.userHasCard(responder.getId(), cardId)) {
                throw new IllegalStateException("Du ejer ikke kort #" + cardId);
            }
            if (tradeRepository.isCardLocked(cardId)) {
                throw new IllegalStateException("Kort #" + cardId + " er i en anden handel");
            }
        }

        // 6.  ATOMISK EJERSKIFTE
        long proposerId = trade.getProposerId();
        long responderId = responder.getId();

        // Proposer's tilbudte kort → responder
        for (TradeCard tc : trade.getOffered()) {
            long cardId = tc.getCard().getId();
            collectionRepository.removeCardFromCollection(cardId, proposerId);
            collectionRepository.addCardToCollection(responderId, cardId);
        }

        // Responder's kort → proposer
        for (Long cardId : responderCardIds) {
            collectionRepository.removeCardFromCollection(cardId, responderId);
            collectionRepository.addCardToCollection(proposerId, cardId);
        }

        // 7. Markér handlen som færdig
        tradeRepository.updateStatus(tradeId, TradeStatus.ACCEPTED, responderId);
    }
    @Transactional
    public void counterPropose(long tradeId, User responder, List<Long> counterCardIds) {

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalStateException("Handel ikke fundet"));

        if (trade.getStatus() != TradeStatus.OPEN) {
            throw new IllegalStateException("Handlen er ikke åben");
        }
        if (trade.getProposerId().equals(responder.getId())) {
            throw new IllegalStateException("Du kan ikke counter-foreslå på din egen handel");
        }
        if (counterCardIds.isEmpty() || counterCardIds.size() > MAX_CARDS_PER_SIDE) {
            throw new IllegalStateException("Vælg 1-" + MAX_CARDS_PER_SIDE + " kort");
        }

        // Counter-kort må ikke være præcis det der blev ønsket (så var det jo en exact match)
        Set<Long> wantedIds = trade.getWanted().stream()
                .map(tc -> tc.getCard().getId())
                .collect(java.util.stream.Collectors.toSet());

        if (wantedIds.equals(new HashSet<>(counterCardIds))) {
            throw new IllegalStateException("Det er en exact match — brug acceptExactly() i stedet");
        }

        // Responder skal eje, og kortene må ikke være låst
        for (Long cardId : counterCardIds) {
            if (!collectionRepository.userHasCard(responder.getId(), cardId)) {
                throw new IllegalStateException("Du ejer ikke kort #" + cardId);
            }
            if (tradeRepository.isCardLocked(cardId)) {
                throw new IllegalStateException("Kort #" + cardId + " er i en anden handel");
            }
        }

        // Gem counter-kort + opdatér status
        tradeRepository.addCounterOfferedCards(tradeId, counterCardIds);
        tradeRepository.updateStatus(tradeId, TradeStatus.COUNTER_PROPOSED, responder.getId());
    }

    @Transactional
    public void approveCounter(long tradeId, User proposer) {

        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalStateException("Handel ikke fundet"));

        if (trade.getStatus() != TradeStatus.COUNTER_PROPOSED) {
            throw new IllegalStateException("Der er intet modforslag at godkende");
        }
        if (!trade.getProposerId().equals(proposer.getId())) {
            throw new IllegalStateException("Kun forslagsstilleren kan godkende modforslag");
        }

        long responderId = trade.getResponderId();

        // Proposer's offered cards → responder
        for (TradeCard tc : trade.getOffered()) {
            long cardId = tc.getCard().getId();
            collectionRepository.removeCardFromCollection(cardId, proposer.getId());
            collectionRepository.addCardToCollection(responderId, cardId);
        }

        // Responder's counter cards → proposer
        for (TradeCard tc : trade.getCounterOffered()) {
            long cardId = tc.getCard().getId();
            collectionRepository.removeCardFromCollection(cardId, responderId);
            collectionRepository.addCardToCollection(proposer.getId(), cardId);
        }

        tradeRepository.updateStatus(tradeId, TradeStatus.ACCEPTED, responderId);
    }

    @Transactional
    public void cancelOrDecline(long tradeId, User user) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalStateException("Handel ikke fundet"));

        boolean isProposer = trade.getProposerId().equals(user.getId());

        if (isProposer && trade.getStatus() == TradeStatus.OPEN) {
            // Proposer trækker egen handel tilbage
            tradeRepository.updateStatus(tradeId, TradeStatus.CANCELLED, null);
        } else if (isProposer && trade.getStatus() == TradeStatus.COUNTER_PROPOSED) {
            // Proposer afviser et counter-tilbud
            tradeRepository.updateStatus(tradeId, TradeStatus.DECLINED, trade.getResponderId());
        } else {
            throw new IllegalStateException("Du kan ikke afslutte denne handel");
        }
    }
}
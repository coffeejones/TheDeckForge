package org.example.thedeckforge.entity.interfaces;

import org.example.thedeckforge.entity.Trade;
import org.example.thedeckforge.entity.enums.TradeStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITradeRepository {
    List<Trade> findOpenTrades();
    List<Trade> findByProposerId(long userId);
    List<Trade> findCountersAwaitingUser(long userId);
    Optional<Trade> findById(long tradeId);
    long createTrade(long proposerId, List<Long> offeredCardIds, List<Long> wantedCardIds);
    boolean isCardLocked(long cardId);
    void updateStatus(long tradeId, TradeStatus newStatus, Long responderId);
    void addCounterOfferedCards(long tradeId, List<Long> cardIds);
}

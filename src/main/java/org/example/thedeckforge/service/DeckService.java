package org.example.thedeckforge.service;
import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.enums.FormatType;
import org.example.thedeckforge.entity.interfaces.ICardRepository;
import org.example.thedeckforge.entity.interfaces.IDeckRepository;
import org.example.thedeckforge.entity.interfaces.IUserRepository;
import org.example.thedeckforge.validation.ValidationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class DeckService {
    private final IDeckRepository deckRepository;
    private final IUserRepository userRepository;
    private final ICardRepository cardRepository;
    private final ValidationService validationService;

    @Autowired
    public DeckService(IDeckRepository deckRepository, IUserRepository userRepository, ICardRepository cardRepository, ValidationService validationService) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.validationService = validationService;
    }
    @PreAuthorize("hasRole('MEMEBER')")
    public void createDeck(Deck deck, User user){
        deck.setCards(new ArrayList<>());
        user.setDecks(new ArrayList<>());
        user.addDeck(deck);
        deckRepository.createUserDeck(deck,userRepository.getUserId(user));
    }
    @PreAuthorize("hasRole('MEMEBER')")
    public List<Deck> getUserDecks(User user){
        return cardRepository.getDecksCards(deckRepository.getUsersDecks(userRepository.getUserId(user)));
    }
    public Deck getDeckForm(){
        return new Deck();
    }
    @PreAuthorize("hasRole('MEMEBER')")
    public Deck getSpecificDeckFromUser(User user, String deckName){
        return user.getDeckFromName(deckName);
    }
    @PreAuthorize("hasRole('MEMEBER')")
    public void removeCardFromDeck(String deckName, User user, Card card){
        for (Deck deck : user.getDecks()){
            if (deck.getName().equals(deckName)){
                deck.removeCard(card);
            }
        }
    }
    public List<Deck> getAllDecks(){
        return deckRepository.getAllDecks();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void DeleteCardReferenceFromDeck(long cardId){
        deckRepository.deleteCardReferenceFromDeck(cardId);
    }
    @PreAuthorize("hasRole('MEMEBER')")
    public void addCardToDeck (String deckName, User user, Card card){
        for (Deck deck : user.getDecks()) {
            if (deck.getName().equals(deckName)) {
                deck.addCard(card);
            }
        }
    }
    @PreAuthorize("hasRole('MEMEBER')")
    public void saveDeck(String deckName, User user){
        ArrayList<Long> cardIds = new ArrayList<Long>();
        Deck deck = user.getDeckFromName(deckName);
        for (Card card : deck.getCards()) {
            cardIds.add(cardRepository.getCardId(card));
        }
        deckRepository.saveDeck(cardIds, deck);
    }
}


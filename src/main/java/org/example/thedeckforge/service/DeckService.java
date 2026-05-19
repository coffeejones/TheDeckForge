package org.example.thedeckforge.service;
import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.interfaces.ICardRepository;
import org.example.thedeckforge.entity.interfaces.IDeckRepository;
import org.example.thedeckforge.entity.interfaces.IUserRepository;
import org.example.thedeckforge.validation.ValidationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class DeckService {
    private final IDeckRepository deckRepository;
    private final ValidationService validationService;
    private final IUserRepository userRepository;
    private final ICardRepository cardRepository;

    @Autowired
    public DeckService(IDeckRepository deckRepository, ValidationService validationService, IUserRepository userRepository, ICardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.validationService = validationService;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    public void createDeck(Deck deck, User user){
        validateUser(user);
        deck.setCards(new ArrayList<>());
        user.setDecks(new ArrayList<>());
        user.addDeck(deck);
        deckRepository.createUserDeck(deck,userRepository.getUserId(user));
    }
    public List<Deck> getUserDecks(User user){
        validateUser(user);
        return cardRepository.getDecksCards(deckRepository.getUsersDecks(userRepository.getUserId(user)));
    }
    public Deck getDeckForm(){
        return new Deck();
    }

    public Deck getSpecificDeckFromUser(User user, String deckName){
        return user.getDeckFromName(deckName);
    }

    public void removeCardFromDeck(String deckName, User user, Card card){
        validateUser(user);
        for (Deck deck : user.getDecks()){
            if (deck.getName().equals(deckName)){
                deck.removeCard(card);
            }
        }
    }
    public List<Deck> getAllDecks(){
        return deckRepository.getAllDecks();
    }

    public void DeleteCardReferenceFromDeck(User adminUser, long cardId){
        validationService.validate(ValidationType.ADMIN, adminUser);
        deckRepository.deleteCardReferenceFromDeck(cardId);
    }

    public void addCardToDeck (String deckName, User user, Card card){
        for (Deck deck : user.getDecks()) {
            if (deck.getName().equals(deckName)) {
                deck.addCard(card);
            }
        }
    }

    public void saveDeck(String deckName, User user){
        validateUser(user);
        ArrayList<Long> cardIds = new ArrayList<Long>();
        Deck deck = user.getDeckFromName(deckName);
        for (Card card : deck.getCards()) {
            cardIds.add(cardRepository.getCardId(card));
        }
        deckRepository.saveDeck(cardIds, deck);
    }
    private void validateUser(User user){
        validationService.validate(ValidationType.USER,user);
    }
}


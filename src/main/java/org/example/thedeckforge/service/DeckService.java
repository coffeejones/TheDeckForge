package org.example.thedeckforge.service;
import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.interfaces.ICardRepository;
import org.example.thedeckforge.entity.interfaces.IDeckRepository;
import org.example.thedeckforge.entity.interfaces.IUserRepository;
import org.example.thedeckforge.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class DeckService {
private final IDeckRepository deckRepository;
private final UserService userService;
private final IUserRepository userRepository;
private final ICardRepository cardRepository;

@Autowired
public DeckService(IDeckRepository deckRepository, UserService userService, IUserRepository userRepository,  ICardRepository cardRepository) {
    this.deckRepository = deckRepository;
    this.userService = userService;
    this.userRepository = userRepository;
    this.cardRepository = cardRepository;
}

public void createDeck(Deck deck, User user){
    deck.setCards(new ArrayList<>());
    boolean problem = false;
    if(user.getDecks().isEmpty()){
        user.addDeck(deck);
        deckRepository.createUserDeck(deck,userRepository.getUserId(user));
    } else {
        for (Deck decks : user.getDecks()) {
            if (decks.getName().equals(deck.getName())) {
                //text back
                problem = true;
                break;
            }
        }
        if(!problem) {
            user.addDeck(deck);
            deckRepository.createUserDeck(deck, userRepository.getUserId(user));
        }
    }

}
public List<Deck> getUserDecks(User user){
    List<Deck> decks = cardRepository.getDecksCards(deckRepository.getUsersDecks(userRepository.getUserId(user)));
    if (decks == null){
        return new ArrayList<>();
    } else{
        return decks;
    }
}
    public Deck getDeckForm(){
        return new Deck();
    }

    public Deck getSpecificDeckFromUser(User user, String deckName){
    return user.getDeckFromName(deckName);
    }

    public void removeCardFromDeck(String deckName, User user, Card card) {
        for (Deck deck : user.getDecks()) {
            if (deck.getName().equals(deckName)) {
                deck.removeCard(card);
            }
        }
    }
    public void addCardToDeck (String deckName, User user, Card card){
        for (Deck deck : user.getDecks()) {
            if (deck.getName().equals(deckName)) {
                deck.addCard(card);
            }
        }

    }
    public void saveDeck(String deckName, User user){
    ArrayList<Long> cardIds = new ArrayList<Long>();
    Deck deck = user.getDeckFromName(deckName);
        for (Card card : deck.getCards()) {
            cardIds.add(cardRepository.getCardId(card));
        }
    deckRepository.saveDeck(cardIds, deck);
    }
    public void deleteDeck(String deckName, User user){
    Deck deck = user.getDeckFromName(deckName);
    deckRepository.deleteDeck(deck);
    }

    public HashMap<Card, Integer> getPresentableDeck(Deck deck){
    HashMap<Card, Integer> presentableList = new HashMap<Card, Integer>();
        for (Card card : deck.getCards()) {
            if(presentableList.get(card) == null){
                presentableList.put(card,1);
            } else {
                presentableList.put(card, presentableList.get(card) + 1);
            }
        }
        return presentableList;
    }

    public void deckNameEdit(String newDeckName, String oldDeckName, User user){
    for(Deck deck : user.getDecks()){
        if(deck.getName().equals(oldDeckName)){
            if(newDeckName.equals(oldDeckName)){
                break;
            } else {
                deck.setName(newDeckName);
                Deck oldDeck = new Deck();
                oldDeck.setName(oldDeckName);
                deckRepository.deleteDeck(oldDeck);
            }
        }
    }
}
}

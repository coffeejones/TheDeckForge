package org.example.thedeckforge.service;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.interfaces.IDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class DeckService {
private final IDeckRepository deckRepository;
private final UserService userService;

@Autowired
public DeckService(IDeckRepository deckRepository, UserService userService) {
    this.deckRepository = deckRepository;
    this.userService = userService;
}

public void createDeck(Deck deck, User user){
    deck.setCards(new ArrayList<>());
    user.addDeck(deck);
    deckRepository.createUserDeck(deck,user);
}
public List<Deck> getUserDecks(User user){
    return deckRepository.getUsersDecks(user);
}
    public Deck getDeckForm(){
        return new Deck();
    }

    public Deck getSpecificDeckFromUser(User user, String deckName){
    return user.getDeckFromName(deckName);
    }
}

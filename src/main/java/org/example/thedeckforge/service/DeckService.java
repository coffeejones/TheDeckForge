package org.example.thedeckforge.service;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.interfaces.IDeckRepository;
import org.example.thedeckforge.entity.interfaces.IUserRepository;
import org.example.thedeckforge.infrastructure.DeckRepository;
import org.example.thedeckforge.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class DeckService {
private final IDeckRepository deckRepository;
private final IUserRepository userRepository;
private final UserService userService;
@Autowired
public DeckService(IDeckRepository deckRepository, IUserRepository userRepository, UserService userService) {
    this.deckRepository = deckRepository;
    this.userRepository = userRepository;
    this.userService = userService;
}

public void createDeck(Deck deck, Authentication auth){
    User user = userService.getCurrentUser(auth);
    deck.setCards(new ArrayList<>());
    user.addDeck(deck);
    long Userid = userRepository.getUserId(user);
    deckRepository.createUserDeck(deck,Userid);
}
public List<Deck> getUserDecks(User user){
    long userid = userRepository.getUserId(user);
    return deckRepository.getUsersDecks(userid);
}
    public Deck getDeckForm(){
        return new Deck();
    }

    public Deck getSpecificDeckFromUser(User user, String deckName){
    return user.getDeckFromName(deckName);
    }
}

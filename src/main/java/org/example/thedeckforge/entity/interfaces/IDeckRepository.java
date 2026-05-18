package org.example.thedeckforge.entity.interfaces;

import org.example.thedeckforge.entity.Deck;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDeckRepository {

    List<Deck> getUsersDecks(long userId);
    void createUserDeck(Deck deck, long userId);
}

package org.example.thedeckforge.entity.interfaces;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDeckRepository {

    List<Deck> getUsersDecks(long userId);
    void createUserDeck(Deck deck, long userId);
    void removeDeckCard(Deck deck, long cardId, long userId);
}

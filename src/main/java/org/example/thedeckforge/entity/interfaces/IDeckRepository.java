package org.example.thedeckforge.entity.interfaces;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IDeckRepository {

    List<Deck> getUsersDecks(long userId);
    void createUserDeck(Deck deck, long userId);
    void saveDeck(List<Long> cardIds, Deck deck);
    void deleteDeck(Deck deck);
    void deleteCardReferenceFromDeck(long id);
    Optional<Long> getCommanderCardIdForDeck(Deck deck);
}

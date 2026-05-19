package org.example.thedeckforge.entity.interfaces;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.ObjectSearchCriteria;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ICardRepository {

    List<Card> returnCardListByName(ObjectSearchCriteria criteria);
    Optional<Card> returnCardByName(ObjectSearchCriteria criteria);
    void saveCard(Card card);
    void updateCard(Card card);
    void deleteCard(long cardId);
    List<Deck> getDecksCards(List<Deck> decks);

}

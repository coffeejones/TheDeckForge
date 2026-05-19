package org.example.thedeckforge.entity.interfaces;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.ObjectSearchCriteria;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ICardRepository {

    void populateCardList();
    List<Card> returnCardList();
    List<Card> returnCardListByName(ObjectSearchCriteria criteria);
    Optional<Card> returnCardById(ObjectSearchCriteria criteria);
    Optional<Card> returnCardByName(String name);
    void saveCard(Card card);
    List<Deck> getDecksCards(List<Deck> decks);
    long getCardId(Card card);

}
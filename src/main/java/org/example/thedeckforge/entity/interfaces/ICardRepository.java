package org.example.thedeckforge.entity.interfaces;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.ObjectSearchCriteria;
import java.util.List;
import java.util.Optional;

public interface ICardRepository {

    List<Card> returnCardListByName(ObjectSearchCriteria criteria);
    Optional<Card> returnCardByName(ObjectSearchCriteria criteria);
    void saveCard(Card card);
    void updateCard(Card card);
    void deleteCard(long cardId);
    List<Deck> getDecksCards(List<Deck> decks);
    long getCardId (Card card);
    Optional<Card> returnCardById(ObjectSearchCriteria  criteria);
    List<Card> searchPaginated(String searchTerm, int page, int pageSize);
    int countSearchResults(String searchTerm);

}

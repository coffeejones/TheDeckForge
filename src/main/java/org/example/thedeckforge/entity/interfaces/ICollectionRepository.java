package org.example.thedeckforge.entity.interfaces;

import org.example.thedeckforge.entity.Card;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICollectionRepository {
    List<Card> findOwnedCardsByUserId(long userId);
    List<Card> findOwnedCardsByUserId(long userId, int page, int pageSize);
    int countOwnedCardsByUserId(long userId);
    boolean userHasCard(long userId, long cardId);
    void deleteCardFromCollectionReference(long cardId);
    void addCardToCollection(long responderId, long cardId);
    void removeCardFromCollection(long cardId, long userId);
}

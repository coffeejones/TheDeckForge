package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.interfaces.ICollectionRepository;
import org.example.thedeckforge.entity.interfaces.IUserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionService {

    private final ICollectionRepository collectionRepository;
    private final IUserRepository userRepository;

    public CollectionService(ICollectionRepository collectionRepository, IUserRepository userRepository) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
    }
    @PreAuthorize("hasRole('MEMEBER')")
    public List<Card> getOwnedCards(long userId, int page, int pageSize) {
        return collectionRepository.findOwnedCardsByUserId(userId, page, pageSize);
    }
    @PreAuthorize("hasRole('MEMEBER')")
    public int countOwnedCards(long userId) {
        return collectionRepository.countOwnedCardsByUserId(userId);
    }
    @PreAuthorize("hasRole('MEMEBER')")
    public void addCardToCollection(Card card, User user) {
        userRepository.addCardToCollection(user, card);
    }
    @PreAuthorize("hasRole('MEMEBER')")
    public boolean userHadCard(Card card, User user) {
        return collectionRepository.userHasCard(user.getId(), card.getId());
    }
    @PreAuthorize("hasRole('MEMEBER')")
    public void removeCardFromCollection(Card card, User user) {
        collectionRepository.removeCardFromCollection(card.getId(),user.getId());
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCardReferenceFromCollection(long cardId){
        collectionRepository.deleteCardFromCollectionReference(cardId);
    }

}

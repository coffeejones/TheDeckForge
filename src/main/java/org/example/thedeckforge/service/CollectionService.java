package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.interfaces.ICollectionRepository;
import org.example.thedeckforge.infrastructure.CollectionRepository;
import org.example.thedeckforge.infrastructure.UserRepository;
import org.example.thedeckforge.validation.ValidationType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ValidationService validationService;

    public CollectionService(CollectionRepository collectionRepository, UserRepository userRepository, UserService userService, ValidationService validationService) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.validationService = validationService;
    }

    public List<Card> getOwnedCards(long userId) {
        return collectionRepository.findOwnedCardsByUserId(userId);
    }

    public List<Card> getOwnedCards(long userId, int page, int pageSize) {
        return collectionRepository.findOwnedCardsByUserId(userId, page, pageSize);
    }

    public int countOwnedCards(long userId) {
        return collectionRepository.countOwnedCardsByUserId(userId);
    }

    public void addCardToCollection(Card card, Authentication auth) {
        User user = userService.getCurrentUser(auth);
        userRepository.addCardToCollection(user, card);
    }

    public boolean userHadCard(Card card, Authentication auth) {
        User user = userService.getCurrentUser(auth);
        return collectionRepository.userHasCard(user.getId(), card.getId());
    }

    public void removeCardFromCollection(Card card, Authentication auth) {
        User user = userService.getCurrentUser(auth);
        collectionRepository.removeCardFromCollection(card.getId(),user.getId());
    }
    public void deleteCardReferenceFromCollection(User adminUser,long cardId){
        validationService.validate(ValidationType.ADMIN, adminUser);
        collectionRepository.deleteCardFromCollectionReference(cardId);
    }
}

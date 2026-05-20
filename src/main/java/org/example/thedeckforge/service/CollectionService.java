package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.interfaces.ICollectionRepository;
import org.example.thedeckforge.entity.interfaces.IUserRepository;
import org.example.thedeckforge.validation.ValidationType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionService {

    private final ICollectionRepository collectionRepository;
    private final IUserRepository userRepository;
    private final ValidationService validationService;

    public CollectionService(ICollectionRepository collectionRepository, IUserRepository userRepository, ValidationService validationService) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    public List<Card> getOwnedCards(long userId, int page, int pageSize) {
        return collectionRepository.findOwnedCardsByUserId(userId, page, pageSize);
    }

    public int countOwnedCards(long userId) {
        return collectionRepository.countOwnedCardsByUserId(userId);
    }

    public void addCardToCollection(Card card, User user) {
        validateUser(user);
        userRepository.addCardToCollection(user, card);
    }

    public boolean userHadCard(Card card, User user) {
        validateUser(user);
        return collectionRepository.userHasCard(user.getId(), card.getId());
    }

    public void removeCardFromCollection(Card card, User user) {
        validateUser(user);
        collectionRepository.removeCardFromCollection(card.getId(),user.getId());
    }
    public void deleteCardReferenceFromCollection(User adminUser,long cardId){
        validationService.validate(ValidationType.ADMINROLE, adminUser);
        collectionRepository.deleteCardFromCollectionReference(cardId);
    }
    private void validateUser(User user){
        validationService.validate(ValidationType.USERROLE,user);
    }
}

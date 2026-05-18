package org.example.thedeckforge.service;
import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.ObjectSearchCriteria;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.entity.interfaces.ICardRepository;
import org.example.thedeckforge.validation.ValidationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class CardService {
    private final ICardRepository cardRepository;
    private final ObjectSearchCriteriaService objectSearchCriteriaService;
    private final ValidationService validationService;

    @Autowired
    public CardService(ICardRepository cardRepository, ObjectSearchCriteriaService objectSearchCriteriaService, ValidationService validationService) {
        this.cardRepository = cardRepository;
        this.objectSearchCriteriaService = objectSearchCriteriaService;
        this.validationService = validationService;
    }

    public List<Card> getCardListOnSearchTerm(ObjectSearchCriteria searchTerm) {
        return cardRepository.returnCardListByName(searchTerm);
    }

    public List<Card> getCardListBasedOnSearchTerm(String searchTerm, CardType cardType) {

        ObjectSearchCriteria criteria = objectSearchCriteriaService.createSearchCriteria(searchTerm, cardType);

        return cardRepository.returnCardListByName(criteria);
    }

    public Card getCardById(long id){

        ObjectSearchCriteria criteria = objectSearchCriteriaService.createSearchCriteria(id);

        return cardRepository.returnCardById(criteria).orElseThrow(() -> new RuntimeException("Card with id " + id + " does not exist"));
    }

    public Card createDefaultCard(){
        return new Card();
    }

    public void saveCard(User adminUser, Card card, MultipartFile picture) throws IOException {
        validationService.validate(ValidationType.ADMIN, adminUser);
        String cardPictureRef = saveImage(picture);
        addPictureReferenceToCard(card,cardPictureRef);
        cardRepository.saveCard(card);
    }

    private String saveImage(MultipartFile picture) throws IOException {
        if (!picture.isEmpty()) {
            Path uploadPath = Paths.get("target/classes/static/img/cards");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = picture.getOriginalFilename();
            Files.copy(picture.getInputStream(), uploadPath.resolve(fileName),StandardCopyOption.REPLACE_EXISTING);
            return ("/img/cards/" + fileName);
        }
        return null;
    }
    private void addPictureReferenceToCard(Card card, String cardPictureRef) throws IOException {
        card.setPictureRef(cardPictureRef);
    }
    public void updateCard(User adminUser, Card card) {
        validationService.validate(ValidationType.ADMIN, adminUser);
    }
    public void deleteCard(User adminUser, long id) {
        validationService.validate(ValidationType.ADMIN, adminUser);
    }
}

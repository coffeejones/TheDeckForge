package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.ObjectSearchCriteria;
import org.example.thedeckforge.entity.interfaces.ICardRepository;
import org.example.thedeckforge.validation.exceptions.CardValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
@SpringBootTest
@Transactional
class CardServiceTest {

    private ICardRepository cardRepository;
    private ObjectSearchCriteriaService objectSearchCriteriaService;

    @BeforeEach
    public void setUp(){
        cardRepository.populateCardList();
    }

    @Test
    public void returnCardListTest() {
        List<Card> cardResult = cardRepository.returnCardList();

        Assertions.assertNotNull(cardResult);
        Assertions.assertFalse(cardResult.isEmpty());
        Assertions.assertEquals(2, cardResult.size());
    }
    @Test
    public void returnEmptyCardListTestBasedOnCriteria(){
        ObjectSearchCriteria criteria = new ObjectSearchCriteria();
        criteria.setObjectName("Booby");
        List<Card> cardResult = cardRepository.returnCardListByName(criteria);
        Assertions.assertTrue(cardResult.isEmpty());
    }
    @Test
    public void returnCardByIdTest() {
        Long id = 1L;
        ObjectSearchCriteria criteria = objectSearchCriteriaService.createSearchCriteria(id);

        Optional<Card> cardResult = cardRepository.returnCardById(criteria);
        Assertions.assertTrue(cardResult.isPresent());
    }
    @Test
    public void returnCardByIdFailTest() {
        Long id = 1000L;
        ObjectSearchCriteria criteria = objectSearchCriteriaService.createSearchCriteria(id);
        Optional<Card> cardResult = cardRepository.returnCardById(criteria);
        Assertions.assertFalse(cardResult.isPresent());
    }
    @Test
    public void returnCardByNameTest() {
        CardValidationException exception = Assertions.assertThrows(CardValidationException.class, () -> {} );
        Optional<Card> cardResult = cardRepository.returnCardByName("P");
        Assertions.assertTrue(cardResult.isPresent());
    }
    @Test
    public void returnCardByNameFailTest() {
        Optional<Card> cardResult = cardRepository.returnCardByName("m");
        Assertions.assertFalse(cardResult.isPresent());
    }

}
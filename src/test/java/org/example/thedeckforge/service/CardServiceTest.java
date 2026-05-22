package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.Authority;
import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.ObjectSearchCriteria;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.entity.enums.Roles;
import org.example.thedeckforge.entity.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@SpringBootTest
@Transactional
@Sql(scripts = {"/schema.sql", "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CardServiceTest {
    @Autowired
    private CardService cardService;

    @Autowired
    private ObjectSearchCriteriaService objectSearchCriteriaService;

    private User getAdminUser() {
        Authority adminAuthority = new Authority("admin@deckforge.com", "Password123!", Roles.ADMIN);
        return new User("Admin Account", LocalDate.of(1995, 2, 1), adminAuthority);
    }

    @Test
    void getCardListOnSearchTerm() {
        ObjectSearchCriteria criteria = objectSearchCriteriaService.createSearchCriteria("goblin", CardType.CREATURE);

        List<Card> cards = cardService.getCardListOnSearchTerm(criteria);

        Assertions.assertFalse(cards.isEmpty());
        Assertions.assertEquals(3, cards.size());
    }

    @Test
    void getCardListBasedOnSearchTerm() {
        List<Card> cards = cardService.getCardListBasedOnSearchTerm("goblin", CardType.CREATURE);
        Assertions.assertFalse(cards.isEmpty());
        Assertions.assertEquals(3, cards.size());
    }

    @Test
    void getCardByName() {
        Card card = cardService.getCardByName("Soul Warden");
        Assertions.assertNotNull(card);
        Assertions.assertEquals("Soul Warden", card.getCardName());
    }

    @Test
    void createDefaultCard() {
        Card card = cardService.createDefaultCard();

        Assertions.assertNotNull(card);
        Assertions.assertNull(card.getCardName());
    }

    @Test
    void saveCard() throws IOException {
        User adminUser = getAdminUser();
        Card card = new Card("Black Lotus", CardType.ARTIFACT, "Gray",
                "Alpha", "Mythic", "Add three mana of any single color.",
                "/img/cards/black_lotus.jpg", "{0}", 0, 0);

        MockMultipartFile emptyPicture = new MockMultipartFile("picture", new byte[0]);

        cardService.saveCard(card, emptyPicture);

        Card saved = cardService.getCardByName("Black Lotus");
        Assertions.assertNotNull(saved);
        Assertions.assertEquals("Black Lotus", saved.getCardName());
        Assertions.assertEquals(CardType.ARTIFACT, saved.getCardType());
    }

    @Test
    void saveCard_throwsException_whenUserIsNotAdmin() {
        Authority memberAuthority = new Authority("alice@example.com", "Password123!", Roles.MEMBER);
        User memberUser = new User("Alice Johnson", LocalDate.of(2000, 7, 12), memberAuthority);
        Card card = cardService.createDefaultCard();
        MockMultipartFile emptyPicture = new MockMultipartFile("picture", new byte[0]);

        Assertions.assertThrows(ValidationException.class,
                () -> cardService.saveCard(card, emptyPicture));
    }
    @Test
    void updateCard() throws IOException {
        Card card = cardService.getCardByName("Soul Warden");
        card.setRarity("Rare"); // was Common in data.sql
        MockMultipartFile emptyPicture = new MockMultipartFile("picture", new byte[0]);

        cardService.updateCard(card, emptyPicture);

        Card updated = cardService.getCardByName("Soul Warden");
        Assertions.assertEquals("Rare", updated.getRarity());
    }

    @Test
    void updateCard_throwsException_whenUserIsNotAdmin() {
        Card card = cardService.getCardByName("Soul Warden");
        MockMultipartFile emptyPicture = new MockMultipartFile("picture", new byte[0]);

        Assertions.assertThrows(ValidationException.class,
                () -> cardService.updateCard(card, emptyPicture));
    }

    @Test
    void deleteCard() {
        Card card = cardService.getCardByName("Soul Warden");
        cardService.deleteCard(card.getId());
        Assertions.assertThrows(RuntimeException.class,
                () -> cardService.getCardByName("Soul Warden"));
    }
    @Test
    void deleteCard_throwsException_whenUserIsNotAdmin() {
        Card card = cardService.getCardByName("Soul Warden");
        Assertions.assertThrows(ValidationException.class,
                () -> cardService.deleteCard(card.getId()));
    }
}
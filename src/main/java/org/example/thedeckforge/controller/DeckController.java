package org.example.thedeckforge.controller;


import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.service.CardService;
import org.example.thedeckforge.service.DeckService;
import org.example.thedeckforge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller

@RequestMapping("/decks")
public class DeckController {
    private final DeckService deckService;
    private final UserService userService;
    private final CardService cardService;

    @Autowired
    public DeckController(DeckService deckService, UserService userService,  CardService cardService) {
        this.deckService = deckService;
        this.userService = userService;
        this.cardService = cardService;
    }
    @GetMapping("/user-decks")
    public String userDecks(Model model, Authentication authentication) {
        User user = userService.getUserForm();
        user = userService.getCurrentUser(authentication);
        user.setDecks(deckService.getUserDecks(user));
        model.addAttribute("user", user);
        return "user-decks";
    }

    @GetMapping("/deck-editor/{id}")
    public String showDeckEditor(@PathVariable String id, Model model, Authentication authentication) {
        User user = userService.getUserForm();
        user = userService.getCurrentUser(authentication);
        user.setDecks(deckService.getUserDecks(user));
        Deck deck = deckService.getSpecificDeckFromUser(user, id);
        model.addAttribute("deck", deck);

        return "deck-editor";
    }
}


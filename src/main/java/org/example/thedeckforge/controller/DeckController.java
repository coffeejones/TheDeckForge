package org.example.thedeckforge.controller;


import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.enums.FormatType;
import org.example.thedeckforge.service.CardService;
import org.example.thedeckforge.service.DeckService;
import org.example.thedeckforge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller

@RequestMapping("/decks")
public class DeckController {
    private final DeckService deckService;
    private final CardService cardService;

    @Autowired
    public DeckController(DeckService deckService, CardService cardService) {
        this.deckService = deckService;
        this.cardService = cardService;
    }
    @GetMapping("/user-decks")
    public String userDecks(Model model, @AuthenticationPrincipal User user) {
        user.setDecks(deckService.getUserDecks(user));
        model.addAttribute("user", user);
        return "user-decks";
    }
    @GetMapping("/deck-creator")
    public String ShowDeckCreatorForm(Model model){
        model.addAttribute("formatType", FormatType.values());
        model.addAttribute("deck", deckService.getDeckForm());
        return "deck-creator";
    }
    @PostMapping("/deck-creator")
    public String createDeck(@ModelAttribute("deck") Deck deck, @AuthenticationPrincipal User user){
        deckService.createDeck(deck, user);
        return "redirect:/decks/user-decks";
    }
    @GetMapping("/deck-editor/{id}")
    public String showDeckEditor(@PathVariable String id, Model model,@AuthenticationPrincipal User user) {
        user.setDecks(deckService.getUserDecks(user));
        Deck deck = deckService.getSpecificDeckFromUser(user, id);
        model.addAttribute("deck", deck);

        return "deck-editor";
    }
}


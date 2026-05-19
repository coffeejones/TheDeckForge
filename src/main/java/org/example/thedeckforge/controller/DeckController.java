package org.example.thedeckforge.controller;


import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.enums.FormatType;
import org.example.thedeckforge.service.CardService;
import org.example.thedeckforge.service.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/deck-editor/{cardId}/{deckId}/remove")
    @ResponseBody
    public ResponseEntity<String> removeCard(@PathVariable long cardId, @PathVariable String deckId, @AuthenticationPrincipal User user) {
        System.out.println("Html works");
        Card card = cardService.getCardById(cardId);
        deckService.removeCardFromDeck(deckId, user, card);
        return ResponseEntity.ok("Kort fjernet");
    }
    @PostMapping("/deck-editor/{cardId}/{deckId}/add")
    @ResponseBody
    public ResponseEntity<String> addCard(@PathVariable long cardId, @PathVariable String deckId, @AuthenticationPrincipal User user) {
        Card card = cardService.getCardById(cardId);
        deckService.addCardToDeck(deckId, user, card);
        return ResponseEntity.ok("Kort tilføjet");
    }
    @GetMapping("/deck-editor/{deckId}/save-deck")
    public String saveDeck(@ModelAttribute("deck") @PathVariable String deckId, @AuthenticationPrincipal User user, Model model) {
        deckService.saveDeck(deckId, user);
        model.addAttribute("user", user);
        return "user-decks";
    }
    @GetMapping("/view")
    public String viewDecks(Model model, @AuthenticationPrincipal User user) {
        List<Deck> decks = deckService.getAllDecks();
        user.setDecks(decks);
        model.addAttribute("user", user);
        model.addAttribute("decks", decks);
        return "decks/view";
    }
}


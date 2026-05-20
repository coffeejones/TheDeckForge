package org.example.thedeckforge.controller;


import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.entity.enums.FormatType;
import org.example.thedeckforge.service.CardService;
import org.example.thedeckforge.service.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        Deck deck = deckService.getSpecificDeckFromUser(user, id);
        HashMap<Card, Integer> presentableCards = deckService.getPresentableDeck(deck);
        model.addAttribute("deck", deck);
        model.addAttribute("presentableCards", presentableCards);
        return "deck-editor";
    }
    @GetMapping("/deck-editor/{deckId}/deck-card-list")
    public String deckCardListController(@RequestParam String searchTerm, Model model, CardType cardType, @AuthenticationPrincipal User user, @PathVariable String deckId){
        List<Card> searchResults = cardService.getCardListBasedOnSearchTerm(searchTerm, cardType);
        Deck deck = deckService.getSpecificDeckFromUser(user, deckId);
        HashMap<Card, Integer> presentableCards = deckService.getPresentableDeck(deck);
        model.addAttribute("deck", deck);
        model.addAttribute("presentableCards", presentableCards);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("searchTerm", searchTerm);
        return "deck-editor";
    }
    @PostMapping("/deck-editor/{cardId}/{deckId}/remove")
    @ResponseBody
    public ResponseEntity<String> removeCard(@PathVariable long cardId, @PathVariable String deckId, @AuthenticationPrincipal User user) {
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
    @GetMapping("/deck-editor/deckId/delete-deck")
    public String deleteDeck(@RequestParam String deckId, @AuthenticationPrincipal User user) {
        deckService.deleteDeck(deckId, user);
        return "redirect:/decks/user-decks";
    }
    @GetMapping("/deck-name-editor/deckId")
    public String deckNameEditor(Model model) {
        model.addAttribute("formatType", FormatType.values());
        model.addAttribute("deck", deckService.getDeckForm());
        return "deck-name-editor";
    }

}


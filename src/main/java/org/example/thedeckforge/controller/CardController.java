package org.example.thedeckforge.controller;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.service.CardService;
import org.example.thedeckforge.service.CollectionService;
import org.example.thedeckforge.service.ObjectSearchCriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;
    private final CollectionService collectionService;
    private final ObjectSearchCriteriaService objectSearchCriteriaService;

    @Autowired
    public CardController(CardService cardService, CollectionService collectionService, ObjectSearchCriteriaService objectSearchCriteriaService) {
        this.cardService = cardService;
        this.collectionService = collectionService;
        this.objectSearchCriteriaService = objectSearchCriteriaService;
    }
    @GetMapping("/card-search")
    public String cardController(Model model) {
        model.addAttribute("searchTerm", "");
        return "card-search";
    }
    @GetMapping("/card-list")
    public String cardListController(@RequestParam String searchTerm, Model model, CardType cardType) {
        List<Card> searchResults = cardService.getCardListBasedOnSearchTerm(searchTerm, cardType);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("searchTerm", searchTerm);
        return "card-list";
    }
    @GetMapping("/card-detail/{id}")
    public String cardDetail(@PathVariable long id, Model model, Authentication auth) {
        Card card = cardService.getCardById(id);
        model.addAttribute("card", card);
        model.addAttribute("hasCard", auth != null && collectionService.userHadCard(card, auth));
        return "card-detail";
    }

}

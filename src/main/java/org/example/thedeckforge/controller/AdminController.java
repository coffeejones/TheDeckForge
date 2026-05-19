package org.example.thedeckforge.controller;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CardService cardService;

    @Autowired
    public AdminController(CardService cardService) {
        this.cardService = cardService;
    }
    @GetMapping("/admin-panel")
    public String adminPanel(){
        return "admin/admin-panel";
    }

    @GetMapping("/create-card-form")
    public String createCardForm(Model model) {
        model.addAttribute("Card",cardService.createDefaultCard());
        model.addAttribute("cardType", CardType.values());
        return "admin/create-card-form";
    }
    @PostMapping("/create-card")
    public String createCard(@AuthenticationPrincipal User adminUser,@ModelAttribute("Card") Card card, @RequestParam("cardImage")MultipartFile picture) throws IOException {
        cardService.saveCard(adminUser, card, picture);
        return "admin/create-card-form";
    }
    @GetMapping("/admin-card-list")
    public String listCards(@RequestParam(required = false) String searchTerm,@RequestParam(required = false) CardType cardType, Model model) {
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("cardTypes", CardType.values());
        List<Card> searchResults = cardService.getCardListBasedOnSearchTerm(searchTerm,cardType);
        model.addAttribute("searchResults", searchResults);
        return "admin/admin-cards-list";
    }
    @GetMapping("/admin-card-detail/{cardName}")
    public String adminCardDetail(@PathVariable String cardName, Model model) {
        Card card = cardService.getCardByName(cardName);
        model.addAttribute("card", card);
        model.addAttribute("cardTypes", CardType.values());
        return "admin/admin-card-detail";
    }
    @PostMapping("/admin-card-detail")
    public String cardEdit(@AuthenticationPrincipal User adminUser, @ModelAttribute("card") Card card, @RequestParam("picture") MultipartFile picture) throws IOException {
        cardService.updateCard(adminUser, card, picture);
        return "redirect:/admin/admin-card-detail/" + card.getCardName();
    }
    @PostMapping("/admin-card-delete/{cardId}")
    public String deleteCard(@AuthenticationPrincipal User adminUser, @PathVariable long cardId) {
        cardService.deleteCard(adminUser, cardId);
        return "redirect:/admin/admin-card-list";
    }
}

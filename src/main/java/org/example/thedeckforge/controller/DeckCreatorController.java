package org.example.thedeckforge.controller;
import org.example.thedeckforge.entity.User;
import org.springframework.security.core.Authentication;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.enums.FormatType;
import org.example.thedeckforge.service.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/decks")
public class DeckCreatorController{

    private final DeckService deckService;
    @Autowired
    public DeckCreatorController(DeckService deckService) {
        this.deckService = deckService;
    }

    @GetMapping("/deck-creator")
    public String ShowDeckCreatorForm(Model model){
        model.addAttribute("formatType", FormatType.values());
        model.addAttribute("deck", deckService.getDeckForm());
        return "deck-creator";
    }
    @PostMapping("/deck-creator")
    public String createDeck(@AuthenticationPrincipal User user, @ModelAttribute("deck") Deck deck){
        deckService.createDeck(deck, user);
        return "redirect:/decks/user-decks";
    }

}

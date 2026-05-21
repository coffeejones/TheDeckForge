package org.example.thedeckforge.controller;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Trade;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.service.CardService;
import org.example.thedeckforge.service.CollectionService;
import org.example.thedeckforge.service.TradeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/trades")
@PreAuthorize("hasRole('MEMBER')")
public class TradeController {

    private static final int CATALOG_PAGE_SIZE = 20;
    private static final int LARGE_PAGE = 1000;     // til "alle mine kort" på trade-formen

    private final TradeService tradeService;
    private final CollectionService collectionService;
    private final CardService cardService;

    public TradeController(TradeService tradeService,
                           CollectionService collectionService,
                           CardService cardService) {
        this.tradeService = tradeService;
        this.collectionService = collectionService;
        this.cardService = cardService;
    }

    // ============================================================
    // 1. GET /trades — liste-siden
    // ============================================================
    @GetMapping
    public String list(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("openTrades", tradeService.listOpenTrades());
        model.addAttribute("myTrades", tradeService.listMyTrades(user));
        model.addAttribute("countersWaiting", tradeService.listCountersAwaitingMe(user));
        return "trades/list";
    }

    // ============================================================
    // 2. GET /trades/new — vis oprettelses-form
    // ============================================================
    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal User user,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "") String search,
                          Model model) {

        // Egen samling — alle kort, ingen paginering på trade-formen
        model.addAttribute("myCards", collectionService.getOwnedCards(user.getId(), 0, LARGE_PAGE));

        // Katalog — søgning + paginering
        int totalResults = cardService.countSearchResults(search);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalResults / CATALOG_PAGE_SIZE));

        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        List<Card> catalogCards = cardService.searchPaginated(search, page, CATALOG_PAGE_SIZE);

        model.addAttribute("catalogCards", catalogCards);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalResults", totalResults);
        model.addAttribute("search", search);

        return "trades/new";
    }

    // ============================================================
    // 3. POST /trades/new — opret ny handel
    // ============================================================
    @PostMapping("/new")
    public String create(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "offeredCardIds", required = false) List<Long> offeredCardIds,
            @RequestParam(name = "wantedCardIds", required = false) List<Long> wantedCardIds,
            RedirectAttributes redirect) {

        try {
            if (offeredCardIds == null) offeredCardIds = Collections.emptyList();
            if (wantedCardIds == null) wantedCardIds = Collections.emptyList();

            long tradeId = tradeService.proposeTrade(user, offeredCardIds, wantedCardIds);
            redirect.addFlashAttribute("successMessage", "Byttehandel oprettet!");
            return "redirect:/trades/" + tradeId;

        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/trades/new";
        }
    }

    // ============================================================
    // 4. GET /trades/{id} — detalje-side
    // ============================================================
    @GetMapping("/{id}")
    public String detail(@PathVariable long id,
                         @AuthenticationPrincipal User user,
                         Model model,
                         RedirectAttributes redirect) {
        try {
            Trade trade = tradeService.getTrade(id);

            model.addAttribute("trade", trade);
            model.addAttribute("proposerName", trade.getProposerName());
            model.addAttribute("responderName", trade.getResponderName());
            model.addAttribute("isProposer", trade.getProposerId().equals(user.getId()));

            return "trades/detail";

        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/trades";
        }
    }

    // ============================================================
    // 5. GET /trades/{id}/respond — vis vælg-kort-side
    // ============================================================
    @GetMapping("/{id}/respond")
    public String respondForm(@PathVariable long id,
                              @AuthenticationPrincipal User user,
                              Model model,
                              RedirectAttributes redirect) {
        try {
            Trade trade = tradeService.getTrade(id);

            if (trade.getProposerId().equals(user.getId())) {
                redirect.addFlashAttribute("errorMessage", "Du kan ikke reagere på din egen handel");
                return "redirect:/trades/" + id;
            }

            List<Card> myCards = collectionService.getOwnedCards(user.getId(), 0, LARGE_PAGE);

            // JS skal kunne detektere "exact match" — send wanted-IDs som liste
            List<Long> wantedIds = trade.getWanted().stream()
                    .map(tc -> tc.getCard().getId())
                    .collect(Collectors.toList());

            model.addAttribute("trade", trade);
            model.addAttribute("proposerName", trade.getProposerName());
            model.addAttribute("myCards", myCards);
            model.addAttribute("wantedIds", wantedIds);

            return "trades/respond";

        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/trades";
        }
    }

    // ============================================================
    // 6. POST /trades/{id}/respond — submit svar
    //    Server afgør om det er exact match eller counter-proposal
    // ============================================================
    @PostMapping("/{id}/respond")
    public String submitResponse(
            @PathVariable long id,
            @AuthenticationPrincipal User user,
            @RequestParam(name = "responderCardIds", required = false) List<Long> responderCardIds,
            RedirectAttributes redirect) {

        try {
            if (responderCardIds == null || responderCardIds.isEmpty()) {
                throw new IllegalStateException("Vælg mindst ét kort");
            }

            Trade trade = tradeService.getTrade(id);

            Set<Long> wanted = trade.getWanted().stream()
                    .map(tc -> tc.getCard().getId())
                    .collect(Collectors.toSet());

            if (new HashSet<>(responderCardIds).equals(wanted)) {
                // Exact match — gennemfør med det samme
                tradeService.acceptExactly(id, user, responderCardIds);
                redirect.addFlashAttribute("successMessage", "Handel gennemført!");
            } else {
                // Modforslag — afventer proposer
                tradeService.counterPropose(id, user, responderCardIds);
                redirect.addFlashAttribute("successMessage", "Modforslag sendt");
            }
            return "redirect:/trades/" + id;

        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/trades/" + id + "/respond";
        }
    }

    // ============================================================
    // 7. POST /trades/{id}/approve-counter — proposer godkender modforslag
    // ============================================================
    @PostMapping("/{id}/approve-counter")
    public String approveCounter(@PathVariable long id,
                                 @AuthenticationPrincipal User user,
                                 RedirectAttributes redirect) {
        try {
            tradeService.approveCounter(id, user);
            redirect.addFlashAttribute("successMessage", "Modforslag godkendt — handel gennemført!");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/trades/" + id;
    }

    // ============================================================
    // 8. POST /trades/{id}/decline — annullér egen handel eller afvis modforslag
    // ============================================================
    @PostMapping("/{id}/decline")
    public String cancelOrDecline(@PathVariable long id,
                                  @AuthenticationPrincipal User user,
                                  RedirectAttributes redirect) {
        try {
            tradeService.cancelOrDecline(id, user);
            redirect.addFlashAttribute("successMessage", "Handel afsluttet");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/trades/" + id;
    }
}

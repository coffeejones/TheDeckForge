package org.example.thedeckforge.validation;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.entity.exceptions.DeckRuleException;
import org.example.thedeckforge.entity.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeckValidation implements ValidationStrategy {
    @Override
    public ValidationType getValidationType() {
        return ValidationType.DECK;
    }

    @Override
    public void validate(Object object) {
        Deck deck = null;
        if(object instanceof Deck){
            deck = (Deck) object;
        } else {
            throw new ValidationException("Invalid validation type chosen");
        }
        switch (deck.getFormat()){
            case DRAFT -> {
                if(deck.getCards().size() < 40){
                    throw new DeckRuleException("Deck has to be at least 40 cards");
                }
            }
            case STANDARD ->  {
                if(deck.getCards().size() < 60){
                    throw new DeckRuleException("Deck has to be at least 60 cards");
                }
                HashMap<Card, Integer> numberedList = new HashMap<>();
                for(Card card : deck.getCards()){
                    numberedList.put(card, numberedList.getOrDefault(card, 0) + 1);
                }
                for(Card card : numberedList.keySet()){
                    if(numberedList.get(card) > 4){
                        if(card.getCardType() != CardType.LAND){
                            throw new DeckRuleException("Standard Decks can't have more than 4 copies of each card except lands.  " + card.getCardName() + " is at fault" );
                        }
                    }
                }
            }
            case COMMANDER ->  {
                if(deck.getCards().size() != 100){
                    throw new DeckRuleException("Deck has to be a 100 cards");
                }
                HashMap<Card, Integer> numberedList = new HashMap<>();
                for(Card card : deck.getCards()){
                    numberedList.put(card, numberedList.getOrDefault(card, 0) + 1);
                }
                for(Card card : numberedList.keySet()){
                    if(numberedList.get(card) > 1){
                        if(card.getCardType() != CardType.LAND){
                            throw new DeckRuleException("Commander decks may only have 1 copy of each card except lands.  " + card.getCardName() + " is at fault" );
                        }
                    }
                }
                if(deck.getCommanderCard() != null){
                String commanderManaCost = deck.getCommanderCard().getManaCost();
                String[] allowedManaCosts = commanderManaCost.split("[{}]");
                List<String> disallowedManaCosts = new ArrayList<>();
                disallowedManaCosts.add("U");
                disallowedManaCosts.add("W");
                disallowedManaCosts.add("B");
                disallowedManaCosts.add("G");
                disallowedManaCosts.add("R");
                for(String manaCost : allowedManaCosts){
                    if(disallowedManaCosts.contains(manaCost)){
                        disallowedManaCosts.remove(manaCost);
                    }
                }
                for(Card card : numberedList.keySet()){
                    for(String disallowedManaCost : disallowedManaCosts){
                        if(card.getManaCost().contains(disallowedManaCost)){
                            throw new DeckRuleException("Commander decks may only have cards that contain the same mana types as the commander card. " + card.getCardName() + " is at fault");
                        }
                    }
                }
                } else {
                    throw new DeckRuleException("Commander decks need a commander card");
                }
            }
        }
    }
}

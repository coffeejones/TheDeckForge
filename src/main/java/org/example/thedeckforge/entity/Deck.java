package org.example.thedeckforge.entity;

import org.example.thedeckforge.entity.enums.FormatType;

import java.util.List;

public class Deck {
    private long deckId;
    private String name;
    private List<Card> cards;
    private FormatType format;

    public Deck(long deckId, String name, FormatType format) {
        this.deckId = deckId;
        this.name = name;
        this.format = format;
    }

    public Deck(String name, List<Card> cards, FormatType format) {
        this.name = name;
        this.cards = cards;
        this.format = format;
    }
    public Deck(String name, FormatType format) {
        this.name = name;
        this.format = format;
    }
    public Deck(){}
    public long getDeckId() {
        return deckId;
    }
    public void setDeckId(long deckId) {
        this.deckId = deckId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Card> getCards() {
        return cards;
    }
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
    public void removeCard(Card card){
        this.cards.remove(card);
    }
    public FormatType getFormat() {
        return format;
    }
    public void setFormat(FormatType format) {
        this.format = format;
    }

}
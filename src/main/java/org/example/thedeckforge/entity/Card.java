package org.example.thedeckforge.entity;

import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.validation.exceptions.CardValidationException;
import java.util.Objects;

public class Card {
    private long id;
    private String cardName;
    private CardType cardTypes;
    private String color;
    private String set;
    private String rarity;
    private String ruleText;
    private String pictureRef;
    private String manaCost;
    private int attack;
    private int defense;

    public Card(){}

    public Card(long id, String cardName,CardType cardTypes, String color, String set, String rarity, String ruleText, String pictureRef, String manaCost, int attack, int defense) {
        if(id <= 0){
            throw new CardValidationException("Card id must be greater than 0");
        }
        if(cardName == null || cardName.isEmpty()){
            throw new CardValidationException("Card name cannot be empty");
        }
        if(cardTypes == null){
            throw new CardValidationException("Card types cannot be empty");
        }
        if(color == null || color.isEmpty()){
            throw new CardValidationException("Card color cannot be empty");
        }
        if(set == null || set.isEmpty()){
            throw new CardValidationException("Card set cannot be empty");
        }
        if(rarity == null || rarity.isEmpty()){
            throw new CardValidationException("Card rarity cannot be empty");
        }
        if(ruleText == null || ruleText.isEmpty()){
            throw new CardValidationException("Card rule text cannot be empty");
        }
        if(manaCost == null || manaCost.isEmpty()){
            throw new CardValidationException("Card mana cost cannot be empty");
        }
        this.id = id;
        this.cardName = cardName;
        this.cardTypes = cardTypes;
        this.color = color;
        this.set = set;
        this.rarity = rarity;
        this.ruleText = ruleText;
        this.pictureRef = pictureRef;
        this.manaCost = manaCost;
        this.attack = attack;
        this.defense = defense;
    }
    public Card(String cardName,CardType cardTypes, String color, String set, String rarity, String ruleText, String pictureRef, String manaCost, int attack, int defense) {
        if(cardName == null || cardName.isEmpty()){
            throw new CardValidationException("Card name cannot be empty");
        }
        if(cardTypes == null){
            throw new CardValidationException("Card types cannot be empty");
        }
        if(color == null || color.isEmpty()){
            throw new CardValidationException("Card color cannot be empty");
        }
        if(set == null || set.isEmpty()){
            throw new CardValidationException("Card set cannot be empty");
        }
        if(rarity == null || rarity.isEmpty()){
            throw new CardValidationException("Card rarity cannot be empty");
        }
        if(ruleText == null || ruleText.isEmpty()){
            throw new CardValidationException("Card rule text cannot be empty");
        }
        if(pictureRef == null || pictureRef.isEmpty()){
            throw new CardValidationException("Card picture ref cannot be empty");
        }
        this.cardName = cardName;
        this.cardTypes = cardTypes;
        this.color = color;
        this.set = set;
        this.rarity = rarity;
        this.ruleText = ruleText;
        this.pictureRef = pictureRef;
        this.manaCost = manaCost;
        this.attack = attack;
        this.defense = defense;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getCardName() {
        return cardName;
    }
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
    public CardType getCardType() {
        return cardTypes;
    }
    public void setCardType(CardType cardTypes) {
        this.cardTypes = cardTypes;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getSet() {
        return set;
    }
    public void setSet(String set) {
        this.set = set;
    }
    public String getRarity() {
        return rarity;
    }
    public void setRarity(String rarity) {
        this.rarity = rarity;
    }
    public String getRuleText() {
        return ruleText;
    }
    public void setRuleText(String ruleText) {
        this.ruleText = ruleText;
    }
    public String getPictureRef() {
        return pictureRef;
    }
    public void setPictureRef(String pictureRef) {
        this.pictureRef = pictureRef;
    }
    public String getManaCost() {
        return manaCost;
    }
    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }
    public int getAttack(){
        return attack;
    }
    public void setAttack(int attack){
        this.attack = attack;
    }
    public int getDefense(){
        return defense;
    }
    public void setDefense(int defense){
        this.defense = defense;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id == card.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

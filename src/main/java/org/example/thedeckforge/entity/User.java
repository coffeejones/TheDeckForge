package org.example.thedeckforge.entity;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class User {
    private long userId;
    private String name;
    private LocalDate dateOfBirth;
    private Authority authority;
    private Collection collection;
    private List<Deck> decks;
    public User(long userId, String name, LocalDate dateOfBirth, Authority authority, Collection collection, List<Deck> decks) {
        this.userId = userId;
        if(name.isEmpty()){
            throw new IllegalArgumentException("User's name cannot be empty");
        }
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.authority = authority;
        this.collection = new Collection();
        this.decks = decks;
    }
    public User(String name, LocalDate dateOfBirth, Authority authority) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.authority = authority;
        this.collection = new Collection();
        this.decks = new ArrayList<>();
    }
    public User(){
        this.collection = new Collection();
        this.decks = new ArrayList<>();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public Authority getAuthority() {
        return authority;
    }
    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
    public long getId() {return userId;}
    public void setId(long userId) {this.userId = userId;}
    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
    public void setCollection(Collection collection) {
        this.collection = collection;
    }
    public void addDeck(Deck deck){
        decks.add(deck);
    }
    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }
    public List<Deck> getDecks(){
        return decks;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Collection getCollection() {
        return collection;
    }
    public Deck getDeckFromName(String deckName){
            for(Deck d : decks){
                if(d.getName().equals(deckName)){
                    return d;
                }
            }
        return null;
    }
}

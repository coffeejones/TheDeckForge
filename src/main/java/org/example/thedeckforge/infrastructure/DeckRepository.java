package org.example.thedeckforge.infrastructure;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.entity.enums.FormatType;
import org.example.thedeckforge.entity.interfaces.IDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
@Repository
public class DeckRepository implements IDeckRepository {

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public DeckRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void createUserDeck(Deck deck, User user){
        String sql = "INSERT INTO Decks (UserId, DeckName, DeckFormat) VALUES (?, ?, ?) ";
        jdbcTemplate.update(sql,user.getId(),deck.getName(),deck.getFormat());
    }


    @Override
    public List<Deck> getUsersDecks (User user){
        String deckIdSql = "SELECT DeckID FROM Users RIGHT JOIN Decks ON Users.UserId = Decks.UserId where Email = ?";
        ArrayList<Integer> deckIds = new ArrayList<>();
        jdbcTemplate.queryForList(deckIdSql,user.getAuthority().getUsername(),deckIds);
        String deckInfoSql = "SELECT * FROM Decks WHERE DeckID = ?";
        String deckContentsSql = "SELECT * FROM Cards LEFT JOIN DeckkCards ON Cards.CardId = DeckkCards.CardId WHERE DeckId = ?";
        List<Deck> decks = new ArrayList<>();
        for (Integer deckId : deckIds) {
            Deck deck = jdbcTemplate.queryForObject(deckInfoSql,(rs, rowNum) -> new Deck(rs.getString("DeckName"), FormatType.valueOf(rs.getString("DeckFormat"))), deckId);
            ArrayList<Card> cards = new ArrayList<>();
            jdbcTemplate.queryForList(deckContentsSql,deckId,cards);
            deck.setCards(cards);
            decks.add(deck);
        } //test
        return decks;
    }
    //I made this :b Niels, Vis det kan bruges Johan, men der skal måske lidt ændring til. Den skal I hvert tilfald nok brydes ned til lidt mere clean code :b
    public List<Deck> getUserDecks(User user){
        String sqlDeckQuery = "SELECT * FROM Decks WHERE UserUd = ?";
        List<Deck> decks = new ArrayList<>(jdbcTemplate.query(sqlDeckQuery, (rs, rowNum) ->
                new Deck(
                        rs.getLong("DeckId"),
                        rs.getString("DeckName"),
                        FormatType.valueOf(rs.getString("Format").toUpperCase())
                ), user.getId()
        ));
        if (!decks.isEmpty()) {
            String sqlDeckContentsQuery = "SELECT * FROM Cards LEFT JOIN DeckCards ON Cards.CardId = DeckCards.CardId WHERE DeckId = ?";
            for (Deck deck : decks) {
                List<Card> cards = new ArrayList<>(jdbcTemplate.query(sqlDeckContentsQuery, (rs, rowNum) ->
                        new Card(
                                rs.getLong("CardId"),
                                rs.getString("CharacterName"),
                                CardType.valueOf(rs.getString("CardType").toUpperCase()),
                                rs.getString("Color"),
                                rs.getString("CardSet"),
                                rs.getString("Rarity"),
                                rs.getString("RuleText"),
                                rs.getString("PictureReference"),
                                rs.getString("ManaCost"),
                                rs.getInt("ATK"),
                                rs.getInt("DEF")
                        ), deck.getDeckId()
                    )
                );
                deck.setCards(cards);
            }
            return decks;
        }
        return null;
    }
}

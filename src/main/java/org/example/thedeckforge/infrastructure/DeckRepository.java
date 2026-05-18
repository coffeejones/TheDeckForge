package org.example.thedeckforge.infrastructure;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.entity.enums.FormatType;
import org.example.thedeckforge.entity.interfaces.IDeckRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
@Repository
public class DeckRepository implements IDeckRepository {

    private JdbcTemplate jdbcTemplate;

    public DeckRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createUserDeck(Deck deck, long Userid){
        String sql = "INSERT INTO Decks (UserId, DeckName, Format) VALUES (?, ?, ?) ";
        jdbcTemplate.update(sql,Userid,deck.getName(),deck.getFormat().toString());
    }


    @Override
    public List<Deck> getUsersDecks (long userid){
        String deckIdSql = "SELECT DeckID FROM Decks LEFT JOIN Users ON Decks.UserId = Users.UserId where Decks.UserId = ?";
        List<Integer> deckIds = jdbcTemplate.queryForList(deckIdSql, Integer.class, userid);
        String deckInfoSql = "SELECT * FROM Decks WHERE DeckID = ?";
        List<Deck> decks = new ArrayList<>();
        for (Integer deckId : deckIds) {
            Deck deck = jdbcTemplate.queryForObject(deckInfoSql,(rs, rowNum) -> new Deck(rs.getString("DeckName"), FormatType.valueOf(rs.getString("Format"))), deckId);
            deck.setCards(getCardsByDeckId(deckId));
            decks.add(deck);
        }
        return decks;
    }

    private List<Card> getCardsByDeckId(long id){
        String deckContentsSql = "SELECT * FROM Cards LEFT JOIN DeckCards ON Cards.CardId = DeckCards.CardId WHERE DeckId = ?";
        return jdbcTemplate.query(deckContentsSql, (rs, rowNum) -> new Card(rs.getLong("CardId"), rs.getString("CharacterName"), CardType.valueOf(rs.getString("CardType").toUpperCase()),rs.getString("Color"), rs.getString("CardSet"), rs.getString("Rarity"), rs.getString("RuleText"), rs.getString("PictureReference"), rs.getString("ManaCost"), rs.getInt("ATK"), rs.getInt("DEF")) , id);
    }
}

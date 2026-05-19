package org.example.thedeckforge.infrastructure;

import org.example.thedeckforge.entity.Deck;
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
    public List<Deck> getUsersDecks(long userId){
        String sqlDeckQuery = "SELECT * FROM Decks WHERE UserId = ?";
        List<Deck> decks = new ArrayList<>(jdbcTemplate.query(sqlDeckQuery, (rs, rowNum) ->
                new Deck(
                        rs.getLong("DeckId"),
                        rs.getString("DeckName"),
                        FormatType.valueOf(rs.getString("Format").toUpperCase())
                ), userId
        ));
        if (!decks.isEmpty()) {
            return decks;
        }
        return null;
    }
    @Override
    public void deleteCardReferenceFromDeck(long id){
        String sqlQuery = "DELETE FROM DeckCards WHERE CardId = ?";
        jdbcTemplate.update(sqlQuery,id);
    }
}

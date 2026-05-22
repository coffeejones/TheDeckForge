package org.example.thedeckforge.infrastructure;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.Deck;
import org.example.thedeckforge.entity.ObjectSearchCriteria;
import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.entity.interfaces.ICardRepository;
import org.example.thedeckforge.infrastructure.sqlquerybuilders.CardSQLQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CardRepository implements ICardRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CardSQLQueryBuilder cardSQLQueryBuilder;
    @Autowired
    public CardRepository(JdbcTemplate jdbcTemplate,  CardSQLQueryBuilder cardSQLQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.cardSQLQueryBuilder = cardSQLQueryBuilder;
    }
    @Override
    public List<Card> returnCardListByName(ObjectSearchCriteria criteria) {
        List<Object> params = new ArrayList<>(); // Ai anvendt, Object bliver brugt siden listen af ting vi gerne vil søge efter kan bestå af flere ting som både String og enums.
        String sqlQuery = cardSQLQueryBuilder.buildQuery(criteria, params);
        return jdbcTemplate.query(sqlQuery, cardRowMapper(), params.toArray()
        );
    }
    @Override
    public Optional<Card> returnCardByName(ObjectSearchCriteria criteria) {
        List<Object> params = new ArrayList<>();
        String sqlQuery = cardSQLQueryBuilder.buildQuery(criteria, params);
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, cardRowMapper(), params.toArray())
        );
    }
    @Override
    public void saveCard(Card card) {
        String sqlQuery = "INSERT INTO Cards (CharacterName, CardType, Color, CardSet, Rarity, RuleText, PictureReference, ManaCost, ATK, DEF) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                card.getCardName(),
                card.getCardType().toString(),
                card.getColor(),
                card.getSet(),
                card.getRarity(),
                card.getRuleText(),
                card.getPictureRef(),
                card.getManaCost(),
                card.getAttack(),
                card.getDefense()
        );
    }

    @Override
    public void updateCard(Card card) {
        String sqlQuery = "UPDATE Cards SET CharacterName = ?, CardType = ?, Color = ?, CardSet = ?, Rarity = ?, RuleText = ?, PictureReference = ?, ManaCost = ?, ATK = ?, DEF = ? WHERE CardId = ?";
        jdbcTemplate.update(sqlQuery,
                card.getCardName(),
                card.getCardType().toString(),
                card.getColor(),
                card.getSet(),
                card.getRarity(),
                card.getRuleText(),
                card.getPictureRef(),
                card.getManaCost(),
                card.getAttack(),
                card.getDefense(),
                card.getId()
        );
    }

    @Override
    public void deleteCard(long cardId) {
        String sqlQuery = "DELETE FROM Cards WHERE CardId = ?";
        jdbcTemplate.update(sqlQuery, cardId);
    }

    @Override
    public List<Deck> getDecksCards(List<Deck> decks){
        String sqlDeckContentsQuery = "SELECT * FROM Cards LEFT JOIN DeckCards ON Cards.CardId = DeckCards.CardId WHERE DeckId = ?";
        for (Deck deck : decks) {
            List<Card> cards = new ArrayList<>(jdbcTemplate.query(sqlDeckContentsQuery, cardRowMapper(), deck.getDeckId()));
            deck.setCards(cards);
        }
        return decks;
    }

    private RowMapper<Card> cardRowMapper() {
        return (rs, rowNum) -> new Card(
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
        );
    }
    @Override
    public Optional<Card> returnCardById(ObjectSearchCriteria  criteria) {
        List<Object> params = new ArrayList<>();
        String sqlQuery = cardSQLQueryBuilder.buildQuery(criteria, params);
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, cardRowMapper(), params.toArray()
        ));
    }
    @Override
    public long getCardId (Card card){
        String sql = "Select CardId From Cards Where characterName = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("CardId"), card.getCardName());
    }

    public List<Card> searchPaginated(String searchTerm, int page, int pageSize) {
        String sql = """
        SELECT CardId, CharacterName, CardType, Color, CardSet, Rarity,
               RuleText, PictureReference, ManaCost, ATK, DEF
        FROM Cards
        WHERE CharacterName LIKE ?
        ORDER BY CharacterName
        LIMIT ? OFFSET ?
        """;
        String pattern = "%" + searchTerm + "%";
        return jdbcTemplate.query(sql, cardRowMapper(), pattern, pageSize, page * pageSize);
    }

    public int countSearchResults(String searchTerm) {
        String sql = "SELECT COUNT(*) FROM Cards WHERE CharacterName LIKE ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, "%" + searchTerm + "%");
        return count != null ? count : 0;
    }

}
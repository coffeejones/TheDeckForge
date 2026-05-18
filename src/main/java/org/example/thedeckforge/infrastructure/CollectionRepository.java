package org.example.thedeckforge.infrastructure;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.entity.interfaces.ICollectionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CollectionRepository implements ICollectionRepository {

    private final JdbcTemplate jdbc;

    public CollectionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Card> findOwnedCardsByUserId(long userId) {
        String sql = """
            SELECT
                c.CardId, c.CharacterName, c.CardType, c.Color, c.CardSet,
                c.Rarity, c.RuleText, c.PictureReference, c.ManaCost,
                c.ATK, c.DEF
            FROM Cards c
            JOIN Collections col ON c.CardId = col.CardId
            WHERE col.UserId = ?
            ORDER BY c.Rarity, c.CharacterName
            """;
        return jdbc.query(sql, cardRowMapper(), userId);
    }

    public List<Card> findOwnedCardsByUserId(long userId, int page, int pageSize) {
        String sql = """
            SELECT
                c.CardId, c.CharacterName, c.CardType, c.Color, c.CardSet,
                c.Rarity, c.RuleText, c.PictureReference, c.ManaCost,
                c.ATK, c.DEF
            FROM Cards c
            JOIN Collections col ON c.CardId = col.CardId
            WHERE col.UserId = ?
            ORDER BY c.Rarity, c.CharacterName
            LIMIT ? OFFSET ?
            """;
        return jdbc.query(sql, cardRowMapper(), userId, pageSize, page * pageSize);
    }

    public boolean userHasCard(long userId, long cardId) {
        String sql = "SELECT COUNT(*) FROM Collections WHERE UserId = ? AND CardId = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId, cardId);
        return count != null && count > 0;
    }

    public int countOwnedCardsByUserId(long userId) {
        String sql = "SELECT COUNT(*) FROM Collections WHERE UserId = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    public RowMapper<Card> cardRowMapper() {
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

    public void removeCardFromCollection(long cardId, long userId) {
        String sql = """
            DELETE FROM Collections WHERE UserId = ? AND CardId = ? LIMIT 1
        """;
        jdbc.update(sql, userId, cardId);
    }
}

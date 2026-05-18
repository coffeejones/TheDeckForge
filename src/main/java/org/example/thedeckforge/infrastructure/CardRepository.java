package org.example.thedeckforge.infrastructure;

import org.example.thedeckforge.entity.Card;
import org.example.thedeckforge.entity.ObjectSearchCriteria;
import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.entity.interfaces.ICardRepository;
import org.example.thedeckforge.infrastructure.sqlquerybuilders.CardSQLQueryBuilder;
import org.example.thedeckforge.infrastructure.sqlquerybuilders.SQLQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CardRepository implements ICardRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CardSQLQueryBuilder cardSQLQueryBuilder;
    @Autowired
    public CardRepository(JdbcTemplate jdbcTemplate, CardSQLQueryBuilder cardSQLQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.cardSQLQueryBuilder = cardSQLQueryBuilder;
    }
    @Override
    public void populateCardList() {
    }
    @Override
    public List<Card> returnCardList() {
        return List.of();
    }
    @Override
    public List<Card> returnCardListByName(ObjectSearchCriteria criteria) {
        List<Object> params = new ArrayList<>(); // Ai anvendt, Object bliver brugt siden listen af ting vi gerne vil søge efter kan bestå af flere ting som både String og enums.
        String sqlQuery = cardSQLQueryBuilder.buildQuery(criteria, params);
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
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
                ), params.toArray()
        );
    }
    @Override
    public Optional<Card> returnCardById(ObjectSearchCriteria  criteria) {
        List<Object> params = new ArrayList<>();
        String sqlQuery = cardSQLQueryBuilder.buildQuery(criteria, params);
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) ->
                new Card(rs.getLong("CardId"),
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
                ), params.toArray()
        ));
    }
    @Override
    public Optional<Card> returnCardByName(String name) {
        return Optional.empty();
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
}

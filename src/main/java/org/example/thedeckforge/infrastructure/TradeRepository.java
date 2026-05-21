package org.example.thedeckforge.infrastructure;

import org.example.thedeckforge.entity.*;
import org.example.thedeckforge.entity.enums.CardType;
import org.example.thedeckforge.entity.interfaces.ITradeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.example.thedeckforge.entity.TradeDirection.*;

@Repository
public class TradeRepository implements ITradeRepository {

    private final JdbcTemplate jdbc;

    public TradeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Trade> findOpenTrades() {
        String sql = """
        SELECT tradeId, proposerId, responderId, status, createdAt, resolvedAt
        FROM Trades
        WHERE status = 'OPEN'
        ORDER BY createdAt DESC
        """;
        List<Trade> trades = jdbc.query(sql, tradeRowMapper());

        for (Trade t : trades) {
            loadCardsInto(t);
        }
        return trades;
    }

    @Override
    public List<Trade> findByProposerId(long userId) {
        String sql = """
        SELECT tradeId, proposerId, responderId, status, createdAt, resolvedAt
        FROM Trades
        WHERE proposerId = ?
        ORDER BY createdAt DESC
        """;
        List<Trade> trades = jdbc.query(sql, tradeRowMapper(), userId);
        for (Trade t : trades) loadCardsInto(t);
        return trades;
    }

    @Override
    public List<Trade> findCountersAwaitingUser(long userId) {
        String sql = """
            SELECT tradeId, proposerId, responderId, status, createdAt, resolvedAt
            FROM Trades
            WHERE proposerId = ?
              AND status = 'COUNTER_PROPOSED'
            ORDER BY createdAt DESC
            """;

        List<Trade> trades = jdbc.query(sql, tradeRowMapper(), userId);

        for (Trade t : trades) {
            loadCardsInto(t);
        }
        return trades;
    }

    public Optional<Trade> findById(long tradeId) {

        String tradeSql = """
        SELECT tradeId, proposerId, responderId, status, createdAt, resolvedAt
        FROM Trades WHERE tradeId = ?
        """;

        List<Trade> trades = jdbc.query(tradeSql, tradeRowMapper(), tradeId);
        if (trades.isEmpty()) return Optional.empty();
        Trade trade = trades.get(0);


        String cardsSql = """
        SELECT tc.tradeCardId, tc.direction,
               c.CardId, c.CharacterName, c.CardType, c.Color, c.CardSet,
               c.Rarity, c.RuleText, c.PictureReference, c.ManaCost, c.ATK, c.DEF
        FROM Trade_Cards tc
        JOIN Cards c ON tc.cardId = c.CardId
        WHERE tc.tradeId = ?
        """;

        List<TradeCard> allCards = jdbc.query(cardsSql, tradeCardRowMapper(), tradeId);

        List<TradeCard> offered = new ArrayList<>();
        List<TradeCard> wanted = new ArrayList<>();
        List<TradeCard> counter = new ArrayList<>();

        for (TradeCard tc : allCards) {
            switch (tc.getDirection()) {
                case OFFERED         -> offered.add(tc);
                case WANTED          -> wanted.add(tc);
                case COUNTER_OFFERED -> counter.add(tc);
            }
        }

        trade.setOffered(offered);
        trade.setWanted(wanted);
        trade.setCounterOffered(counter);

        return Optional.of(trade);
    }

    @Override
    public long createTrade(long proposerId, List<Long> offeredCardIds, List<Long> wantedCardIds) {

        String tradeSql = """
        INSERT INTO Trades (proposerId, status, createdAt)
        VALUES (?, 'OPEN', CURRENT_TIMESTAMP)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    tradeSql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, proposerId);
            return ps;
        }, keyHolder);

        long tradeId = Objects.requireNonNull(keyHolder.getKey()).longValue();


        String cardSql = "INSERT INTO Trade_Cards (tradeId, cardId, direction) VALUES (?, ?, ?)";

        for (Long cardId : offeredCardIds) {
            jdbc.update(cardSql, tradeId, cardId, OFFERED.name());
        }


        for (Long cardId : wantedCardIds) {
            jdbc.update(cardSql, tradeId, cardId, WANTED.name());
        }

        return tradeId;
    }

    @Override
    public boolean isCardLocked(long cardId) {
        String sql = """
        SELECT COUNT(*)
        FROM Trade_Cards tc
        JOIN Trades t ON tc.tradeId = t.tradeId
        WHERE tc.cardId = ?
          AND tc.direction IN ('OFFERED', 'COUNTER_OFFERED')
          AND t.status     IN ('OPEN', 'COUNTER_PROPOSED')
        """;

        Integer count = jdbc.queryForObject(sql, Integer.class, cardId);
        return count != null && count > 0;
    }

    @Override
    public void updateStatus(long tradeId, TradeStatus newStatus, Long responderId) {
        String sql;
        if (newStatus == TradeStatus.ACCEPTED ||
                newStatus == TradeStatus.DECLINED ||
                newStatus == TradeStatus.CANCELLED) {
            sql = "UPDATE Trades SET status = ?, responderId = ?, resolvedAt = CURRENT_TIMESTAMP WHERE tradeId = ?";
        } else {
            sql = "UPDATE Trades SET status = ?, responderId = ? WHERE tradeId = ?";
        }
        jdbc.update(sql, newStatus.name(), responderId, tradeId);
    }

    @Override
    public void addCounterOfferedCards(long tradeId, List<Long> counterCardIds) {
        String sql = "INSERT INTO Trade_Cards (tradeId, cardId, direction) VALUES (?, ?, ?)";
        for (Long cardId : counterCardIds) {
            jdbc.update(sql, tradeId, cardId, TradeDirection.COUNTER_OFFERED.name());
        }
    }

    private RowMapper<Trade> tradeRowMapper() {
        return (rs, rowNum) -> {
            Trade t = new Trade();
            t.setTradeId(rs.getLong("tradeId"));
            t.setProposerId(rs.getLong("proposerId"));

            long responderId = rs.getLong("responderId");
            t.setResponderId(rs.wasNull() ? null : responderId);

            t.setStatus(TradeStatus.valueOf(rs.getString("status")));
            t.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());

            java.sql.Timestamp resolved = rs.getTimestamp("resolvedAt");
            t.setResolvedAt(resolved != null ? resolved.toLocalDateTime() : null);
            return t;
        };
    }

    private RowMapper<TradeCard> tradeCardRowMapper() {
        return (rs, rowNum) -> {
            Card card = new Card(
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
            return new TradeCard(
                    rs.getLong("tradeCardId"),
                    card,
                    TradeDirection.valueOf(rs.getString("direction"))
            );
        };
    }

    private void loadCardsInto(Trade trade) {
        String sql = """
        SELECT tc.tradeCardId, tc.direction,
               c.CardId, c.CharacterName, c.CardType, c.Color, c.CardSet,
               c.Rarity, c.RuleText, c.PictureReference, c.ManaCost, c.ATK, c.DEF
        FROM Trade_Cards tc
        JOIN Cards c ON tc.cardId = c.CardId
        WHERE tc.tradeId = ?
        """;
        List<TradeCard> all = jdbc.query(sql, tradeCardRowMapper(), trade.getTradeId());

        List<TradeCard> offered = new ArrayList<>();
        List<TradeCard> wanted = new ArrayList<>();
        List<TradeCard> counter = new ArrayList<>();

        for (TradeCard tc : all) {
            switch (tc.getDirection()) {
                case OFFERED         -> offered.add(tc);
                case WANTED          -> wanted.add(tc);
                case COUNTER_OFFERED -> counter.add(tc);
            }
        }
        trade.setOffered(offered);
        trade.setWanted(wanted);
        trade.setCounterOffered(counter);
    }
}

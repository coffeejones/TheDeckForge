package org.example.thedeckforge.infrastructure;

import org.example.thedeckforge.entity.Event;
import org.example.thedeckforge.entity.interfaces.IEventRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class EventRepository implements IEventRepository {

    private final JdbcTemplate jdbcTemplate;

    public EventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> findAll() {
        String sql = """
                SELECT e.id, e.name, e.date, e.location, e.description,
                       e.owner_id, u.name as owner_name
                FROM event e
                JOIN Users u ON e.owner_id = u.UserId
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Event e = new Event();
            e.setId(rs.getLong("id"));
            e.setName(rs.getString("name"));
            e.setDate(rs.getString("date"));
            e.setLocation(rs.getString("location"));
            e.setDescription(rs.getString("description"));
            e.setOwnerId(rs.getLong("owner_id"));
            e.setOwnerName(rs.getString("owner_name"));
            e.setParticipants(findParticipantNamesByEventId(rs.getLong("id")));
            return e;
        });
    }

    @Override
    public Optional<Event> findById(long id) {
        String sql = """
            SELECT e.id, e.name, e.date, e.location, e.description,
                   e.owner_id, u.Name AS owner_name
            FROM event e
            JOIN Users u ON e.owner_id = u.UserId
            WHERE e.id = ?
            """;
        List<Event> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Event e = new Event();
            e.setId(rs.getLong("id"));
            e.setName(rs.getString("name"));
            e.setDate(rs.getString("date"));
            e.setLocation(rs.getString("location"));
            e.setDescription(rs.getString("description"));
            e.setOwnerId(rs.getLong("owner_id"));
            e.setOwnerName(rs.getString("owner_name"));
            e.setParticipants(findParticipantNamesByEventId(rs.getLong("id")));
            return e;
        }, id);
        return results.stream().findFirst();
    }

    @Override
    public Event save(Event event) {
        String sql = """
            INSERT INTO event (name, date, location, description, owner_id)
            VALUES (?, ?, ?, ?, ?)
            """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, event.getName());
            ps.setString(2, event.getDate());
            ps.setString(3, event.getLocation());
            ps.setString(4, event.getDescription());
            ps.setLong(5, event.getOwnerId());
            return ps;
        }, keyHolder);
        event.setId(keyHolder.getKey().longValue());
        return event;
    }

    @Override
    public boolean deleteById(long id) {
        // delete participants first due to foreign key
        jdbcTemplate.update("DELETE FROM event_participant WHERE event_id = ?", id);
        int rows = jdbcTemplate.update("DELETE FROM event WHERE id = ?", id);
        return rows > 0;
    }

    @Override
    public void addParticipant(long eventId, long userId) {
        String sql = "INSERT INTO event_participant (event_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, eventId, userId);
    }

    @Override
    public boolean participantExists(long eventId, long userId) {
        String sql = "SELECT COUNT(*) FROM event_participant WHERE event_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, eventId, userId);
        return count != null && count > 0;
    }


    private List<String> findParticipantNamesByEventId(long eventId) {
        String sql = """
            SELECT u.Name
            FROM event_participant ep
            JOIN Users u ON ep.user_id = u.UserId
            WHERE ep.event_id = ?
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("Name"), eventId);
    }

    @Override
    public void update(Event event) {
        String sql = """
        UPDATE event SET name = ?, date = ?, location = ?, description = ?
        WHERE id = ?
        """;
        jdbcTemplate.update(sql, event.getName(), event.getDate(),
                event.getLocation(), event.getDescription(), event.getId());
    }
}

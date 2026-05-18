package org.example.thedeckforge.infrastructure;

import org.example.thedeckforge.entity.Event;
import org.example.thedeckforge.entity.interfaces.IEventRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class EventRepository implements IEventRepository {

    private final List<Event> events = new ArrayList<>();

    @Override
    public List<Event> findAll() {
        return events;
    }
    @Override
    public Optional<Event> findById(long id) {
        return events.stream().filter(e -> e.getId() == id).findFirst();
    }
    @Override
    public Event save(Event event) {
        events.add(event);
        return event;
    }
    @Override
    public boolean deleteById(long id) {
        return events.removeIf(e -> e.getId() == id);
    }
    //placeholder
}

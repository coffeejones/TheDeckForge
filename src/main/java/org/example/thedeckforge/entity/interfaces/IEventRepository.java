package org.example.thedeckforge.entity.interfaces;

import org.example.thedeckforge.entity.Event;

import java.util.List;
import java.util.Optional;


public interface IEventRepository {
    List<Event> findAll();
    Optional<Event> findById(long id);
    Event save(Event event);
    boolean deleteById(long id);
    void addParticipant(long eventId, long userId);
    boolean participantExists(long eventId, long userId);
}

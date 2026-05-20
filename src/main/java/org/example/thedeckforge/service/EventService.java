package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.Event;
import org.example.thedeckforge.entity.interfaces.IEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final IEventRepository eventRepository;

    public EventService(IEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event, long ownerId) {
        event.setOwnerId(ownerId);
        return eventRepository.save(event);
    }

    // In EventService
    public Event updateEvent(long id, Event updatedEvent, long requestingUserId, boolean isAdmin) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));

        if (!isAdmin && !existing.getOwnerId().equals(requestingUserId)) {
            throw new RuntimeException("Not authorized");
        }

        existing.setName(updatedEvent.getName());
        existing.setDate(updatedEvent.getDate());
        existing.setLocation(updatedEvent.getLocation());
        existing.setDescription(updatedEvent.getDescription());

        eventRepository.update(existing);
        return existing;
    }

    public boolean deleteEvent(long id, long requestingUserId, boolean isAdmin) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));

        if (!isAdmin && !existing.getOwnerId().equals(requestingUserId)) {
            throw new RuntimeException("Not authorized");
        }

        return eventRepository.deleteById(id);
    }

    public boolean joinEvent(long eventId, long userId) {
        if (eventRepository.participantExists(eventId, userId)) {
            return false; // already joined
        }
        eventRepository.addParticipant(eventId, userId);
        return true;
    }
}
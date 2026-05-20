package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.Event;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.interfaces.IEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final IEventRepository ieventRepository;

    @Autowired
    public EventService(IEventRepository ieventRepository) {
        this.ieventRepository = ieventRepository;
    }


    public List<Event> getAllEvents() {
        return ieventRepository.findAll();
    }


    public Optional<Event> getEventById(Long id) {
        return ieventRepository.findById(id);
    }

    public Event createEvent(Event event, long ownerId) {
        event.setOwnerId(ownerId);
        return ieventRepository.save(event);
    }

    public Event updateEvent(Long id, Event updateEvent, long requestingUserId) {
        Event existing = ieventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));

        if (!existing.getOwnerId().equals(requestingUserId)) {
            throw new RuntimeException("not authorized");
        }

        existing.setName(updateEvent.getName());
        existing.setDate(updateEvent.getDate());
        existing.setLocation(updateEvent.getLocation());
        existing.setDescription(updateEvent.getDescription());
        return existing;

    }

    public boolean deleteEvent(Long id, long requestingUserId) {
        Event existing = ieventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));

        if (!existing.getOwnerId().equals(requestingUserId)) {
            throw new RuntimeException("Not authorized");
        }
        return ieventRepository.deleteById(id);

    }

    public boolean addParticipant(Long id, long userId) {
        Event event = ieventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
        if (!event.getParticipants().contains(userId)) {
            event.getParticipants().add(userId));
            return true;
        }
        return false;
    }
}

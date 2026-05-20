package org.example.thedeckforge.controller;

import org.example.thedeckforge.entity.Event;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.interfaces.IUserRepository;
import org.example.thedeckforge.infrastructure.EventRepository;
import org.example.thedeckforge.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.example.thedeckforge.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    @Autowired
    public EventController(EventService eventService,UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    private long getCurrentUserId(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return user.getId();
    }

    @GetMapping
    public List<Event> getAll() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Event create(@RequestBody Event event, Principal principal) {
        long ownerId = getCurrentUserId(principal);
        return eventService.createEvent(event, ownerId);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable Long id, @RequestBody Event event, Principal principal) {
        long ownerId = getCurrentUserId(principal);
        try {
            return ResponseEntity.ok(eventService.updateEvent(id, event, ownerId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        long ownerId = getCurrentUserId(principal);
        try {
            return eventService.deleteEvent(id, ownerId)
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/{id}/participants")
    public ResponseEntity<Void> addParticipant(@PathVariable long id, Principal principal) {
        long userId = getCurrentUserId(principal);
        return eventService.addParticipant(id, userId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }
}

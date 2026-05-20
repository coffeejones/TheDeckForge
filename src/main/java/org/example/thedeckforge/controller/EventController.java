package org.example.thedeckforge.controller;

import org.example.thedeckforge.entity.Event;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.service.EventService;
import org.example.thedeckforge.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    private long getCurrentUserId(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return user.getId();
    }

    // everyone authenticated can see events
    @GetMapping
    public List<Event> getAll() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // only ADMIN and ORGANIZER can create
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public Event create(@RequestBody Event event, Principal principal) {
        long ownerId = getCurrentUserId(principal);
        return eventService.createEvent(event, ownerId);
    }

    // only ADMIN and ORGANIZER can edit (service checks ownership)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public ResponseEntity<Event> update(@PathVariable long id,
                                        @RequestBody Event event,
                                        Principal principal,
                                        Authentication authentication) {
        long requestingUserId = getCurrentUserId(principal);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        try {
            return ResponseEntity.ok(eventService.updateEvent(id, event, requestingUserId, isAdmin));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build();
        }
    }

    // only ADMIN and ORGANIZER can delete (service checks ownership)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public ResponseEntity<Void> delete(@PathVariable long id,
                                       Principal principal,
                                       Authentication authentication) {
        long requestingUserId = getCurrentUserId(principal);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        try {
            return eventService.deleteEvent(id, requestingUserId, isAdmin)
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).build();
        }
    }

    // any authenticated user can join
    @PostMapping("/{id}/participants")
    public ResponseEntity<Void> join(@PathVariable long id, Principal principal) {
        long userId = getCurrentUserId(principal);
        return eventService.joinEvent(id, userId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }
}
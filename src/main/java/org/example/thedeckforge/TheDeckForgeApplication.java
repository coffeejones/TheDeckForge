package org.example.thedeckforge;

import org.example.thedeckforge.entity.Event;
import org.example.thedeckforge.infrastructure.EventRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TheDeckForgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TheDeckForgeApplication.class, args);
    }

}

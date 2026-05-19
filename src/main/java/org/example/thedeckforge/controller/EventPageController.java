package org.example.thedeckforge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/event")
public class EventPageController {

    @GetMapping
    public String evenPage() {
        return "event";
    }
}

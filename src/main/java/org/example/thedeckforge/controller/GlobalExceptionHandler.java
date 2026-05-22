package org.example.thedeckforge.controller;

import org.example.thedeckforge.entity.exceptions.CardValidationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CardValidationException.class)
    public String handleCardNotFound(CardValidationException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        System.out.println(ex.getMessage());
        return "error";
    }

}

package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.interfaces.IUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("ups");
            return null;
        }
        return (User) authentication.getPrincipal();
    }
    public User getUserForm(){
        return new User();
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
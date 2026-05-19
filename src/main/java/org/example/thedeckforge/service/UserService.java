package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.interfaces.IUserRepository;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.Authority;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.interfaces.IUserRepository;
import org.example.thedeckforge.validation.ValidationType;
import org.example.thedeckforge.entity.exceptions.ValidationException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogInService {
    private final IUserRepository userRepository;
    private final ValidationService validationService;

    @Autowired
    public LogInService(IUserRepository userRepository,  ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    public User login(Authority loginRequest) {
        validateLoginRequest(loginRequest);
        Authority authLogin = userRepository.logUserIn(loginRequest);
        if (isValidCredentials(loginRequest, authLogin)) {
            return userRepository.getUserFromAuth(authLogin);
        }
        throw new ValidationException("Invalid credentials");
    }

    private void validateLoginRequest(Authority loginRequest) {
        validationService.validate(ValidationType.EMAIL, loginRequest);
        validationService.validate(ValidationType.PASSWORD, loginRequest);
    }

    private boolean isValidCredentials(Authority loginRequest, Authority authLogin) {
        return authLogin != null && BCrypt.checkpw(loginRequest.getPassword(), authLogin.getPassword());
    }
    public Authority getAuthorityFromEmail(String email){
        return userRepository.getAuthorityByEmail(email);
    }
    public User getUserFromAuthority(Authority authority){
        return userRepository.getUserFromAuth(authority);
    }
}

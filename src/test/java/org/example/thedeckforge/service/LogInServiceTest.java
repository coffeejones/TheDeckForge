package org.example.thedeckforge.service;

import org.example.thedeckforge.entity.Authority;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.entity.interfaces.IUserRepository;
import org.example.thedeckforge.validation.ValidationType;
import org.example.thedeckforge.entity.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogInServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private LogInService logInService;

    private Authority loginRequest;
    private Authority storedAuth;
    private User expectedUser;

    @BeforeEach
    void setUp() {
        loginRequest = new Authority();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("plainPassword");

        storedAuth = new Authority();
        storedAuth.setEmail("test@example.com");
        storedAuth.setPassword(BCrypt.hashpw("plainPassword", BCrypt.gensalt()));

        expectedUser = new User();
        expectedUser.setName("per");
    }

    @Test
    void login_success_returnsUser() {
        when(userRepository.logUserIn(loginRequest)).thenReturn(storedAuth);
        when(userRepository.getUserFromAuth(storedAuth)).thenReturn(expectedUser);

        User result = logInService.login(loginRequest);

        assertNotNull(result);
        assertEquals(expectedUser, result);

        verify(validationService).validate(ValidationType.EMAIL, loginRequest);
        verify(validationService).validate(ValidationType.PASSWORD, loginRequest);
        verify(userRepository).logUserIn(loginRequest);
        verify(userRepository).getUserFromAuth(storedAuth);
    }

    @Test
    void login_invalidPassword_throwsException() {
        storedAuth.setPassword(BCrypt.hashpw("wrongPassword", BCrypt.gensalt()));
        when(userRepository.logUserIn(loginRequest)).thenReturn(storedAuth);

        assertThrows(ValidationException.class, () -> logInService.login(loginRequest));

        verify(userRepository).logUserIn(loginRequest);
        verify(userRepository, never()).getUserFromAuth(any());
    }

    @Test
    void login_userNotFound_throwsException() {
        when(userRepository.logUserIn(loginRequest)).thenReturn(null);

        assertThrows(ValidationException.class, () -> logInService.login(loginRequest));

        verify(userRepository).logUserIn(loginRequest);
        verify(userRepository, never()).getUserFromAuth(any());
    }

    @Test
    void login_validationFails_throwsException() {
        doThrow(new ValidationException("Invalid email"))
                .when(validationService)
                .validate(ValidationType.EMAIL, loginRequest);

        assertThrows(ValidationException.class, () -> logInService.login(loginRequest));

        verify(userRepository, never()).logUserIn(any());
    }
}
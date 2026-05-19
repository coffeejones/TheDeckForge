package org.example.thedeckforge.seclayer;

import org.example.thedeckforge.entity.Authority;
import org.example.thedeckforge.entity.User;
import org.example.thedeckforge.service.LogInService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final LogInService logInService;

    private final PasswordEncoder passwordEncoder;
    @Autowired
    public CustomAuthenticationProvider(LogInService logInService, PasswordEncoder passwordEncoder) {
        this.logInService = logInService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = Objects.requireNonNull(authentication.getCredentials()).toString();
        Authority userAuthority = logInService.getAuthorityFromEmail(email);
        if (!passwordEncoder.matches(password, userAuthority.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        User user = logInService.getUserFromAuthority(userAuthority);
        return new UsernamePasswordAuthenticationToken(user,null,user.getAuthority().getAuthorities());
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

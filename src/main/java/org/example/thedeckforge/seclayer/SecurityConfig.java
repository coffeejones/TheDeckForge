package org.example.thedeckforge.seclayer;   // <-- byt til "config" eller "security", "validation" giver ikke mening

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //This runs all the beans in this class
@EnableWebSecurity //Initializes Spring Security
@EnableMethodSecurity //This should allow us to accept users based on they roles
public class SecurityConfig {


    private final CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    public SecurityConfig(CustomAuthenticationProvider customAuthenticationProvider) {
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    // === Sikkerhedsregler for HTTP requests ===
    @Bean // This build the security flow based on given rules (Which end-point are accessible by what role?, When to encode, and when to check role hierarchy)
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http
                .authenticationProvider(customAuthenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        // Offentligt
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/img/**", "/cards/**", "/decks/**").permitAll()

                        // Kun ADMIN
                        .requestMatchers("/admin/**", "/cards/new", "/cards/*/edit").hasRole("ADMIN")

                        // ORGANIZER eller højere
                        .requestMatchers("/event/new", "/event/*/results").hasRole("ORGANIZER")

                        // MEMBER eller højere (alle indloggede)
                        .requestMatchers("/collection/**", "/trades/**").hasRole("MEMBER")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form // This is where we can config how spring security is supposed to handle its form, when it comes to log in.
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout.permitAll().logoutSuccessUrl("/"));

        return http.build();
    }
}
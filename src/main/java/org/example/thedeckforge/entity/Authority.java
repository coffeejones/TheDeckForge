package org.example.thedeckforge.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.example.thedeckforge.entity.enums.Roles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class Authority implements UserDetails {
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Roles roles;
    public Authority(String email, String password, Roles roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
    public Authority() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + roles.name()));
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public String getUsername() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Roles getRole() {
        return roles;
    }
    public void setRoles(Roles roles) {
        this.roles = roles;
    }
}

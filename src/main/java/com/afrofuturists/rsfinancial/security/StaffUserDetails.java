package com.afrofuturists.rsfinancial.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.afrofuturists.rsfinancial.domain.StaffRole;
import com.afrofuturists.rsfinancial.domain.StaffUser;

public class StaffUserDetails implements UserDetails {

    private final UUID id;
    private final String username;
    private final String passwordHash;
    private final boolean enabled;
    private final StaffRole role;

    public StaffUserDetails(StaffUser staffUser) {
        this.id = staffUser.getId();
        this.username = staffUser.getUsername();
        this.passwordHash = staffUser.getPasswordHash();
        this.enabled = staffUser.isEnabled();
        this.role = staffUser.getRole();
    }

    public UUID getId() {
        return id;
    }

    public StaffRole getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // "ROLE_" prefix is Spring Security convention - hasRole('ADVISER')
        // checks for authority "ROLE_ADVISER".
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

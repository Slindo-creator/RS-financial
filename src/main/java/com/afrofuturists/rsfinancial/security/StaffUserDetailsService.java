package com.afrofuturists.rsfinancial.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.afrofuturists.rsfinancial.domain.StaffUser;
import com.afrofuturists.rsfinancial.repository.StaffUserRepository;

@Service
public class StaffUserDetailsService implements UserDetailsService {

    private final StaffUserRepository staffUserRepository;

    public StaffUserDetailsService(StaffUserRepository staffUserRepository) {
        this.staffUserRepository = staffUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StaffUser staffUser = staffUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No staff user found with username: " + username));
        return new StaffUserDetails(staffUser);
    }
}

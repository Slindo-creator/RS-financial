package com.afrofuturists.rsfinancial.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

import com.afrofuturists.rsfinancial.dto.AuthDtos.AuthResponse;
import com.afrofuturists.rsfinancial.dto.AuthDtos.LoginRequest;
import com.afrofuturists.rsfinancial.security.JwtUtils;
import com.afrofuturists.rsfinancial.security.StaffUserDetails;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            StaffUserDetails principal = (StaffUserDetails) authentication.getPrincipal();
            String token = jwtUtils.generateTokenFromUsername(principal);

            return ResponseEntity.ok(new AuthResponse(token, principal.getUsername(), principal.getRole(), 0));
        } catch (BadCredentialsException e) {
            // Deliberately the same message whether the username doesn't
            // exist or the password is wrong - distinguishing the two
            // would let a caller enumerate valid usernames.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    // No self-registration endpoint: staff accounts (advisers, compliance
    // officer, admin) are provisioned by an admin, not signed up by the
    // public - see the StaffUserSeeder for how initial accounts get
    // created. Revisit if a self-service flow is actually wanted later.
}

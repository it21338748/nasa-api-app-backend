package com.example.nasa.controller;


import com.example.nasa.entity.AuthenticationRequest;
import com.example.nasa.service.TokenBlacklistService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.nasa.constants.Constant.SECRET_KEY;

@RestController
@RequestMapping("/api/users")
public class AuthenticationController {
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private  UserDetailsService userDetailsService;

    @Autowired
    private  TokenBlacklistService tokenBlacklistService;



    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User details not found");
            }

            // Build claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", userDetails.getUsername());
            claims.put("iat", new Date().getTime());

            // Generate JWT token
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(new Date(System.currentTimeMillis() + 864000000)) // Token validity: 10 days
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();
            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            System.out.println("Generated Token: " + token);
            System.out.println("SecretKey : " + SECRET_KEY);

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = extractToken(authorizationHeader);
            tokenBlacklistService.addTokenToBlacklist(token);
            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

    private String extractToken(String authorizationHeader) {
        // Extract token from Authorization header (assuming Bearer token format)
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
    }
}

package com.surest_member_managemant.service;

import com.surest_member_managemant.config.JwtUtil;
import com.surest_member_managemant.dto.AuthRequest;
import com.surest_member_managemant.dto.AuthResponse;
import com.surest_member_managemant.entity.User;
import com.surest_member_managemant.repository.UserRepository;
import com.surest_member_managemant.config.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    public AuthResponse login(AuthRequest request) {
        // Authenticate username/password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token
        String token = jwtUtil.generateToken(
                user.getUsername(),
                userDetails.getAuthorities().stream().map(a -> a.getAuthority()).toList()
        );

        return new AuthResponse(token);
    }
}

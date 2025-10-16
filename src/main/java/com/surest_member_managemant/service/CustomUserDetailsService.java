package com.surest_member_managemant.service;

import com.surest_member_managemant.entity.User;
import com.surest_member_managemant.exception.NotFoundException;
import com.surest_member_managemant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(u.getUsername())
                .password(u.getPasswordHash())
                .roles(u.getRole().getName().replace("ROLE_", ""))
                .build();
    }

}

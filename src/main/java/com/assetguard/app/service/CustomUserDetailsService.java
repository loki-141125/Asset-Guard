package com.assetguard.app.service;

import com.assetguard.app.model.User;
import com.assetguard.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting login for: " + username);
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username)) // Allow email login
                .orElseThrow(() -> {
                    System.out.println("User not found: " + username);
                    return new UsernameNotFoundException("User not found with username/email: " + username);
                });
        System.out.println("User found: " + user.getUsername());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                Collections.emptyList());
    }
}

package com.assetguard.app.config;

import com.assetguard.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DebugUserRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- DEBUG: LISTING ALL USERS ---");
        userRepository.findAll().forEach(user -> {
            System.out.println("User: " + user.getUsername() + ", Email: " + user.getEmail() + ", ID: " + user.getId());
        });
        System.out.println("--- DEBUG: END USER LIST ---");
    }
}

package com.secchamp.chal.init;

import com.secchamp.chal.entity.User;
import com.secchamp.chal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DbInitializer {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @PostConstruct
    public void initUsers() {
        initAdminUser();
        initNormalUser();
    }

    // Initialize Admin User
    private void initAdminUser() {
        System.out.println("Checking if admin user exists...");

        User existingAdmin = userRepository.findByUsername("admin");
        if (existingAdmin == null) {
            System.out.println("No admin user found, creating admin...");

            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));  // Hash the password
            adminUser.setEmail("admin@secchamp.test");
            adminUser.setFirstname("admin");
            adminUser.setLastname("admin");
            adminUser.setPhone("1234567890");
            adminUser.setRole(0);  // 0 for admin
            adminUser.setCreateDate(LocalDateTime.now().format(formatter));

            userRepository.save(adminUser);
            System.out.println("Admin user created.");
        } else {
            System.out.println("Admin user already exists.");
        }
    }

    // Initialize Non-Admin User
    private void initNormalUser() {
        System.out.println("Checking if normal user exists...");

        User existingUser = userRepository.findByUsername("user");
        if (existingUser == null) {
            System.out.println("No normal user found, creating user...");

            User normalUser = new User();
            normalUser.setUsername("user");
            normalUser.setPassword(passwordEncoder.encode("password"));  // Hash the password
            normalUser.setEmail("user@secchamp.test");
            normalUser.setFirstname("user");
            normalUser.setLastname("user");
            normalUser.setPhone("0987654321");
            normalUser.setRole(1);  // 1 for regular user
            normalUser.setCreateDate(LocalDateTime.now().format(formatter));

            userRepository.save(normalUser);
            System.out.println("Normal user created.");
        } else {
            System.out.println("Normal user already exists.");
        }
    }
}

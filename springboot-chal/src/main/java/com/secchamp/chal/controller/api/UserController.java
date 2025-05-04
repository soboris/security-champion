package com.secchamp.chal.controller.api;

import com.secchamp.chal.entity.User;
import com.secchamp.chal.repository.UserRepository;
import com.secchamp.chal.util.JwtTokenUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import io.jsonwebtoken.Claims;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.List; 
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public UserController() {
        System.out.println("UserController initialized");
    }

    // Endpoint to retrieve all users, only accessible to admins
    @GetMapping("/getallusers")
    public ResponseEntity<?> getAllUsers() {
        // Get the authentication object from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if the user's role is '0' (admin)
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (!"0".equals(role)) {
            return ResponseEntity.status(403).body("Access denied: Admins only.");
        }

        // If the user is an admin, return the list of users
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(
        @RequestHeader("Authorization") String token,  // Extract token from Authorization header
        @PathVariable Long userId  // Get user ID from URL
    ) {
        // Extract the actual token from "Bearer <token>" format
        String jwt = token.substring(7);

        // Extract claims from the token
        Claims claims = jwtTokenUtil.extractAllClaims(jwt);

        // Extract userId as Integer, then convert it to Long if needed
        Integer userIdFromTokenInt = claims.get("userId", Integer.class);
        Long userIdFromToken = userIdFromTokenInt.longValue();  // Convert to Long

        // Extract role as Integer
        Integer roleFromToken = claims.get("role", Integer.class);  // Extract user role (e.g., 0 = admin)

        // Check if the user exists in the database
        Optional<User> existingUser = userRepository.findById(userId);
        if (!existingUser.isPresent()) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Admins (role 0) can access any user's profile
        if (roleFromToken != 0 && !userIdFromToken.equals(userId)) {
            // Non-admins (role != 0) can only access their own profile
            return ResponseEntity.status(403).body("You can only access your own profile");
        }

        // Return the user details for admins and the user themselves
        User user = existingUser.get();
        return ResponseEntity.ok(user);
    }


   @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(409).body("Username already exists");  // HTTP 409 Conflict
        }

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(409).body("Email address already exists");  // HTTP 409 Conflict
        }

        // Set default role to 1 (regular user) if not provided
        if (user.getRole() == null) {
            user.setRole(1);  // Default to '1' if no role is provided
        }

        // Hash the password using BCrypt
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set creation date and save the user
        user.setCreateDate(LocalDateTime.now().format(formatter));
        userRepository.save(user);

        return ResponseEntity.ok(user);  // Return successful registration
    }

    @PutMapping("/edit/{userId}")
    public ResponseEntity<?> editUser(
        @RequestHeader("Authorization") String token,  // Extract token from Authorization header
        @PathVariable Long userId,  // Get user ID from URL
        @RequestBody User updatedUser
    ) {
        // Extract the actual token from "Bearer <token>" format
        String jwt = token.substring(7);

        // Extract claims from the token
        Claims claims = jwtTokenUtil.extractAllClaims(jwt);
        
        // Extract the userId and role from the token
        Long userIdFromToken = claims.get("userId", Long.class);  // Correct the extraction to Long
        Integer roleFromToken = claims.get("role", Integer.class);  // Extract user role (0 = admin, others = non-admin)

        // Check if the user exists in the database
        Optional<User> existingUser = userRepository.findById(userId);
        if (!existingUser.isPresent()) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Role-based access control: Admin can edit any user, non-admins can only edit their own profile
        if (roleFromToken != 0 && !userIdFromToken.equals(userId)) {
            // If the user is not an admin and is trying to edit someone else, deny access
            return ResponseEntity.status(403).body("You can only edit your own profile");
        }

        // Check for unique email (make sure the new email doesn't exist for another user)
        User userWithEmail = userRepository.findByEmail(updatedUser.getEmail());
        if (userWithEmail != null && !userWithEmail.getUserId().equals(userId)) {
            return ResponseEntity.status(409).body("Email address already exists");  // HTTP 409 Conflict
        }

        // Check for unique username (make sure the new username doesn't exist for another user)
        User userWithUsername = userRepository.findByUsername(updatedUser.getUsername());
        if (userWithUsername != null && !userWithUsername.getUserId().equals(userId)) {
            return ResponseEntity.status(409).body("Username already exists");  // HTTP 409 Conflict
        }

        // Proceed to update the user's details
        User user = existingUser.get();
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setFirstname(updatedUser.getFirstname());
        user.setLastname(updatedUser.getLastname());
        user.setPhone(updatedUser.getPhone());

        // If password is updated, hash it and set the new password
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // Set the last update date
        user.setLastUpdateDate(LocalDateTime.now().format(formatter));

        // Save the updated user
        userRepository.save(user);

        // If the user is editing their own profile (non-admin editing themselves), regenerate the token
        if (roleFromToken != 0 || userIdFromToken.equals(userId)) {
            // Regenerate the JWT token with updated user information
            String newJwtToken = jwtTokenUtil.generateToken(user);

            // Return the new JWT token along with the success message
            return ResponseEntity.ok().header("Authorization", "Bearer " + newJwtToken)
                                .body("User updated successfully and new token generated.");
        }

        // If an admin is editing another user, no need to generate a new token
        return ResponseEntity.ok("User updated successfully.");
    }


    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(
        @RequestHeader("Authorization") String token,  // Extract token from Authorization header
        @PathVariable Long userId
    ) {
        // Extract the actual token from "Bearer <token>" format
        String jwt = token.substring(7);

        // Extract claims from the token
        Claims claims = jwtTokenUtil.extractAllClaims(jwt);
        Integer roleFromToken = claims.get("role", Integer.class);  // Extract user role (e.g., 0 = admin)

        // Only admins (role 0) can delete users
        if (roleFromToken != 0) {
            return ResponseEntity.status(403).body("Access denied: Only admins can delete users");
        }

        // Check if the user exists in the database
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Delete the user
        userRepository.delete(user.get());

        return ResponseEntity.ok("User deleted successfully");
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String username, @RequestParam String password) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
        
        if (user.isPresent()) {
            System.out.println("User found: " + username);
            if (passwordEncoder.matches(password, user.get().getPassword())) {  // Use BCrypt to check password
                System.out.println("Password match");
                user.get().setLastLogin(LocalDateTime.now().format(formatter));
                userRepository.save(user.get());

                // Generate JWT token with user details as claims
                String token = jwtTokenUtil.generateToken(user.get());

                // Build response with the JWT token
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);

                return ResponseEntity.ok(response);
            } else {
                System.out.println("Password mismatch");
                return ResponseEntity.status(404).body("Invalid username or password");
            }
        } else {
            System.out.println("User not found: " + username);
            return ResponseEntity.status(404).body("Invalid username or password");
        }
    }
}

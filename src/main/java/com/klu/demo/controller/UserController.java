package com.klu.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klu.demo.dto.ProfileUpdateRequest;
import com.klu.demo.entity.User;
import com.klu.demo.entity.Registration;
import com.klu.demo.repository.UserRepository;
import com.klu.demo.repository.RegistrationRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private RegistrationRepository registrationRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody ProfileUpdateRequest incoming) {
        return userRepository.findById(id)
            .map(u -> {
                String newRole = incoming.getRole() != null ? incoming.getRole() : u.getRole();
                String newEmail = incoming.getEmail() != null && !incoming.getEmail().isBlank()
                        ? incoming.getEmail().trim().toLowerCase()
                        : u.getEmail();

                if (incoming.getName() != null) u.setName(incoming.getName());
                if (incoming.getDepartment() != null) u.setDepartment(incoming.getDepartment());
                if (incoming.getRole() != null) u.setRole(newRole);
                if (incoming.getPassword() != null && !incoming.getPassword().isBlank()) {
                    u.setPassword(passwordEncoder.encode(incoming.getPassword()));
                }
                u.setEmail(newEmail);

                User saved = userRepository.save(u);

                // Sync registration only for Admin/Faculty
                if ("Admin".equalsIgnoreCase(newRole) || "Faculty".equalsIgnoreCase(newRole)) {
                    Registration reg = registrationRepository.findByEmailIgnoreCase(newEmail)
                            .orElseGet(Registration::new);
                    reg.setEmail(newEmail);
                    reg.setRole(newRole);
                    reg.setDepartment(incoming.getDepartment() != null ? incoming.getDepartment() : u.getDepartment());
                    reg.setExperience(incoming.getExperience() != null ? incoming.getExperience() : reg.getExperience());
                    registrationRepository.save(reg);
                } else {
                    // ensure no registration row for students
                    registrationRepository.deleteByEmailIgnoreCase(newEmail);
                }

                return ResponseEntity.ok(saved);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}


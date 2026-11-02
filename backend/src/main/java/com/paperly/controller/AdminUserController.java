package com.paperly.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.paperly.dto.UserRequest;
import com.paperly.dto.UserRoleRequest;
import com.paperly.dto.UserResponse;
import com.paperly.entity.User;
import com.paperly.entity.UserRole;
import com.paperly.exception.ApiException;
import com.paperly.repository.GeneratedPaperRepository;
import com.paperly.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final UserRepository userRepository;
    private final GeneratedPaperRepository generatedPaperRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(UserRepository userRepository,
                               GeneratedPaperRepository generatedPaperRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.generatedPaperRepository = generatedPaperRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
            .sorted((first, second) -> first.getUsername().compareToIgnoreCase(second.getUsername()))
            .map(this::toResponse)
            .toList();
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        String username = request.username().trim();
        if (userRepository.findByUsernameIgnoreCase(username).isPresent()) {
            throw new ApiException("A user with this username already exists.");
        }

        User user = userRepository.save(new User(username, passwordEncoder.encode(request.password()), request.role()));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    @PutMapping("/{id}/role")
    public UserResponse updateRole(@PathVariable Long id,
                                   @Valid @RequestBody UserRoleRequest request,
                                   Principal principal) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ApiException("User not found with id: " + id));
        if (user.getUsername().equals(principal.getName())) {
            throw new ApiException("You cannot change your own account role.");
        }
        if (user.getRole() == UserRole.ADMIN && request.role() != UserRole.ADMIN
            && userRepository.countByRole(UserRole.ADMIN) <= 1) {
            throw new ApiException("The last administrator account cannot be demoted.");
        }
        user.setRole(request.role());
        return toResponse(userRepository.save(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Principal principal) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ApiException("User not found with id: " + id));
        if (user.getUsername().equals(principal.getName())) {
            throw new ApiException("You cannot delete your own account.");
        }
        if (user.getRole() == UserRole.ADMIN && userRepository.countByRole(UserRole.ADMIN) <= 1) {
            throw new ApiException("The last administrator account cannot be deleted.");
        }
        if (generatedPaperRepository.existsByGeneratedBy(user)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "This user has generated papers and cannot be deleted.");
        }

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }
}

package com.paperly;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.paperly.entity.User;
import com.paperly.entity.UserRole;
import com.paperly.repository.UserRepository;
import com.paperly.security.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Test
    void adminApiRequiresAuthenticationAndAdminRole() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/users").with(user("faculty").roles("FACULTY")))
            .andExpect(status().isForbidden());
    }

    @Test
    void facultyCannotManageSubjects() throws Exception {
        mockMvc.perform(post("/api/subjects")
                .with(user("faculty").roles("FACULTY"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Forbidden subject\",\"description\":\"\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void jwtFacultyIsAuthenticatedButForbiddenFromAdminEndpoints() throws Exception {
        String username = "jwt-faculty-test";
        userRepository.save(new User(username, passwordEncoder.encode("password123"), UserRole.FACULTY));
        String token = jwtService.generateToken(username, UserRole.FACULTY.name());

        mockMvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }

    @Test
    void administratorCanCreateSubjects() throws Exception {
        mockMvc.perform(post("/api/subjects")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Authorization test subject\",\"description\":\"Role test\"}"))
            .andExpect(status().isOk());
    }
}

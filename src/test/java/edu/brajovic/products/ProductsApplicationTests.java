package edu.brajovic.products;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;


import edu.brajovic.products.data.UserDataService;
import edu.brajovic.products.data.UsersRepository;
import edu.brajovic.products.models.UserEntity;
import edu.brajovic.products.models.UserModel;

@SpringBootTest
@AutoConfigureMockMvc
class ProductsApplicationTests {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserDataService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    /**
     * ✅ findByUsername returns users correctly
     */
    @Test
    void findByUsernameReturnsUser() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole("ROLE_USER");

        usersRepository.save(user);

        UserEntity found = usersRepository.findByUsername("testuser");

        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
    }

    /**
     * Registration persists users
     */
    @Test
    void registrationPersistsUser() {
        UserModel user = new UserModel();
        user.setUsername("newuser");
        user.setPassword("plaintext");
        user.setRole("ROLE_USER");

        userService.create(user);

        UserEntity saved = usersRepository.findByUsername("newuser");
        assertNotNull(saved);
    }

    /**
     * Admin users can access admin routes
     */
    @Test
    void adminUserCanAccessAdminRoutes() throws Exception {

        UserEntity admin = new UserEntity();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("adminpass"));
        admin.setRole("ROLE_ADMIN");
        usersRepository.save(admin);

        mockMvc.perform(
                formLogin("/login")
                        .user("admin")
                        .password("adminpass")
        ).andExpect(authenticated());

        mockMvc.perform(get("/admin/users")
                .with(org.springframework.security.test.web.servlet.request
                        .SecurityMockMvcRequestPostProcessors.user("admin")
                        .roles("ADMIN")))
                .andExpect(status().isOk());
    }

}

package edu.brajovic.products.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.brajovic.products.data.UserDataService;
import edu.brajovic.products.models.UserModel;

@Controller
public class AuthController {

    private final UserDataService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserDataService userService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String displayLogin(Model model,
                         @RequestParam(value = "error", required = false) String error,
                         @RequestParam(value = "logout", required = false) String logout) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }

        if (logout != null) {
            model.addAttribute("message", "You have been successfully logged out");
        }
        return "login";
    }

    @GetMapping("/register")
    public String display(Model model) {
        model.addAttribute("userModel", new UserModel());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("userModel") UserModel user,
                        @RequestParam String confirmPassword,
                        Model model) {

        // Check for empty fields
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()) {
            model.addAttribute("error", "Username and password are required");
            return "register";
        }

        // Check password confirmation
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        // Check if username already exists
        if (userService.getByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        // Encode password and set role
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");

        // Save user
        userService.create(user);

        return "redirect:/login";
    }


}

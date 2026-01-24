package edu.brajovic.products.controller;

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
    public String register(@ModelAttribute("userModel") UserModel user, @RequestParam String confirmPassword, Model model) 
    {

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userService.create(user);

        model.addAttribute("success", "Account created successfully!");
        return "redirect:/login";
    }
}

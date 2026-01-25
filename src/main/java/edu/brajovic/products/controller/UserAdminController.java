package edu.brajovic.products.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.brajovic.products.data.UserDataService;
import edu.brajovic.products.models.UserModel;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController {
    
    private final UserDataService userService;

    public UserAdminController(UserDataService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String userAdmin(Model model) {
        model.addAttribute("users", userService.getAll());
        return "userAdmin";
    }
    
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable int id, Model model) {
        model.addAttribute("user", userService.getById(id));
        return "editUser";
    }

    @PostMapping("/edit")
public String editUser(@ModelAttribute("user") UserModel formUser) {

    // Load existing user from DB
    UserModel existingUser = userService.getById(formUser.getId());

    // Update only editable fields
    existingUser.setUsername(formUser.getUsername());
    existingUser.setRole(formUser.getRole());

    // Save updated user
    userService.update(existingUser);

    return "redirect:/admin/users";
}


    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable int id, Model model) {
        UserModel user = userService.getById(id);
        model.addAttribute("user", user);
        return "admin/deleteUser";
    }
    
    @PostMapping("/delete")
    public String deleteUser(@RequestParam int id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }
    
}

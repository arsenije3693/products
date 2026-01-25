package edu.brajovic.products.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        try {
            model.addAttribute("users", userService.getAll());
        } catch (Exception ex) {
            model.addAttribute("error", "Failed to load users. Please try again later.");
            ex.printStackTrace(); 
        }
        return "userAdmin";
    }
    
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable int id, Model model) {
        try {
            UserModel user = userService.getById(id);
            if (user == null) {
                model.addAttribute("error", "User not found");
                return "redirect:/admin/users";
            }
            model.addAttribute("user", user);
        } catch (Exception ex) {
            model.addAttribute("error", "Failed to load user. Please try again.");
            ex.printStackTrace();
            return "redirect:/admin/users";
        }
        return "editUser";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute("user") UserModel formUser, Model model) {
        try {
            UserModel existingUser = userService.getById(formUser.getId());
            if (existingUser == null) {
                model.addAttribute("error", "User not found");
                return "redirect:/admin/users";
            }

            
            existingUser.setUsername(formUser.getUsername());
            existingUser.setRole(formUser.getRole());

            userService.update(existingUser);
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("error", "Username already exists or invalid data");
            return "editUser";
        } catch (Exception ex) {
            model.addAttribute("error", "Failed to update user. Please try again.");
            ex.printStackTrace();
            return "editUser";
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable int id, Model model) {
        try {
            UserModel user = userService.getById(id);
            if (user == null) {
                model.addAttribute("error", "User not found");
                return "redirect:/admin/users";
            }
            model.addAttribute("user", user);
        } catch (Exception ex) {
            model.addAttribute("error", "Failed to load user for deletion. Please try again.");
            ex.printStackTrace();
            return "redirect:/admin/users";
        }
        return "confirmDeleteUser";
    }
    
    @PostMapping("/delete")
    public String deleteUser(@RequestParam int id, Model model) {
        try {
            UserModel user = userService.getById(id);
            if (user == null) {
                model.addAttribute("error", "User not found");
                return "redirect:/admin/users";
            }

            userService.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("error", "Cannot delete user due to database constraints");
            return "redirect:/admin/users";
        } catch (Exception ex) {
            model.addAttribute("error", "Failed to delete user. Please try again.");
            ex.printStackTrace();
            return "redirect:/admin/users";
        }
        return "redirect:/admin/users";
    }
}

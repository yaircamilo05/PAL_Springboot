package com.example.pal.controller;

import com.example.pal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public String createUser(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String[] roles) {
        userService.createUserWithRoles(username, password, roles);
        return "User created successfully!";
    }
}

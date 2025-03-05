package com.example.pal.controller;

import com.example.pal.dto.UserDTO;
import com.example.pal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public String createUser(@RequestBody UserDTO userDTO) {
        userService.createUserWithRoles(
            userDTO.getUsername(),
            userDTO.getPassword(),
            userDTO.getRoles().toArray(new String[0])
        );
        return "User created successfully!";
    }
}

package com.example.pal.controller;

import com.example.pal.dto.UserDTO;
import com.example.pal.dto.UserResponseDTO;
import com.example.pal.model.User;
import com.example.pal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/all")
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users.stream()
            .map(user -> new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());
    }
}

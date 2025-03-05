package com.example.pal.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserDTO {
    private String username;
    private String password;
    private List<String> roles;
} 
package com.example.pal.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private Set<RoleDTO> roles;
} 
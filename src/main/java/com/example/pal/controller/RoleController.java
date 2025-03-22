package com.example.pal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pal.dto.RoleDTO;
import com.example.pal.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> getAllUsers(){
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}

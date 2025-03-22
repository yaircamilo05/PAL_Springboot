package com.example.pal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;

import com.example.pal.dto.RoleDTO;
import com.example.pal.model.Role;
import com.example.pal.repository.RoleRepository;


@Service
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public RoleService(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

     @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream() .map(role -> modelMapper.map(role, RoleDTO.class)).toList();
    }
    
}

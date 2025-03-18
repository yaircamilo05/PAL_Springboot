package com.example.pal.service;
import org.modelmapper.ModelMapper;
import com.example.pal.dto.CreateUserDTO;
import com.example.pal.dto.UserResponseDTO;
import com.example.pal.model.Role;
import com.example.pal.model.User;
import com.example.pal.repository.RoleRepository;
import com.example.pal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, 
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder,
                      ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public User createUserWithRoles(CreateUserDTO createUserDTO) {
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));

        Set<Role> roles = new HashSet<>();
        for (String roleName : createUserDTO.getRoles()) {
            Optional<Role> roleOpt = Optional.ofNullable(roleRepository.findByName(roleName)); //Sugerencia de Java
            Role role = roleOpt.orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName(roleName);
                return roleRepository.save(newRole);
            });
            roles.add(role);
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
    	return users.stream().map(user->modelMapper.map(user, UserResponseDTO.class)).toList();

    }
    
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        return modelMapper.map(user, UserResponseDTO.class);
    }
    
    
    public UserResponseDTO updateUser(Long id, CreateUserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        
        user.setUsername(userDTO.getUsername());
        
        if(userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        if(userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : userDTO.getRoles()) {
                Role role = roleRepository.findByName(roleName);
                if(role == null) {
                    role = new Role();
                    role.setName(roleName);
                    role = roleRepository.save(role);
                }
                roles.add(role);
            }
            user.setRoles(roles);
        }
        
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }
    
    public Map<String, String> deleteUser(Long id) {
        userRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Usuario eliminado exitosamente");
        return response;
    }

    public List<UserResponseDTO> getUsersByRole(String role) {
        List<User> users = userRepository.findByRolesName(role);
        return users.stream().map(user->modelMapper.map(user, UserResponseDTO.class)).toList();
    }


}
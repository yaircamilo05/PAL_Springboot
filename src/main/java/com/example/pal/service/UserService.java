package com.example.pal.service;
import org.modelmapper.ModelMapper;
import com.example.pal.dto.CreateUserDTO;
import com.example.pal.dto.UserResponseDTO;
import com.example.pal.model.Role;
import com.example.pal.model.User;
import com.example.pal.repository.RoleRepository;
import com.example.pal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

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
    public Optional<UserResponseDTO> getUserById(Long id){
    return userRepository.findById(id).map(user -> modelMapper.map(user, UserResponseDTO.class));
    }
    
    public User updateUser(Long id, User userDetails) {
    	User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found!"));
    	user.setUsername(userDetails.getUsername());
    	if(user.getPassword()!=null) {
    		user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
    	}
    	user.setRoles(userDetails.getRoles());
    	return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
    	userRepository.deleteById(id);
    }
}
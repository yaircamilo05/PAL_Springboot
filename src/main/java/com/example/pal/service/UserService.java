package com.example.pal.service;

import com.example.pal.model.Role;
import com.example.pal.model.User;
import com.example.pal.repository.RoleRepository;
import com.example.pal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUserWithRoles(String username, String password, String[] roleNames) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
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
    
    public List<User> getAllUsers() {
    	return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id){
    	return userRepository.findById(id);
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
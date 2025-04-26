package com.example.pal.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pal.dto.AuthRequest;
import com.example.pal.dto.AuthResponse;
import com.example.pal.dto.UserResponseDTO;
import com.example.pal.repository.UserRepository;
import com.example.pal.security.JwtUtil;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ModelMapper modelMapper;


    @Transactional(readOnly = true)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        return userRepo.findByUsername(req.getUsername())
                .filter(user -> passwordEncoder.matches(req.getPassword(), user.getPassword()))
                .map(user -> {
                    UserResponseDTO userDTO =  modelMapper.map(user, UserResponseDTO.class);
                    String token = jwtUtil.generateToken(userDTO);
                    return ResponseEntity.ok(new AuthResponse(token));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("Invalid username or password")));
    }

}
package com.example.pal.controller;

import com.example.pal.dto.AuthRequest;
import com.example.pal.dto.AuthResponse;
import com.example.pal.dto.UserResponseDTO;
import com.example.pal.repository.UserRepository;
import com.example.pal.security.GoogleTokenVerifierService;
import com.example.pal.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @Autowired
    private GoogleTokenVerifierService googleVerifier;

    @Transactional(readOnly = true)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        return userRepo.findByUsername(req.getUsername())
                .filter(user -> passwordEncoder.matches(req.getPassword(), user.getPassword()))
                .map(user -> {
                    UserResponseDTO userDTO = modelMapper.map(user, UserResponseDTO.class);
                    String token = jwtUtil.generateToken(userDTO);
                    return ResponseEntity.ok(new AuthResponse(token));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("Invalid username or password")));
    }

    @PostMapping("/auth/google")
    public ResponseEntity<Map<String, String>> loginWithGoogle(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            GoogleIdToken.Payload payload = googleVerifier.verify(token);

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // Simular roles (puedes obtenerlos de tu base de datos si deseas)
            List<Map<String, Object>> roles = new ArrayList<>();
            roles.add(Map.of("id", 2, "name", "STUDENT"));

            String jwt = Jwts.builder()
                    .setSubject(email)
                    .claim("id", new Random().nextInt(10000)) // ID simulado
                    .claim("roles", roles)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hora
                    .signWith(SignatureAlgorithm.HS256, "ESTO_ES_UN_SECRETO_SUPER_SEGURITO".getBytes())
                    .compact();

            return ResponseEntity.ok(Map.of("jwt", jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }
    }
}

package com.example.pal.security;

import com.example.pal.model.Role;
import com.example.pal.model.User;
import com.example.pal.repository.RoleRepository;
import com.example.pal.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();

        // 📌 Extraer correo y nombre con compatibilidad para Google y GitHub
        String username = (String) attributes.get("email");
        String fullName = (String) attributes.get("name");

        // Para GitHub: si no tiene email público, usar login como correo temporal
        if (username == null && attributes.containsKey("login")) {
            username = attributes.get("login") + "@github.com";
        }

        // Si fullName no está disponible, usar login
        if (fullName == null && attributes.containsKey("login")) {
            fullName = (String) attributes.get("login");
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user;

        if (optionalUser.isPresent()) {
            // ✅ Recargar el usuario con los roles
            user = userRepository.findByUsernameWithRoles(username)
                    .orElseThrow(() -> new RuntimeException("No se pudo cargar el usuario con roles"));
        } else {
            // Buscar el rol 'student'
            Role studentRole = roleRepository.findByName("student")
                    .orElseThrow(() -> new RuntimeException("El rol 'student' no existe en la base de datos"));

            // Crear el nuevo usuario
            user = new User();
            user.setUsername(username);
            user.setPassword(null); // Usuario externo → sin contraseña local
            user.setFullName(fullName);

            // Guardar el usuario
            user = userRepository.save(user);

            // Asignar el rol y guardar de nuevo
            user.setRoles(Collections.singleton(studentRole));
            user = userRepository.save(user);
        }

        // Generar el token con roles incluidos
        String token = jwtUtil.generateToken(user);

        // Redirigir al frontend con el token
        response.sendRedirect("http://localhost:5173/login?token=" + token);
    }
}

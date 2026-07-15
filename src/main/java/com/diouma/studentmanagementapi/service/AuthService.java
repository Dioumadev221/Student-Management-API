package com.diouma.studentmanagementapi.service;

import com.diouma.studentmanagementapi.dto.AuthResponse;
import com.diouma.studentmanagementapi.dto.LoginRequest;
import com.diouma.studentmanagementapi.dto.RegisterRequest;
import com.diouma.studentmanagementapi.entity.Role;
import com.diouma.studentmanagementapi.entity.User;
import com.diouma.studentmanagementapi.exception.BadRequestException;
import com.diouma.studentmanagementapi.exception.DuplicateResourceException;
import com.diouma.studentmanagementapi.repository.UserRepository;
import com.diouma.studentmanagementapi.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;


    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request == null || request.username() == null || request.username().isBlank()) {
            throw new BadRequestException("Le nom d'utilisateur est obligatoire.");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("Le mot de passe est obligatoire.");
        }
        String username = request.username().trim();
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Le nom d'utilisateur '" + username + "' est deja pris.");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        return authentifierEtGenererToken(username, request.password());
    }

    /**
     * Connecte un utilisateur et renvoie un token.
     * En cas d'identifiants invalides, Spring Security leve une AuthenticationException (-> 401).
     */
    public AuthResponse login(LoginRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            throw new BadRequestException("Le nom d'utilisateur et le mot de passe sont obligatoires.");
        }
        return authentifierEtGenererToken(request.username().trim(), request.password());
    }

    private AuthResponse authentifierEtGenererToken(String username, String rawPassword) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, rawPassword));

        String token = jwtUtils.generateToken(authentication);
        String role = jwtUtils.extractRole(token);
        return AuthResponse.bearer(token, username, role);
    }
}

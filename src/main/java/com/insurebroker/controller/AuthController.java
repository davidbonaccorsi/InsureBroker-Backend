package com.insurebroker.controller;

import com.insurebroker.dto.request.LoginRequest;
import com.insurebroker.dto.response.AuthResponse;
import com.insurebroker.entity.Broker;
import com.insurebroker.entity.User;
import com.insurebroker.entity.UserRoleEntity;
import com.insurebroker.repository.BrokerRepository;
import com.insurebroker.repository.UserRepository;
import com.insurebroker.repository.UserRoleRepository;
import com.insurebroker.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final BrokerRepository brokerRepository;
    private final JwtTokenProvider tokenProvider;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found")
        );

        UserRoleEntity roleEntity = userRoleRepository.findByUserId(user.getId()).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("User has no roles")
        );

        Broker broker = brokerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Broker profile not found for user")
        );

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String jwt = tokenProvider.generateToken(authentication, broker.getId(), roleEntity.getRole().name());

        AuthResponse.UserDto userDto = AuthResponse.UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(roleEntity.getRole().name())
                .brokerId(broker.getId())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();

        return ResponseEntity.ok(AuthResponse.builder()
                .token(jwt)
                .refreshToken(jwt)
                .expiresIn(jwtExpirationInMs / 1000)
                .user(userDto)
                .build());
    }
}
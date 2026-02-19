package com.insurebroker.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AuthResponse {
    private UserDto user;
    private String token;
    private String refreshToken;
    private long expiresIn;

    @Data
    @Builder
    public static class UserDto {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String role;
        private Long brokerId;
        private Boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime lastLogin;
    }
}
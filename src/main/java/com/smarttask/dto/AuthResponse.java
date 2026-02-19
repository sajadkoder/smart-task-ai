package com.smarttask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type;
    private Long userId;
    private String username;
    private String email;
    private String fullName;

    public static AuthResponse builder() {
        return new AuthResponse();
    }

    public AuthResponse token(String token) {
        this.token = token;
        return this;
    }

    public AuthResponse type(String type) {
        this.type = type;
        return this;
    }

    public AuthResponse userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public AuthResponse username(String username) {
        this.username = username;
        return this;
    }

    public AuthResponse email(String email) {
        this.email = email;
        return this;
    }

    public AuthResponse fullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public AuthResponse build() {
        AuthResponse response = new AuthResponse();
        response.token = this.token;
        response.type = this.type;
        response.userId = this.userId;
        response.username = this.username;
        response.email = this.email;
        response.fullName = this.fullName;
        return response;
    }
}

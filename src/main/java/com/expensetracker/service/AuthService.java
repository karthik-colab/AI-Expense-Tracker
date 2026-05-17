package com.expensetracker.service;

import com.expensetracker.config.SupabaseConfig;
import com.expensetracker.dto.AuthRequest;
import com.expensetracker.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SupabaseConfig supabaseConfig;

    private WebClient authClient() {
        return WebClient.builder()
                .baseUrl(supabaseConfig.getSupabaseUrl() + "/auth/v1")
                .defaultHeader("apikey", supabaseConfig.getSupabaseAnonKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /** Register a new user via Supabase Auth */
    public AuthResponse register(AuthRequest request) {
        try {
            Map<?, ?> response = authClient().post()
                    .uri("/signup")
                    .bodyValue(Map.of(
                            "email",    request.getEmail(),
                            "password", request.getPassword()
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Supabase returns 200 even for signup — check if user is in response
            return buildAuthResponse(response,
                    "Account created! You can now sign in.");

        } catch (WebClientResponseException e) {
            // Parse error message from Supabase body e.g. {"msg":"...","code":...}
            String body = e.getResponseBodyAsString();
            String msg  = extractSupabaseError(body, e.getMessage());
            throw new RuntimeException(msg);
        }
    }

    /** Login an existing user via Supabase Auth */
    public AuthResponse login(AuthRequest request) {
        try {
            Map<?, ?> response = authClient().post()
                    .uri("/token?grant_type=password")
                    .bodyValue(Map.of(
                            "email",    request.getEmail(),
                            "password", request.getPassword()
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return buildAuthResponse(response, "Login successful!");

        } catch (WebClientResponseException e) {
            String body = e.getResponseBodyAsString();
            String msg  = extractSupabaseError(body, "Invalid email or password");
            throw new RuntimeException(msg);
        }
    }

    private String extractSupabaseError(String body, String fallback) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<?, ?> map = mapper.readValue(body, java.util.Map.class);
            if (map.containsKey("msg")) return String.valueOf(map.get("msg"));
            if (map.containsKey("message")) return String.valueOf(map.get("message"));
            if (map.containsKey("error_description")) return String.valueOf(map.get("error_description"));
            if (map.containsKey("error_code")) return String.valueOf(map.get("error_code"));
            if (map.containsKey("code")) return "Error code: " + map.get("code");
        } catch (Exception ignored) {}
        return fallback;
    }

    @SuppressWarnings("unchecked")
    private AuthResponse buildAuthResponse(Map<?, ?> response, String message) {
        if (response == null) {
            throw new RuntimeException("No response from Supabase Auth");
        }

        String accessToken = (String) response.get("access_token");
        Map<String, Object> user = (Map<String, Object>) response.get("user");

        String userId = null;
        String email  = null;
        if (user != null) {
            userId = (String) user.get("id");
            email  = (String) user.get("email");
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(userId)
                .email(email)
                .message(message)
                .build();
    }
}

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

    /** Extract a human-readable message from Supabase error JSON */
    private String extractSupabaseError(String body, String fallback) {
        try {
            // Supabase error body: {"code":400,"msg":"Invalid login credentials","error_code":"invalid_credentials"}
            if (body.contains("\"msg\"")) {
                int start = body.indexOf("\"msg\"") + 6;
                int colon = body.indexOf(":", start);
                int quote1 = body.indexOf("\"", colon);
                int quote2 = body.indexOf("\"", quote1 + 1);
                if (quote1 >= 0 && quote2 > quote1)
                    return body.substring(quote1 + 1, quote2);
            }
            if (body.contains("\"error_description\"")) {
                int start = body.indexOf("\"error_description\"") + 19;
                int colon = body.indexOf(":", start);
                int q1 = body.indexOf("\"", colon);
                int q2 = body.indexOf("\"", q1 + 1);
                if (q1 >= 0 && q2 > q1)
                    return body.substring(q1 + 1, q2);
            }
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

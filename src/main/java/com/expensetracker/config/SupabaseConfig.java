package com.expensetracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SupabaseConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon.key}")
    private String supabaseAnonKey;

    @Value("${supabase.rest.url}")
    private String restUrl;

    /**
     * WebClient pre-configured for Supabase REST API calls.
     * Uses the anon key suitable for authenticated-user operations (RLS enforced).
     */
    @Bean
    public WebClient supabaseWebClient() {
        return WebClient.builder()
                .baseUrl(restUrl)
                .defaultHeader("apikey", supabaseAnonKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public String getSupabaseUrl()     { return supabaseUrl; }
    public String getSupabaseAnonKey() { return supabaseAnonKey; }
    public String getRestUrl()         { return restUrl; }
}

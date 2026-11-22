package com.bazaarehunar.config;

import com.bazaarehunar.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Without this Spring answers 403 for a missing/expired token, so the panel never logged out.
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, ex) -> writeJson(response, 401, "Please sign in again"))
                        .accessDeniedHandler((request, response, ex) -> writeJson(response, 403, "You do not have permission to do this")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/health", "/api/auth/**", "/api/categories", "/api/categories/**", "/api/patterns/**", "/api/cart/preview").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/artisans/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/custom-orders", "/api/contact").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static void writeJson(jakarta.servlet.http.HttpServletResponse response, int status, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\"}");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.frontend.origin}") String frontendOrigin,
            @Value("${app.admin.origin:http://localhost:5174}") String adminOrigin) {
        CorsConfiguration config = new CorsConfiguration();
        // Storefront (5173) and the separate admin panel (5174) may call this API.
        java.util.List<String> origins = new java.util.ArrayList<>(java.util.stream.Stream.concat(
                java.util.Arrays.stream(frontendOrigin.split(",")), java.util.Arrays.stream(adminOrigin.split(",")))
                .map(String::trim).filter(o -> !o.isEmpty()).distinct().toList());
        // http://localhost:5174 and http://127.0.0.1:5174 are different origins for the browser: allow both.
        for (String origin : List.copyOf(origins)) {
            if (origin.contains("://localhost")) origins.add(origin.replace("://localhost", "://127.0.0.1"));
        }
        config.setAllowedOrigins(origins.stream().distinct().toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

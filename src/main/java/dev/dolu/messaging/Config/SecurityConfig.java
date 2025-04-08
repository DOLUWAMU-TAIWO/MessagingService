package dev.dolu.messaging.Config;

import dev.dolu.messaging.filter.ApiKeyFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    private final ApiKeyFilter apiKeyFilter;

    public SecurityConfig(ApiKeyFilter apiKeyFilter) {
        this.apiKeyFilter = apiKeyFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/actuator/prometheus")).permitAll() // ✅ Explicitly permit all for Prometheus
                        .requestMatchers(new AntPathRequestMatcher("/actuator/health")).permitAll()     // ✅ Explicitly permit all for health
                        .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()       // ✅ Allow other actuator endpoints (optional, be cautious in production)
                        .requestMatchers(new AntPathRequestMatcher("/api/email/health")).permitAll()   // ✅ Allow health check endpoint
                        .anyRequest().authenticated()
                )
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class) // ✅ Register Filter Here
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    // ... PasswordEncoder bean ...
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

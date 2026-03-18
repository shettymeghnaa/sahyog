package org.hartford.mutualinsurance.config;

import org.hartford.mutualinsurance.security.CustomUserDetailsService;
import org.hartford.mutualinsurance.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtFilter jwtFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth

                        // H2 console (dev only)
                        .requestMatchers("/h2-console/**").permitAll()

                        // Public static resources
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/auth/**").permitAll()

                        // Authentication (login, member register)
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register/claim-officer").hasRole("ADMIN")

                        // Member self-registration — open (admin creates regions, members register themselves)
                        .requestMatchers("/api/members/region/**").permitAll()

                        // Region list — public (used in registration form)
                        .requestMatchers("/api/regions").permitAll()

                        // Active disasters — members and claim officers can see them
                        .requestMatchers("/api/disasters/active/**").hasAnyRole("MEMBER", "CLAIM_OFFICER")

                        // Contributions — members only
                        .requestMatchers("/api/contributions/**").hasRole("MEMBER")

                        // All other API calls require authentication
                        .requestMatchers("/api/**").authenticated()

                        .anyRequest().permitAll()
                )
                .userDetailsService(userDetailsService)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
package com.lender.mortgage.config;

import com.lender.mortgage.security.CustomUserDetailsService;
import com.lender.mortgage.security.JwtAuthenticationEntryPoint;
import com.lender.mortgage.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                
                // User management endpoints
                .requestMatchers(HttpMethod.POST, "/api/users").hasAnyRole("MANAGER", "UNDERWRITER")
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("MANAGER", "UNDERWRITER", "PROCESSOR")
                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("MANAGER", "UNDERWRITER")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("MANAGER")
                
                // Loan management endpoints
                .requestMatchers(HttpMethod.POST, "/api/loans").hasAnyRole("LOAN_OFFICER", "PROCESSOR", "UNDERWRITER", "MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/loans/borrower").hasRole("BORROWER")
                .requestMatchers(HttpMethod.GET, "/api/loans/**").hasAnyRole("LOAN_OFFICER", "PROCESSOR", "UNDERWRITER", "MANAGER", "BORROWER")
                .requestMatchers(HttpMethod.PUT, "/api/loans/*/status").hasAnyRole("PROCESSOR", "UNDERWRITER", "MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/loans/**").hasAnyRole("LOAN_OFFICER", "PROCESSOR", "UNDERWRITER", "MANAGER")
                
                // Condition management endpoints
                .requestMatchers(HttpMethod.POST, "/api/conditions").hasAnyRole("PROCESSOR", "UNDERWRITER", "MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/conditions/**").hasAnyRole("LOAN_OFFICER", "PROCESSOR", "UNDERWRITER", "MANAGER", "BORROWER")
                .requestMatchers(HttpMethod.PUT, "/api/conditions/**").hasAnyRole("PROCESSOR", "UNDERWRITER", "MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/conditions/**").hasAnyRole("UNDERWRITER", "MANAGER")
                
                // Document management endpoints
                .requestMatchers(HttpMethod.POST, "/api/documents/**").hasAnyRole("LOAN_OFFICER", "PROCESSOR", "UNDERWRITER", "MANAGER", "BORROWER")
                .requestMatchers(HttpMethod.GET, "/api/documents/**").hasAnyRole("LOAN_OFFICER", "PROCESSOR", "UNDERWRITER", "MANAGER", "BORROWER")
                .requestMatchers(HttpMethod.PUT, "/api/documents/**").hasAnyRole("LOAN_OFFICER", "PROCESSOR", "UNDERWRITER", "MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/documents/**").hasAnyRole("PROCESSOR", "UNDERWRITER", "MANAGER")
                
                // Dashboard endpoints
                .requestMatchers("/api/dashboard/**").hasAnyRole("LOAN_OFFICER", "PROCESSOR", "UNDERWRITER", "MANAGER")
                
                // All other requests must be authenticated
                .anyRequest().authenticated()
            );
        
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

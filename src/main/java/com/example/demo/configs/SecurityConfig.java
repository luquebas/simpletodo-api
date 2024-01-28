package com.example.demo.configs;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.security.JWTAuthenticationFilter;
import com.example.demo.security.JWTUtil;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;


    @Autowired
    private UserDetailsService userDetailsService;
    
    private static final String[] PUBLIC_MATCHERS = {
        "/"
    };

    private static final String[] PUBLIC_MATCHERS_POST = {
        "/user",
        "/login"
    };

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable());

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(this.userDetailsService).passwordEncoder(bCryptPasswordEncoder());
        this.authenticationManager = authenticationManagerBuilder.build();

        http.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll()
        .requestMatchers(PUBLIC_MATCHERS).permitAll()
        .anyRequest().authenticated()).authenticationManager(authenticationManager);

        http.addFilter(new JWTAuthenticationFilter(this.authenticationManager, this.jwtUtil));

        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); 
        return source;
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

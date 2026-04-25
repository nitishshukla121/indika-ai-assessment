//package com.example.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http.csrf(AbstractHttpConfigurer::disable)
////            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
////            .authorizeHttpRequests(auth -> auth
////                .requestMatchers("/api/media/**", "/api/chat/**").permitAll()
////                .anyRequest().authenticated()
////            );
////        return http.build();
////    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable()) // Crucial: Allows POST requests for your file upload
//            .cors(cors -> cors.configure(http)) // Enables the @CrossOrigin annotation we just added
//            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Permits everything
//
//        return http.build();
//    }
//
//    @Bean
//    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of("http://localhost:5173")); 
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("*"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
//}
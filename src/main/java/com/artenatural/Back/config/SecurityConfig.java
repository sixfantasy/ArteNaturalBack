package com.artenatural.Back.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${app.local-domain-front}")
    private String localDomainFront;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF since we are using JWT (stateless)
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                // Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login",
                                "/auth/register","/Images/**",
                                "/uploads/list/all").permitAll() // Permit access to specific endpoints
                        //  Rutas públicas por método HTTP: cualquier GET a /products/...
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()

                        //  Solo artistas pueden subir imágenes
                        .requestMatchers("/uploads/upload").hasAuthority("ARTIST")

                        //  Solo artistas pueden gestionar productos (crear, editar, borrar)
                        .requestMatchers(HttpMethod.POST, "/products").hasAuthority("ARTIST")
                        .requestMatchers(HttpMethod.PUT, "/products").hasAuthority("ARTIST")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAuthority("ARTIST")

                        //  Cualquier usuario autenticado (CLIENT o ARTIST) puede comprar
                        .requestMatchers(HttpMethod.POST, "/purchases").authenticated()

                        // 🔒 Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class).httpBasic(withDefaults());

        return http.build();
    }

    // ✅ CHANGE 4: Simplified CORS Configuration
    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Chained methods for better readability
                registry.addMapping("/**")
                        .allowedOrigins(localDomainFront)
                        .allowedMethods("POST", "PUT", "GET", "DELETE", "OPTIONS");
            }
        };
    }
}
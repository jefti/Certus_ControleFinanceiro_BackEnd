package com.projeto.financeiro.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(1)
@RequiredArgsConstructor
public class ActuatorSecurityConfig {

    private final PasswordEncoder passwordEncoder;

    @Value("${app.actuator.username}")
    private String actuatorUsername;

    @Value("${app.actuator.password}")
    private String actuatorPassword;

    @Bean
    @Order(1)
    public SecurityFilterChain actuatorFilterChain(HttpSecurity http) throws Exception {
        InMemoryUserDetailsManager metricsUsers = new InMemoryUserDetailsManager(
            User.withUsername(actuatorUsername)
                .password(passwordEncoder.encode(actuatorPassword))
                .roles("ACTUATOR")
                .build()
        );

        http
            .securityMatcher("/actuator/**")
            .userDetailsService(metricsUsers)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers("/actuator/prometheus").hasRole("ACTUATOR")
                .anyRequest().denyAll()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}

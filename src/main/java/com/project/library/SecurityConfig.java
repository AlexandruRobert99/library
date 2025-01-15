package com.project.library;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {

    private final AdminDetailsService adminDetailsService;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(AdminDetailsService adminDetailsService, UserDetailsServiceImpl userDetailsService) {
        this.adminDetailsService = adminDetailsService;
        this.userDetailsService = userDetailsService;
    }

    // 🔐 Configurare securitate pentru ADMIN
    @Bean
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")  // Se aplică doar pe /admin/**
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login", "/admin/register").permitAll()
                        .anyRequest().hasRole("ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/admin/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf().disable();

        return http.build();
    }

    // 🔐 Configurare securitate pentru USER
    @Bean
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/user/**")  // Se aplică doar pe /user/**
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login", "/user/register").permitAll()
                        .anyRequest().hasRole("USER")
                )
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/login")
                        .defaultSuccessUrl("/user/dashboard", true)
                        .failureUrl("/user/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/user/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf().disable();

        return http.build();
    }

    // 🛠️ Configurare AuthenticationManager pentru ambele servicii
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

        builder.userDetailsService(adminDetailsService)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());

        builder.userDetailsService(userDetailsService)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());

        return builder.build();
    }


}

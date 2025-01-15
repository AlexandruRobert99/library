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
    private final UserDetailsServiceImpl userDetailsService;  // ➡️ Adăugat pentru utilizatori

    public SecurityConfig(AdminDetailsService adminDetailsService, UserDetailsServiceImpl userDetailsService) {
        this.adminDetailsService = adminDetailsService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 🔒 Rutele Admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 🔒 Rutele User
                        .requestMatchers("/user/**").hasRole("USER")

                        // 🔓 Rutele publice
                        .anyRequest().permitAll()
                )
                // 🔐 Login pentru Admin
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .permitAll()
                )
                // 🔐 Login pentru User
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .defaultSuccessUrl("/user/dashboard", true)
                        .permitAll()
                )
                // 🚪 Logout pentru Admin
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // 🚪 Logout pentru User
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

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                // 🔐 Configurare Admin
                .userDetailsService(adminDetailsService)
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                // 🔐 Configurare User
                .and()
                .userDetailsService(userDetailsService)
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .and()
                .build();
    }
}

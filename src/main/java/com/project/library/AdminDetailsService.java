package com.project.library;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    public AdminDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsernameIgnoreCase(username);
        if (admin == null) {
            throw new UsernameNotFoundException("Adminul nu a fost găsit!");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(admin.getUsername())
                .password("{noop}" + admin.getPassword())  // {noop} => fără criptare
                .roles("ADMIN")  // Setăm rolul ADMIN
                .build();
    }
}

package com.project.library;

import com.project.library.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Autentificare: verificare username și parolă (ignorând literele mari/mici)
    Admin findByUsernameIgnoreCaseAndPassword(String username, String password);

    // Verificare existență username (pentru a preveni duplicarea conturilor de admin)
    Admin findByUsernameIgnoreCase(String username);


}

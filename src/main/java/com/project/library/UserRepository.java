package com.project.library;

import com.project.library.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Pentru autentificare: verificare username și parolă (fără diferență între litere mari/mici)
    User findByUsernameIgnoreCaseAndPassword(String username, String password);

    // Verifică dacă un username există deja (ignora literele mari/mici)
    User findByUsernameIgnoreCase(String username);

    // Verifică dacă un email există deja (ignora literele mari/mici)
    User findByEmailIgnoreCase(String email);

    // Găsește toți utilizatorii inactivi (nu e nevoie de IgnoreCase aici)
    List<User> findByActiveFalse();
}

package com.ecommerce.app.repository;

import com.ecommerce.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
   // Optional<User> findById(Long id);
  //  List<User> findAll();
}
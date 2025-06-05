package com.ecommerce.app.repository;

import com.ecommerce.app.model.ERole;
import com.ecommerce.app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
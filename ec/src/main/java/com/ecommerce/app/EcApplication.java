package com.ecommerce.app;

import com.ecommerce.app.model.ERole;
import com.ecommerce.app.model.Role;
import com.ecommerce.app.repository.RoleRepository;
import com.ecommerce.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableCaching
public class EcApplication {

    private static final Logger logger = LoggerFactory.getLogger(EcApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EcApplication.class, args);
    }

    @Bean
    public CommandLineRunner initRolesAndAdmin(RoleRepository roleRepository , UserRepository userRepository, PasswordEncoder passwordEncoder ) {
        return args -> {
            // Check and create ROLE_USER if it doesn't exist
            if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
                roleRepository.save(new Role(ERole.ROLE_USER));
                logger.info("Role ROLE_USER created.");
            }
            // Check and create ROLE_ADMIN if it doesn't exist
            if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
                roleRepository.save(new Role(ERole.ROLE_ADMIN));
                logger.info("Role ROLE_ADMIN created.");
            }

        };
    }
}
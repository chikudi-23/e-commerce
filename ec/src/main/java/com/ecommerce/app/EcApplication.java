package com.ecommerce.app;

import com.ecommerce.app.model.ERole;
import com.ecommerce.app.model.Role;
import com.ecommerce.app.model.User;
import com.ecommerce.app.repository.RoleRepository;
import com.ecommerce.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class EcApplication {

	private static final Logger logger = LoggerFactory.getLogger(EcApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(EcApplication.class, args);
	}

	@Bean
	public CommandLineRunner initRolesAndAdmin(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
				roleRepository.save(new Role(ERole.ROLE_USER));
				logger.info("Role ROLE_USER created.");
			}
			if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
				roleRepository.save(new Role(ERole.ROLE_ADMIN));
				logger.info("Role ROLE_ADMIN created.");
			}

			if (userRepository.findByUsername("admin").isEmpty()) {
				Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
						.orElseThrow(() -> new RuntimeException("Error: Admin Role not found in init."));
				Set<Role> roles = new HashSet<>();
				roles.add(adminRole);

				User adminUser = new User("admin", "admin@example.com", passwordEncoder.encode("adminpass"));
				adminUser.setRoles(roles);
				userRepository.save(adminUser);
				logger.info("Default Admin user 'admin' created.");
			}
		};
	}
}
package com.ecommerce.app.service;

import com.ecommerce.app.dto.auth.LoginRequestDTO;
import com.ecommerce.app.dto.auth.UserRegistrationRequestDTO;
import com.ecommerce.app.dto.auth.JwtResponseDTO;
import com.ecommerce.app.dto.user.UserResponseDTO;
import com.ecommerce.app.dto.auth.ApiResponse;
import com.ecommerce.app.exception.ResourceNotFoundException;
import com.ecommerce.app.model.ERole;
import com.ecommerce.app.model.Role;
import com.ecommerce.app.model.User;
import com.ecommerce.app.repository.RoleRepository;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.security.JwtUtils;
import com.ecommerce.app.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    public ApiResponse<JwtResponseDTO> authenticateUser(LoginRequestDTO loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        logger.info("User {} logged in successfully.", userDetails.getUsername());

        String singleRole = roles.isEmpty() ? null : roles.get(0);

        // Assuming roles will always be a list of strings
        JwtResponseDTO jwtResponse = new JwtResponseDTO(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), singleRole);

        return new ApiResponse<>("Login successful.", HttpStatus.OK.value(), jwtResponse);
    }

    @Transactional
    public ApiResponse<UserResponseDTO> registerUser(UserRegistrationRequestDTO signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setApartmentName(signUpRequest.getApartmentName());
        user.setStreetAddress(signUpRequest.getStreetAddress());
        user.setCity(signUpRequest.getCity());
        user.setCountry(signUpRequest.getCountry());
        user.setState(signUpRequest.getState());
        user.setPincode(signUpRequest.getPincode()); // CHANGED: Set pincode

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role USER is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        logger.info("User {} registered successfully with role USER.", user.getUsername());

        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setApartmentName(user.getApartmentName());
        userResponse.setStreetAddress(user.getStreetAddress());
        userResponse.setCity(user.getCity());
        userResponse.setCountry(user.getCountry());
        userResponse.setState(user.getState());
        userResponse.setPincode(user.getPincode()); // CHANGED: Set pincode


        return new ApiResponse<>("User registered successfully.", HttpStatus.CREATED.value(), userResponse);
    }
}
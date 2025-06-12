package com.ecommerce.app.controller;

import com.ecommerce.app.dto.user.UserResponseDTO;
import com.ecommerce.app.dto.user.UserUpdateRequestDTO;
import com.ecommerce.app.dto.auth.ApiResponse;
import com.ecommerce.app.model.User;
import com.ecommerce.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.PageImpl; // Import PageImpl for mapping
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserResponseDTO userResponse = convertToDTO(user);
            return ResponseEntity.ok(new ApiResponse<>("User profile retrieved successfully.", HttpStatus.OK.value(), userResponse));
        } else {
            return new ResponseEntity<>(new ApiResponse<>("User not found", HttpStatus.NOT_FOUND.value(), null), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userService.getAllUsers(pageable);

        List<UserResponseDTO> userResponseDTOs = usersPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<UserResponseDTO> userResponseDTOPage = new PageImpl<>(userResponseDTOs, pageable, usersPage.getTotalElements());

        return ResponseEntity.ok(new ApiResponse<>("Users retrieved successfully.", HttpStatus.OK.value(), userResponseDTOPage));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (#id == authentication.principal.id)") // CHANGED HERE!
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequestDTO updateRequest) {

        User updatedUser = userService.updateUser(id, updateRequest);
        if (updatedUser != null) {
            UserResponseDTO userResponse = convertToDTO(updatedUser);
            return ResponseEntity.ok(new ApiResponse<>("User profile updated successfully.", HttpStatus.OK.value(), userResponse));
        } else {
            return new ResponseEntity<>(new ApiResponse<>("User not found or update failed.", HttpStatus.NOT_FOUND.value(), null), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            return ResponseEntity.ok(new ApiResponse<>("User deleted successfully.", HttpStatus.OK.value(), null));
        } else {
            return new ResponseEntity<>(new ApiResponse<>("User not found or could not be deleted.", HttpStatus.NOT_FOUND.value(), null), HttpStatus.NOT_FOUND);
        }
    }


    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setApartmentName(user.getApartmentName());
        dto.setStreetAddress(user.getStreetAddress());
        dto.setCity(user.getCity());
        dto.setCountry(user.getCountry());
        dto.setState(user.getState());
        dto.setPincode(user.getPincode());
        return dto;
    }
}
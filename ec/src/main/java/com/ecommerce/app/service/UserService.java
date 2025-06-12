package com.ecommerce.app.service;

import com.ecommerce.app.dto.user.UserUpdateRequestDTO;
import com.ecommerce.app.model.User;
import com.ecommerce.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.Pageable; // Import Pageable


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequestDTO updateRequest) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (updateRequest.getFirstName() != null) {
                user.setFirstName(updateRequest.getFirstName());
            }
            if (updateRequest.getLastName() != null) {
                user.setLastName(updateRequest.getLastName());
            }
            if (updateRequest.getPhoneNumber() != null) {
                user.setPhoneNumber(updateRequest.getPhoneNumber());
            }
            if (updateRequest.getApartmentName() != null) {
                user.setApartmentName(updateRequest.getApartmentName());
            }
            if (updateRequest.getStreetAddress() != null) {
                user.setStreetAddress(updateRequest.getStreetAddress());
            }
            if (updateRequest.getCity() != null) {
                user.setCity(updateRequest.getCity());
            }
            if (updateRequest.getCountry() != null) {
                user.setCountry(updateRequest.getCountry());
            }
            if (updateRequest.getPincode() != null) {
                user.setPincode(updateRequest.getPincode());
            }
            if(updateRequest.getEmail() != null) {
                user.setEmail(updateRequest.getEmail());
            }

            return userRepository.save(user);
        }
        return null;
    }

    @Transactional
    public boolean deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
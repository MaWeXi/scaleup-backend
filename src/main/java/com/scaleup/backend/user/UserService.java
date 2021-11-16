package com.scaleup.backend.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            return userOptional.orElse(null);
        } catch (Exception e) {

            // TODO: Implement logging of errors and give it back as response over HTTP
            System.out.println(e);
            return null;
        }
    }

    @Transactional
    public User updateUser(UUID id, User user) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                return userRepository.save(new User(user.getId(), user.getUsername()));
            } else {
                return null;
            }
        } catch (Exception e) {

            // TODO: Implement logging of errors and give it back as response over HTTP
            System.out.println(e);
            return null;
        }
    }

    public User saveUser(User user) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
            if (userOptional.isEmpty()) {
                return userRepository.save(user);
            }
            return null;
        } catch (Exception e) {

            // TODO: Implement logging of errors and give it back as response over HTTP
            System.out.println(e);
            return null;
        }
    }
}

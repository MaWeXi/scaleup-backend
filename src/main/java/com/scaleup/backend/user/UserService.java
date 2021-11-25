package com.scaleup.backend.user;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "No Users in DB");
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {

            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<User> getUserById(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            try {
                return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
            } catch (Exception e) {

                // TODO: Implement logging of errors
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), id);
            }
        } else {
            throw new CustomErrorException(HttpStatus.NOT_FOUND, "User could not be found under this id", id);
        }
    }

    @Transactional
    public ResponseEntity<User> updateUser(UUID id, User user) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            try {
                User _user = userRepository.save(new User(id, user.getUsername()));
                return new ResponseEntity<>(_user, HttpStatus.OK);
            } catch (Exception e) {

                // TODO: Implement logging of errors
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), user);
            }
        } else {
            throw new CustomErrorException(HttpStatus.NO_CONTENT, "User with this id was not found in DB", user);
        }
    }

    public ResponseEntity<User> saveUser(User user) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());

        if (userOptional.isEmpty()) {
            try {
                User _user = userRepository.save(user);
                return new ResponseEntity<>(_user, HttpStatus.CREATED);
            } catch (Exception e) {

                // TODO: Implement logging of errors
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), user);
            }
        } else {
            throw new CustomErrorException(HttpStatus.CONFLICT,
                    "User with this username already saved in DB",
                    user.getUsername());
        }
    }

    public ResponseEntity<?> deleteUser(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            try {
                userRepository.deleteUserById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {

                // TODO: Implement logging of errors
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), id);
            }
        } else {
            throw new CustomErrorException(HttpStatus.NO_CONTENT, "No user found under this id", id);
        }
    }
}

package com.scaleup.backend.user;


import com.scaleup.backend.league.DTO.AddLeagueDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class UserController {

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/all")
    public ResponseEntity<List<User>> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") String id) {
        return userService.getUserById(id);
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/user/{id}/")
    public ResponseEntity<User> updateUser(@PathVariable("id") String userId, @RequestBody User user) {
        return userService.updateUser(userId, user);
    }

    @PutMapping("/user/join-league/{id}")
    public ResponseEntity<User> updateLeagueOfUser(@PathVariable("id") String userId, @RequestBody AddLeagueDTO addLeagueDTO) {
        return userService.addUserToLeague(userId, addLeagueDTO);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
        return userService.deleteUser(id);
    }
}

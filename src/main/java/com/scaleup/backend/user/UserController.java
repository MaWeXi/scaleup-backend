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
        return userService.saveUser(user);
    }

    @PutMapping("/user/joinleague/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody AddLeagueDTO addLeagueDTO) {
        return userService.updateUser(id, addLeagueDTO);
    }

    @PutMapping("user/{id}")
    public ResponseEntity<User> updateUserLeague(@PathVariable("id") String id, @RequestParam String leagueCode) {
        return userService.updateUserLeague(id, leagueCode);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
        return userService.deleteUser(id);
    }
}

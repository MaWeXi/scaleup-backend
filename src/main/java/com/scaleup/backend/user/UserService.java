package com.scaleup.backend.user;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.league.DTO.AddLeagueDTO;
import com.scaleup.backend.league.League;
import com.scaleup.backend.league.LeagueRepository;
import com.scaleup.backend.userByLeague.UserByLeague;
import com.scaleup.backend.userByLeague.UserByLeagueRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    final UserRepository userRepository;
    final LeagueRepository leagueRepository;
    final UserByLeagueRepository userByLeagueRepository;

    public UserService(UserRepository userRepository, LeagueRepository leagueRepository, UserByLeagueRepository userByLeagueRepository) {
        this.userRepository = userRepository;
        this.leagueRepository = leagueRepository;
        this.userByLeagueRepository = userByLeagueRepository;
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

    public ResponseEntity<User> getUserById(String id) {
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

    public ResponseEntity<User> createUser(User user) {
        Optional<User> userOptional = userRepository.findUserByUsername(user.getUsername());

        if (userOptional.isEmpty()) {
            User _user = saveUser(user);
            return new ResponseEntity<>(_user, HttpStatus.CREATED);
        } else {
            throw new CustomErrorException(HttpStatus.CONFLICT,
                    "User with this username already exists",
                    user.getUsername());
        }
    }

    @Transactional
    public ResponseEntity<User> updateUser(String userId, User user) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User _user = saveUser(user);

            return new ResponseEntity<>(_user, HttpStatus.OK);
        } else {
            throw new CustomErrorException(HttpStatus.CONFLICT,
                    "User with this username already exists",
                    user.getUsername());
        }
    }

    @Transactional
    public ResponseEntity<User> addUserToLeague(String userId, AddLeagueDTO addLeague) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<League> leagueOptional = leagueRepository.findLeagueByLeagueIdAndLeagueCode(
                addLeague.getLeagueId(),
                addLeague.getLeagueCode()
        );
        Optional<UserByLeague> userByLeagueOptional = userByLeagueRepository.findByLeagueIdAndUserId(
                addLeague.getLeagueId(), userId);

        // Check if league and user are store in DB for given IDs
        if (userOptional.isPresent() && leagueOptional.isPresent() && userByLeagueOptional.isEmpty()) {
            try {

                /*
                Check if given user is not already stored in league and store new league
                 */
                League league = leagueOptional.get();
                User user = userOptional.get();

                // Check if user is already in given league
                if (user.getLeagues().containsKey(addLeague.getLeagueId())) {
                    throw new CustomErrorException(HttpStatus.CONFLICT,
                            "User is already in this league",
                            addLeague);
                } else {
                    // Add new league to already stored leagues
                    LinkedHashMap<String, String> leagues = user.getLeagues();
                    leagues.put(addLeague.getLeagueId(), league.getLeagueName());

                    userRepository.updateUserLeagues(leagues, userId);

                    /*
                    Save user to user_by_league DB
                     */
                    UserByLeague userByLeague = new UserByLeague(
                            league.getLeagueId(),
                            user.getId(),
                            user.getUsername(),
                            BigDecimal.ZERO,
                            league.getStartBudget(),
                            true,
                            false,
                            false,
                            false);
                    userByLeagueRepository.save(userByLeague);

                    return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
                }
            } catch (Exception e) {

                // TODO: Implement logging of errors
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), addLeague);
            }
        } else {
            throw new CustomErrorException(HttpStatus.NOT_FOUND,
                    "Either the user or the league with this id does not exist",
                    addLeague);
        }
    }

    public ResponseEntity<?> deleteUser(String id) {
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
            throw new CustomErrorException(HttpStatus.NOT_FOUND, "No user found under this id", id);
        }
    }

    /*
    Helper Methods
     */

    public User saveUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {

            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), user);
        }
    }
}

package com.scaleup.backend.user;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.league.DTO.AddLeagueDTO;
import com.scaleup.backend.league.League;
import com.scaleup.backend.league.LeagueRepository;
import com.scaleup.backend.userByLeague.UserByLeague;
import com.scaleup.backend.userByLeague.UserByLeagueKey;
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

    @Transactional
    public ResponseEntity<User> updateUser(String userId, AddLeagueDTO newLeague) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<League> leagueOptional = leagueRepository.findLeagueByLeagueIdAndLeagueCode(
                newLeague.getLeagueId(),
                newLeague.getLeagueCode()
        );

        // Check if league and user are store in DB for given IDs
        if (userOptional.isPresent() && leagueOptional.isPresent()) {
            try {

                /*
                Check if given user is not already stored in league and store new league
                 */
                League league = leagueOptional.get();
                User user = userOptional.get();

                // Check if user is already in given league
                if (user.getLeagues().containsKey(newLeague.getLeagueId())) {
                    throw new CustomErrorException(HttpStatus.CONFLICT,
                            "User is already in this league",
                            newLeague);
                } else {
                    // Add new league to already stored leagues
                    LinkedHashMap<String, String> leagues = user.getLeagues();
                    leagues.put(newLeague.getLeagueId(), league.getLeagueName());

                    userRepository.updateUserLeagues(leagues, userId);

                    /*
                    Save user to user_by_league DB
                     */
                    UserByLeagueKey key = new UserByLeagueKey(league.getLeagueId(), new BigDecimal("0"));
                    UserByLeague userByLeague = new UserByLeague(key, user.getUsername(), league.getStartBudget(),
                            true, true, true, true);
                    userByLeagueRepository.save(userByLeague);

                    return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
                }
            } catch (Exception e) {

                // TODO: Implement logging of errors
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), newLeague);
            }
        } else {
            throw new CustomErrorException(HttpStatus.NOT_FOUND,
                    "Either the user or the league with this id does not exist",
                    newLeague);
        }
    }

    public ResponseEntity<User> saveUser(User user) {
        Optional<User> userOptional = userRepository.findUserByUsername(user.getUsername());

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
                    "User with this username already exists",
                    user.getUsername());
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
}

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

    @Transactional
    public ResponseEntity<User> updateUser(String id, AddLeagueDTO newLeague) {
        Optional<User> userOptional = userRepository.findById(id);
        Optional<League> leagueOptional = leagueRepository.findLeagueByLeagueIdAndLeagueCode(
                newLeague.getLeagueId(),
                newLeague.getLeagueCode()
        );
        Optional<UserByLeague> userByLeague = userByLeagueRepository.findAllByLeagueidEqualsAndUseridEquals(newLeague.getLeagueId(), id);

        // Check if league and user are store in DB for given IDs
        if (userOptional.isPresent() && leagueOptional.isPresent()) {
            if (userByLeague.isEmpty()){
                try {
                    League league = leagueOptional.get();

                    // Check if user is already in given league
                    if (userOptional.get().getLeagues().containsKey(newLeague.getLeagueId())) {
                        throw new CustomErrorException(HttpStatus.CONFLICT,
                                "The user already joined this league",
                                newLeague);
                    } else {
                        // Add new league to already stored leagues
                        LinkedHashMap<String, String> leagues = userOptional.get().getLeagues();
                        leagues.put(newLeague.getLeagueId(), league.getLeagueName());

                        // Store all leagues in DB
                        userRepository.updateUserLeagues(leagues, id);
                        // Create new userByLeague
                        userByLeagueRepository.save(new UserByLeague(newLeague.getLeagueId(), BigDecimal.ZERO, id, Boolean.FALSE, BigDecimal.valueOf(leagueOptional.get().getStartBudget()), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
                        return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
                    }
                } catch (Exception e) {

                    // TODO: Implement logging of errors
                    throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), newLeague);
                }
            } else {
                throw new CustomErrorException(HttpStatus.CONFLICT,
                        "The user already joined this league");
            }

        } else {
            throw new CustomErrorException(HttpStatus.NOT_FOUND,
                    "Either the user or the league with this id does not exist",
                    newLeague);
        }
    }

    public ResponseEntity<User> updateUserLeague(String id, String leagueCode) {
        Optional<User> userOptional = userRepository.findById(id);
        Optional<League> leagueOptional = leagueRepository.findLeagueByLeagueCode(leagueCode);

        // Check if league and user are store in DB for given IDs
        if (userOptional.isPresent() && leagueOptional.isPresent()) {
            try {
                League league = leagueOptional.get();

                // Check if user is already in given league
                if (userOptional.get().getLeagues().containsKey(league.getLeagueId())) {
                    throw new CustomErrorException(HttpStatus.CONFLICT,
                            "User is already in this league",
                            league);
                } else {
                    // Add new league to already stored leagues
                    LinkedHashMap<String, String> leagues = userOptional.get().getLeagues();
                    leagues.put(league.getLeagueId(), league.getLeagueName());

                    // Store all leagues in DB
                    userRepository.updateUserLeagues(leagues, id);
                    return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
                }
            } catch (Exception e) {

                // TODO: Implement logging of errors
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), leagueCode);
            }
        } else {
            throw new CustomErrorException(HttpStatus.NOT_FOUND,
                    "Either the user or the league with this id does not exist",
                    leagueCode);
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

package com.scaleup.backend.league;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.league.DTO.LeagueDTO;
import com.scaleup.backend.user.User;
import com.scaleup.backend.user.UserRepository;
import com.scaleup.backend.userByLeague.UserByLeague;
import com.scaleup.backend.userByLeague.UserByLeagueKey;
import com.scaleup.backend.userByLeague.UserByLeagueRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class LeagueService {

    final LeagueRepository leagueRepository;
    final UserRepository userRepository;
    final UserByLeagueRepository userByLeagueRepository;
    final ModelMapper modelMapper = new ModelMapper();

    public LeagueService(LeagueRepository leagueRepository, UserRepository userRepository, UserByLeagueRepository userByLeagueRepository) {
        this.leagueRepository = leagueRepository;
        this.userRepository = userRepository;
        this.userByLeagueRepository = userByLeagueRepository;
    }

    public ResponseEntity<List<League>> getAllLeagues() {
        try {
            List<League> leagues = leagueRepository.findAll();
            if (leagues.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "No leagues in DB");
            }
            return new ResponseEntity<>(leagues, HttpStatus.OK);
        } catch (Exception e) {

            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<League> getLeagueById(String leagueId) {
        Optional<League> leagueOptional = leagueRepository.findLeagueByLeagueId(leagueId);

        if (leagueOptional.isPresent()) {
            try {
                return new ResponseEntity<>(leagueOptional.get(), HttpStatus.OK);
            } catch (Exception e) {

                // TODO: Implement logging of errors
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), leagueId);
            }
        } else {
            throw new CustomErrorException(HttpStatus.NOT_FOUND, "League could not be found under this id", leagueId);
        }
    }

    @Transactional
    public ResponseEntity<League> createLeague(LeagueDTO leagueDTO) {
        Optional<League> leagueOptional = leagueRepository.findLeagueByLeagueId(leagueDTO.getLeagueId());
        Optional<User> userOptional = userRepository.findUserById(leagueDTO.getUserId());
        Optional<UserByLeague> userByLeagueOptional = userByLeagueRepository.findAllByLeagueidEqualsAndUseridEquals(
                leagueDTO.getLeagueId(),
                leagueDTO.getUserId());

        if (leagueOptional.isEmpty() && userOptional.isPresent() && userByLeagueOptional.isEmpty()) {
            try {

                /*
                Map leagueDTO to league and save in DB
                 */
                League league = modelMapper.map(leagueDTO, League.class);
                League _league = leagueRepository.save(league);

                /*
                Get saved leagues from user in DB, add new league and save in DB
                 */
                User savedUser = userOptional.get();
                LinkedHashMap<String, String> userLeagues = savedUser.getLeagues();

                // Necessary as Java throws a NullPointerException when you try to put a new Key-Value into an empty
                // LinkedHashMap from the DB
                if (userLeagues == null) {
                    userLeagues = new LinkedHashMap<>();
                }

                userLeagues.put(_league.getLeagueId(), _league.getLeagueName());
                userRepository.updateUserLeagues(userLeagues, savedUser.getId());

                /*
                Save new league and user as admin to user_by_league DB
                 */
                UserByLeagueKey key = new UserByLeagueKey(leagueDTO.getLeagueId(), BigDecimal.ZERO);
                UserByLeague userByLeague = new UserByLeague(key, savedUser.getUsername(), leagueDTO.getStartBudget(),
                true, false, false, false);
                userByLeagueRepository.save(userByLeague);

                return new ResponseEntity<>(_league, HttpStatus.CREATED);
            } catch (Exception e) {

                // TODO: Implement logging of errors
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), leagueDTO);
            }
        } else {
            throw new CustomErrorException(HttpStatus.CONFLICT,
                    "Either this league id does already exist or the user does not exist in the DB",
                    leagueDTO);
        }
    }
}

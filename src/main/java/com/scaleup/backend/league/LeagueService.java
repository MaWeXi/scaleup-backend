package com.scaleup.backend.league;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.league.DTOs.LeagueDTO;
import com.scaleup.backend.user.User;
import com.scaleup.backend.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class LeagueService {

    final LeagueRepository leagueRepository;
    final UserRepository userRepository;
    final ModelMapper modelMapper = new ModelMapper();

    public LeagueService(LeagueRepository leagueRepository, UserRepository userRepository) {
        this.leagueRepository = leagueRepository;
        this.userRepository = userRepository;
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

        if (leagueOptional.isEmpty() && userOptional.isPresent()) {
            try {
                League league = modelMapper.map(leagueDTO, League.class);
                League _league = leagueRepository.save(league);

                User savedUser = userOptional.get();
                LinkedHashMap<String, String> userLeagues = savedUser.getLeagues();

                // If-clause is necessary as Java throws a NullPointerException when you try to put a new Key-Value
                // into an empty LinkedHashMap from the DB
                if (userLeagues == null) {
                    userLeagues = new LinkedHashMap<>();
                }
                userLeagues.put(_league.getLeagueId(), _league.getLeagueName());
                userRepository.updateUserLeagues(userLeagues, savedUser.getId());

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

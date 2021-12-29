package com.scaleup.backend.userByLeague;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.Optional;

public class UserByLeagueService {

    final UserByLeagueRepository userByLeagueRepository;

    public UserByLeagueService(UserByLeagueRepository userByLeagueRepository) {
        this.userByLeagueRepository = userByLeagueRepository;
    }

    public ResponseEntity<UserByLeague> findUserByLeagueByLeagueid(String league_id, String user_id) {
        try {
            Optional<UserByLeague> userByLeague = userByLeagueRepository.findAllByLeagueidEqualsAndUseridEquals(league_id, user_id);
            if (userByLeague.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "Der User konnte in dieser Liga nicht gefunden werden");
            }
            return new ResponseEntity<>(userByLeague.get(), HttpStatus.OK);
        }catch (Exception e){
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<Integer> findNumberOfJokerAvailable(String league_id, String user_id) {
        try {
            Optional<UserByLeague> userByLeague = userByLeagueRepository.findAllByLeagueidEqualsAndUseridEquals(league_id, user_id);
            if (userByLeague.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "Der User konnte in dieser Liga nicht gefunden werden");
            }
            Integer jokerAmount = 0;
            if (userByLeague.get().getJoker1()==Boolean.FALSE){ jokerAmount++;}
            if (userByLeague.get().getJoker2()==Boolean.FALSE){ jokerAmount++;}
            if (userByLeague.get().getJoker3()==Boolean.FALSE){ jokerAmount++;}
            return new ResponseEntity<>(jokerAmount, HttpStatus.OK);
        }catch (Exception e){
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}

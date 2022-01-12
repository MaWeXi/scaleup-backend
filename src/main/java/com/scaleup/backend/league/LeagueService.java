package com.scaleup.backend.league;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.league.DTO.LeagueDTO;
import com.scaleup.backend.user.DTO.LeaderboardUserDTO;
import com.scaleup.backend.user.User;
import com.scaleup.backend.user.UserRepository;
import com.scaleup.backend.userByLeague.UserByLeague;
import com.scaleup.backend.userByLeague.UserByLeagueRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class LeagueService {

    final LeagueRepository leagueRepository;
    final UserRepository userRepository;
    final UserByLeagueRepository userByLeagueRepository;
    final ModelMapper modelMapper = new ModelMapper();

    public LeagueService(
            LeagueRepository leagueRepository,
            UserRepository userRepository,
            UserByLeagueRepository userByLeagueRepository
    ) {
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
        Optional<User> userOptional = userRepository.findUserById(leagueDTO.getUserId());

        if (userOptional.isPresent()) {

            try {

                /*
                Map leagueDTO to league
                 */
                League league = modelMapper.map(leagueDTO, League.class);

                // Create and set new leagueId and new leagueCode
                String leagueCode = getUniqueString();
                String leagueId = leagueCode + "_SUID";
                while (leagueRepository.findLeagueByLeagueId(leagueId).isPresent()){
                    leagueCode = getUniqueString();
                    leagueId = leagueCode + "_SUID";
                }
                league.setLeagueId(leagueId);
                league.setLeagueCode(leagueCode);

                /*
                Save new league in DB
                 */
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

                // Save new league and user as admin to user_by_league DB
                UserByLeague userByLeague = new UserByLeague(
                        league.getLeagueId(),
                        savedUser.getId(),
                        savedUser.getUsername(),
                        BigDecimal.ZERO,
                        leagueDTO.getStartBudget(),
                        true,
                        false,
                        false,
                        false
                );
                userByLeagueRepository.save(userByLeague);

                return new ResponseEntity<>(_league, HttpStatus.CREATED);
            } catch (Exception e) {

                // TODO: Implement logging of errors
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), leagueDTO);
            }
        } else {
            throw new CustomErrorException(HttpStatus.CONFLICT,
                    "The user does not exist in the DB",
                    leagueDTO);
        }
    }

    // ----------------------------------- Helperclass -----------------------------------

    protected String getUniqueString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    // -----------------------------------------------------------------------------------

    public ResponseEntity<List<LeaderboardUserDTO>> getLeaderboardByLeagueId(String leagueId) {
        Optional<League> leagueOptional = leagueRepository.findLeagueByLeagueId(leagueId);

        if (leagueOptional.isPresent()) {
            try {
                Collection<LeaderboardUserDTO> userCollection = userByLeagueRepository.findByLeagueId(
                        leagueId,
                        LeaderboardUserDTO.class
                );

                if (userCollection.isEmpty()) {
                    throw new CustomErrorException(HttpStatus.NO_CONTENT, "No leagues in DB");
                } else {
                    List<LeaderboardUserDTO> userList;
                    if (userCollection instanceof List)
                        userList = (List<LeaderboardUserDTO>) userCollection;
                    else
                        userList = new ArrayList<>(userCollection);

                    return new ResponseEntity<>(userList, HttpStatus.OK);
                }
            } catch (Exception e) {
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else {
            throw new CustomErrorException(HttpStatus.NOT_FOUND, "League could not be found under this id", leagueId);
        }
    }
}

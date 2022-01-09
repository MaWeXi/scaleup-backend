package com.scaleup.backend.userByLeague;

import com.scaleup.backend.userByLeague.DTO.DepotUser;
import com.scaleup.backend.userByLeague.DTO.ValueDepotUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/")
public class UserByLeagueController {

    final UserByLeagueService userByLeagueService;

    public UserByLeagueController(UserByLeagueService userByLeagueService) {
        this.userByLeagueService = userByLeagueService;
    }

    @GetMapping("/valueDepot")
    public ResponseEntity<BigDecimal> createUser(@RequestBody ValueDepotUpdate valueDepotUpdate) {
        return userByLeagueService.updateValueDepot(valueDepotUpdate);
    }

    @GetMapping("/Depot")
    public ResponseEntity<DepotUser> getDepotUser(@RequestBody ValueDepotUpdate valueDepotUpdate) {
        return userByLeagueService.getDepotUser(valueDepotUpdate);
    }
}

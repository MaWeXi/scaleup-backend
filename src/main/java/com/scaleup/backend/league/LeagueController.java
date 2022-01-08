package com.scaleup.backend.league;

import com.scaleup.backend.league.DTO.LeagueDTO;
import com.scaleup.backend.user.DTO.LeaderboardUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class LeagueController {

    final LeagueService leagueService;

    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping("/league/all")
    public ResponseEntity<List<League>> getLeagues() {
        return leagueService.getAllLeagues();
    }

    @GetMapping("/league/{id}")
    public ResponseEntity<League> getLeagueById(@PathVariable("id") String leagueId) {
        return leagueService.getLeagueById(leagueId);
    }

    @GetMapping("/league/leaderboard/{id}")
    public ResponseEntity<List<LeaderboardUserDTO>> getLeaderboardByLeagueId(@PathVariable("id") String leagueId) {
        return leagueService.getLeaderboardByLeagueId(leagueId);
    }

    @PostMapping("/league")
    public ResponseEntity<League> createLeague(@RequestBody LeagueDTO leagueDTO) {
        return leagueService.createLeague(leagueDTO);
    }
}

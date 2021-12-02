package com.scaleup.backend.league;

import org.springframework.stereotype.Service;

@Service
public class LeagueService {

    final LeagueRepository leagueRepository;


    public LeagueService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }
}

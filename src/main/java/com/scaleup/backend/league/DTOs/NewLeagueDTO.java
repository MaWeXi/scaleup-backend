package com.scaleup.backend.league.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewLeagueDTO {

    private String leagueId;
    private String leagueName;
}

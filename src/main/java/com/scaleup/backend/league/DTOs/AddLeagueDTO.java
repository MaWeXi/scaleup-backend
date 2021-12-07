package com.scaleup.backend.league.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddLeagueDTO {

    private String leagueId;
    private String leagueCode;
}

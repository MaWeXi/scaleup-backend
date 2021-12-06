package com.scaleup.backend.league.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
@AllArgsConstructor
public class LeagueDTO {

    private String userId;
    private String leagueId;
    private String leagueName;
    private String code;
    private Double startBudget;
    private Double transactionCost;
    private Integer stockAmount;
    private LinkedHashMap<String, Double> probability;
}

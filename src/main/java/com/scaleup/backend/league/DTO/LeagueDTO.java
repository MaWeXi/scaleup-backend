package com.scaleup.backend.league.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Getter
@Setter
@AllArgsConstructor
public class LeagueDTO {

    private String userId;
    private String leagueId;
    private String leagueName;
    private String code;
    private BigDecimal startBudget;
    private BigDecimal transactionCost;
    private Integer stockAmount;
    private LinkedHashMap<String, Double> probability;
}

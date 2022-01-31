package com.scaleup.backend.stocksByUser.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StockAmountGetInformation {

    private String leagueId;
    private String userId;
    private String symbol;

}

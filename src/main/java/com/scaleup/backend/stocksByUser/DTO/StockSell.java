package com.scaleup.backend.stocksByUser.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class StockSell {

    private String userId;
    private String leagueId;
    private String symbol;
    private BigDecimal askPrice;
    private Integer amount;

}
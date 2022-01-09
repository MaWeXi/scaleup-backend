package com.scaleup.backend.userByLeague.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepotUser {

    private BigDecimal portfolio_value;
    private BigDecimal portfolio_valueDevelopmentTotal;
    private BigDecimal portfolio_valueDevelopmentPercent;
    private List<String> stocks;
    private List<BigDecimal[]> stocksValues;
    private LinkedHashMap<Timestamp, BigDecimal> historyPortfolio_value;
    private Integer amountJoker;

}

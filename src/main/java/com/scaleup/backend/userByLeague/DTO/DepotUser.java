package com.scaleup.backend.userByLeague.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepotUser {

    private BigDecimal portfolioValue;
    private BigDecimal portfolioValueDevelopmentTotal;
    private BigDecimal portfolioValueDevelopmentPercent;
    private BigDecimal freeBudget;
    private List<StockInDepot> stocksInDepot;
    private LinkedHashMap<LocalDateTime, BigDecimal> historyPortfolioValue;
    private Integer amountJoker;

}

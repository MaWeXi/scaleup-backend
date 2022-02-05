package com.scaleup.backend.userByLeague.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockInDepot {

    private String symbol;
    private String stockName;
    private BigDecimal currentPriceSingleStock;
    private BigDecimal currentPriceTotalValue;
    private BigDecimal currentPriceDevelopmentPercent;
    private BigDecimal currentPriceDevelopmentTotal;

}

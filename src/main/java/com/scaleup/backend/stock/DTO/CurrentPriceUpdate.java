package com.scaleup.backend.stock.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentPriceUpdate {

    private BigDecimal currentPrice;
    private BigDecimal currentPriceDevelopment;

}
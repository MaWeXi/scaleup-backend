package com.scaleup.backend.stock.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AskUpdate {

    private BigDecimal askPrice;
    private BigDecimal askPriceDevelopment;
}

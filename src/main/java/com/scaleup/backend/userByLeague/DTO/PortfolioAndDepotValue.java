package com.scaleup.backend.userByLeague.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioAndDepotValue {

    private BigDecimal portfolioValue;
    private BigDecimal depotValue;

}

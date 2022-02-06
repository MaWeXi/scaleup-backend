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
public class FreeBudgetAndTransactionCost {

    private BigDecimal freeBudget;
    private BigDecimal transactionCost;

}

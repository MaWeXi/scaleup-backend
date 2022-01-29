package com.scaleup.backend.transactions.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private String username;
    private Timestamp timestamp;
    private String symbol;
    private BigDecimal singleStockValue;
    private Float amount;
    private String typeOfTransaction;

}

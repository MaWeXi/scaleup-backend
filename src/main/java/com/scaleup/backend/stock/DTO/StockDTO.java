package com.scaleup.backend.stock.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class StockDTO {

    private String symbol;
    private String name;
    private String isin;
    private String wkn;
    private Timestamp lastUpdated;
    private BigDecimal askPrice;
    private BigDecimal bidPrice;
    private BigDecimal currentPrice;
    private BigDecimal dayOpen;
    private BigDecimal previousClose;
    private BigDecimal dayHigh;
    private BigDecimal dayLow;
    private BigDecimal fiftyTwoHigh;
    private BigDecimal fiftyTwoLow;
    private Float volume;
    private String stockType;
    private String sector;
    private List<StockHistory> stockHistory;
}

package com.scaleup.backend.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Table("stocks")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stock {

    @PrimaryKey("symbol")
    private String symbol;

    @Column("last_updated")
    private Timestamp lastUpdated;

    @Column("price")
    private BigDecimal price;

    @Column("day_open")
    private BigDecimal dayOpen;

    @Column("last_day_close")
    private BigDecimal previousClose;

    @Column("day_high")
    private BigDecimal dayHigh;

    @Column("day_low")
    private BigDecimal dayLow;

    @Column("fifty_two_high")
    private BigDecimal fiftyTwoHigh;

    @Column("fifty_two_low")
    private BigDecimal fiftyTwoLow;

    @Column("volume")
    private Float volume;

    @Column("stock_type")
    private String stockType;

    @Column("sector")
    private String sector;
}

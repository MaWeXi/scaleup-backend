package com.scaleup.backend.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Table("transaction_v1")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @PrimaryKeyColumn(name = "leagueId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String leagueId;

    @PrimaryKeyColumn(name = "year", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private Integer year;

    @PrimaryKeyColumn(name = "userId", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private String userId;

    @PrimaryKeyColumn(name = "timestamp", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    private Timestamp timestampTransaction;

    @PrimaryKeyColumn(name = "symbol", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    private String symbol;

    @Column("name")
    private String stockName;

    @Column("username")
    private String username;

    @Column("singleStockValue")
    private BigDecimal singleStockValue;

    @Column("amount")
    private Integer amount;

    //Buy or Sell
    @Column("typeOfTransaction")
    private String typeOfTransaction;
}

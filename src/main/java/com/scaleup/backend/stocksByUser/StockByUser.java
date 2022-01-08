package com.scaleup.backend.stocksByUser;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Table("stocksByUser")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockByUser {
    //leagueId, userId, symbol, timeLastUpdated, amount
    @PrimaryKeyColumn(name = "leagueId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String leagueId;

    @PrimaryKeyColumn(name = "userId", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String userId;

    @PrimaryKeyColumn(name = "symbol", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private String symbol;

    @Column("timeLastUpdated")
    private Timestamp timeLastUpdated;

    @Column("amount")
    private Integer amount;
}

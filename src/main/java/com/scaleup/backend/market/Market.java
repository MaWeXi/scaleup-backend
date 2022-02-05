package com.scaleup.backend.market;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Table("markets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Market {

    @PrimaryKeyColumn(name="league_id", ordinal=0, type=PrimaryKeyType.PARTITIONED)
    private String leagueId;

    @PrimaryKeyColumn(name="symbol", ordinal=1, type=PrimaryKeyType.CLUSTERED)
    private String symbol;

    @Column("stock_name")
    private String stockName;

    @Column("current_value")
    private BigDecimal currentValue;

    @Column("date_entered")
    private Timestamp dateEntered;

    @Column("date_left")
    private Timestamp dateLeft;

    @Column("joker_active")
    private Boolean jokerActive;
}

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

@Table("markets_v1")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Market {

    @PrimaryKeyColumn(name="league_id", ordinal=0, type=PrimaryKeyType.PARTITIONED)
    private String leagueId;

    @PrimaryKeyColumn(name="symbol", ordinal=1, type=PrimaryKeyType.CLUSTERED)
    private String symbol;

    @Column
    private String stockName;

    @Column("currentValue")
    private BigDecimal currentValue;

    @Column("dateEntered")
    private Timestamp dateEntered;

    @Column("dateLeft")
    private Timestamp dateLeft;

    @Column("jokerActive")
    private Boolean jokerActive;
}

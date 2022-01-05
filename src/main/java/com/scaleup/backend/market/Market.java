package com.scaleup.backend.market;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Table("markets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Market {
    // leagueId | symbol | current_value | date_entered | date_left | joker_active
    @PrimaryKeyColumn(name = "leagueId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String leagueid;

    @PrimaryKeyColumn(name = "symbol", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String symbol;

    @Column("current_value")
    private BigDecimal current_value;

    @Column("date_entered")
    private Timestamp date_entered;

    @Column("date_left")
    private Timestamp date_left;

    @Column("joker_active")
    private Boolean joker_active;
}

package com.scaleup.backend.userByLeague;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.math.BigDecimal;

@PrimaryKeyClass
@Getter
@Setter
@AllArgsConstructor
public class UserByLeagueKey {

    @PrimaryKeyColumn(name = "league_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String leagueId;

    @PrimaryKeyColumn(
            name = "portfolio_value",
            ordinal = 1,
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING)
    private BigDecimal portfolioValue;
}

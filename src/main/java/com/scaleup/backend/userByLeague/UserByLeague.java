package com.scaleup.backend.userByLeague;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;

@Table("user_by_leagues")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserByLeague {

    @PrimaryKeyColumn(name = "league_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String leagueId;

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String userId;

    @Column("username")
    private String username;

    @Column("portfolio_value")
    private BigDecimal portfolioValue;

    @Column("free_budget")
    private BigDecimal freeBudget;

    @Column("admin")
    private Boolean admin;

    @Column("joker1")
    private Boolean joker1;

    @Column("joker2")
    private Boolean joker2;

    @Column("joker3")
    private Boolean joker3;
}

package com.scaleup.backend.userByLeague;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Table("user_by_leagues")
@AllArgsConstructor
@NoArgsConstructor
public class UserByLeague {
// league_id | portfolio_value | user_id | admin | free_budget | joker1 | joker2 | joker3
    @PrimaryKeyColumn(name = "league_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String leagueid;

    @PrimaryKeyColumn(name = "portfolio_value", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private BigDecimal portfolio_value;

    @PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private String userid;

    @Column("admin")
    private Boolean admin;

    @Column("free_budget")
    private BigDecimal free_budget;

    @Column("joker1")
    private Boolean joker1;

    @Column("joker2")
    private Boolean joker2;

    @Column("joker3")
    private Boolean joker3;
}

package com.scaleup.backend.userByLeague;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;

@Table("user_by_leagues")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserByLeague {

    @PrimaryKey
    private UserByLeagueKey key;

    @Column("username")
    private String username;

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

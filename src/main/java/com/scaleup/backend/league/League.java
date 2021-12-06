package com.scaleup.backend.league;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.LinkedHashMap;

@Data
@Table("leagues")
@AllArgsConstructor
@NoArgsConstructor
public class League {

    @PrimaryKey("id")
    private String leagueId;

    @Column("name")
    private String leagueName;

    @Column("code")
    private String leagueCode;

    @Column("start_budget")
    private Double startBudget;

    @Column("transaction_costs")
    private Double transactionCost;

    @Column("stock_amount")
    private Integer stockAmount;

    @Column("probability")
    private LinkedHashMap<String, Double> probability;
}

package com.scaleup.backend.depotByUser;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Table("depot_by_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepotByUser {

    @PrimaryKeyColumn(name="league_id", ordinal=0, type=PrimaryKeyType.PARTITIONED)
    private String leagueId;

    @PrimaryKeyColumn(name="user_id", ordinal=1, type=PrimaryKeyType.CLUSTERED)
    private String userId;

    @PrimaryKeyColumn(name="date", ordinal=2, type=PrimaryKeyType.CLUSTERED)
    private LocalDateTime date;

    @Column("portfolio_value")
    private BigDecimal portfolioValue;

    @Column("depot_value")
    private BigDecimal depotValue;

}

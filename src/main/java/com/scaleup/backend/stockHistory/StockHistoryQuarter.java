package com.scaleup.backend.stockHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Table("stock_history_by_quarter")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockHistoryQuarter {

    @PrimaryKeyColumn(name="symbol", ordinal=0, type= PrimaryKeyType.PARTITIONED)
    private String symbol;

    @PrimaryKeyColumn(name="year_and_quarter", ordinal=1, type=PrimaryKeyType.PARTITIONED)
    private String quarter;

    @PrimaryKeyColumn(name="date", ordinal=2, type=PrimaryKeyType.CLUSTERED)
    private Timestamp date;

    @Column("close")
    private BigDecimal close;

    @Column("high")
    private BigDecimal high;

    @Column("low")
    private BigDecimal low;

    @Column("open")
    private BigDecimal open;

    @Column("volume")
    private BigDecimal volume;
}

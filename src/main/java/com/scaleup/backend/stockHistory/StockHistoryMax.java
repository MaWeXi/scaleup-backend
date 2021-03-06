package com.scaleup.backend.stockHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("stock_history_by_symbol")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockHistoryMax {

    @PrimaryKeyColumn(name="symbol", ordinal=0, type= PrimaryKeyType.PARTITIONED)
    private String symbol;

    @PrimaryKeyColumn(name="day", ordinal=1, type=PrimaryKeyType.CLUSTERED)
    private LocalDate day;

    @Column("close")
    private BigDecimal close;
}

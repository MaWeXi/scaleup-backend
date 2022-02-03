package com.scaleup.backend.stockHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("stock_history_by_day")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockHistoryMax {

    @PrimaryKey("symbol")
    private String symbol;

    @Column("day")
    private LocalDate day;

    @Column("close")
    private BigDecimal close;
}

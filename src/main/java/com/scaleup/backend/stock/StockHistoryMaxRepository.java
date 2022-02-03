package com.scaleup.backend.stock;

import com.scaleup.backend.stock.DTO.StockHistory;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockHistoryMaxRepository extends CassandraRepository<StockHistory, String> {

    @Query("SELECT day, close FROM stock_history_by_day WHERE symbol=?1 AND day <= ?2 AND day >= ?3")
    List<StockHistory> findBySymbolAndDate (String symbol, LocalDate nowDate, LocalDate pastDate);
}

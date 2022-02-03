package com.scaleup.backend.stockHistory;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockHistoryMaxRepository extends CassandraRepository<StockHistoryMax, String> {

    List<StockHistoryMax> findBySymbolAndDayBetween(String symbol, LocalDate datePast, LocalDate dateNow);
}

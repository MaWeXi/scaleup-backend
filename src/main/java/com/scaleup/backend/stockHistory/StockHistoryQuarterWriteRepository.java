package com.scaleup.backend.stockHistory;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockHistoryQuarterWriteRepository extends CassandraRepository<StockHistoryQuarter, String> {
}

package com.scaleup.backend.stock;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends CassandraRepository<Stock, String> {

    @AllowFiltering
    @NonNull
    Optional<Stock> findStockBySymbol(@NonNull String symbol);
}

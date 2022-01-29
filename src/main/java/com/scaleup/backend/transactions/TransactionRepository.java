package com.scaleup.backend.transactions;

import com.scaleup.backend.stocksByUser.StockByUser;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface TransactionRepository extends CassandraRepository<Transaction, String> {

    @AllowFiltering
    List<Transaction> findAllByLeagueIdEquals(String leagueid);
}

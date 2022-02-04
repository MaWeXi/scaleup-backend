package com.scaleup.backend.transactions;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface TransactionRepository extends CassandraRepository<Transaction, String> {

    @AllowFiltering
    List<Transaction> findTransactionByLeagueId(String leagueId);
}

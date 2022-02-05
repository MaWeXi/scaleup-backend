package com.scaleup.backend.transactions;

import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface TransactionRepository extends CassandraRepository<Transaction, String> {

    List<Transaction> findTransactionByLeagueIdAndYear(String leagueId, Integer year);
}

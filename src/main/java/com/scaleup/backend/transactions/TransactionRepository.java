package com.scaleup.backend.transactions;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface TransactionRepository extends CassandraRepository<Transaction, String> {
}

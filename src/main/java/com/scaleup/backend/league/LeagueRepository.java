package com.scaleup.backend.league;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeagueRepository extends CassandraRepository<League, String> {
}

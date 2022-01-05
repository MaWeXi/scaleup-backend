package com.scaleup.backend.userByLeague;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface UserByLeagueRepository extends CassandraRepository<UserByLeague, UserByLeagueKey> {
}

package com.scaleup.backend.userByLeague;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface UserByLeagueRepository extends CassandraRepository<UserByLeague, String> {

    <T>Collection<T> findByLeagueId(String leagueId, Class<T> type);

    Optional<UserByLeague> findByLeagueIdAndUserId(String leagueId, String userId);
}

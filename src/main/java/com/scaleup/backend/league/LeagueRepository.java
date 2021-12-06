package com.scaleup.backend.league;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeagueRepository extends CassandraRepository<League, String> {

    @AllowFiltering
    @NonNull
    Optional<League> findLeagueByLeagueId(@NonNull String leagueId);

    @AllowFiltering
    Optional<League> findLeagueByLeagueCode(String leagueCode);
}

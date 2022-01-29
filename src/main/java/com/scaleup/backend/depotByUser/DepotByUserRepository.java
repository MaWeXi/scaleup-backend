package com.scaleup.backend.depotByUser;

import com.scaleup.backend.league.League;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.lang.NonNull;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DepotByUserRepository extends CassandraRepository<DepotByUser, String> {

    @AllowFiltering
    @NonNull
    Optional<DepotByUser> findAllByLeagueIdEqualsAndUserIdEqualsAndDateEquals(String leagueId, String userId, LocalDateTime date);

    @AllowFiltering
    @NonNull
    List<DepotByUser> findAllByLeagueIdEqualsAndUserIdEqualsAndDateAfter (String leagueId, String userId, LocalDateTime date);
}


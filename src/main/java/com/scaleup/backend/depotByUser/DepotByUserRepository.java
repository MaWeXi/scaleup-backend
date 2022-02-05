package com.scaleup.backend.depotByUser;

import org.springframework.data.cassandra.repository.CassandraRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DepotByUserRepository extends CassandraRepository<DepotByUser, String> {

    Optional<DepotByUser> findByLeagueIdAndUserIdAndDate(String leagueId, String userId, LocalDateTime date);

    List<DepotByUser> findAllByLeagueIdAndUserIdAndDate(String leagueId, String userId, LocalDateTime date);
}


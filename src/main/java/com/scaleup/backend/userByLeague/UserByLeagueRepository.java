package com.scaleup.backend.userByLeague;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.Optional;

public interface UserByLeagueRepository extends CassandraRepository<UserByLeague, UserByLeagueKey> {

    @AllowFiltering
    Optional<UserByLeague> findByLeagueIdAndUserId(String leagueId, String userId);

    @AllowFiltering
    @Query("UPDATE user_by_leagues set joker?1=TRUE where league_id=?2 AND user_id=?3")
    void updateJoker(String jokerNumber, String leagueId, String userId);
}

package com.scaleup.backend.userByLeague;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface UserByLeagueRepository extends CassandraRepository<UserByLeague, String> {

    <T>Collection<T> findByLeagueId(String leagueId, Class<T> type);

    @AllowFiltering
    Optional<UserByLeague> findByLeagueIdAndUserId(String leagueId, String userId);

    @AllowFiltering
    @Query("UPDATE user_by_leagues set joker?1=TRUE where league_id=?2 AND user_id=?3")
    void updateJoker(String jokerNumber, String leagueId, String userId);
}

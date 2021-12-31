package com.scaleup.backend.userByLeague;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserByLeagueRepository extends CassandraRepository<UserByLeague, String> {

    @AllowFiltering
    Optional<UserByLeague> findAllByLeagueidEqualsAndUseridEquals(String league_id, String user_id);

    @AllowFiltering
    @Query("UPDATE user_by_leagues set joker?1=TRUE where league_id=?2 AND user_id=?3")
    void updateJoker(String jokerNumber, String league_id, String user_id);
}
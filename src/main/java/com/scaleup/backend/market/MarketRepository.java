package com.scaleup.backend.market;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MarketRepository extends CassandraRepository <Market, String>{

    @AllowFiltering
    List<Market> findMarketByLeagueid(String league);

    @AllowFiltering
    void deleteMarketsByLeagueidEquals(String league);

    @AllowFiltering
    @Query("UPDATE markets set joker_activated=TRUE where leagueid=?1")
    void updateMarketJoker(String leagueid);
}

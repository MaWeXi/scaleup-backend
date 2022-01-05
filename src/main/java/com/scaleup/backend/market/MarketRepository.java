package com.scaleup.backend.market;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MarketRepository extends CassandraRepository <Market, String>{

    @AllowFiltering
    Optional<Market> findMarketByLeagueidAndSymbolEquals(String leagueid, String symbol);

    @AllowFiltering
    List<Market> findMarketByLeagueid(String league);

    @AllowFiltering
    void deleteMarketsByLeagueidEquals(String league);

    @AllowFiltering
    @Query("UPDATE markets set joker_activated=TRUE where leagueid=?1 AND symbol=?2")
    void updateMarketJoker(String leagueid, String symbol);

    @AllowFiltering
    @Query("UPDATE markets set current_value=?1 where leagueid=?2 AND symbol=?3")
    void updateCurrentValue(BigDecimal current_value, String leagueid, String symbol);
}

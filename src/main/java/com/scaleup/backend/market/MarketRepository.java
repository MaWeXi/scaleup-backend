package com.scaleup.backend.market;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MarketRepository extends CassandraRepository <Market, String>{

    Optional<Market> findMarketByLeagueIdAndSymbol(String leagueId, String symbol);

    List<Market> findMarketByLeagueId(String league);

    void deleteMarketByLeagueId(String league);

//    @AllowFiltering
//    @Query("UPDATE markets set joker_activated=TRUE where leagueid=?1 AND symbol=?2")
//    void updateMarketJoker(String leagueid, String symbol);
//
//    @AllowFiltering
//    @Query("UPDATE markets set currentValue=?1 where leagueid=?2 AND symbol=?3")
//    void updateCurrentValue(BigDecimal current_value, String leagueid, String symbol);
}

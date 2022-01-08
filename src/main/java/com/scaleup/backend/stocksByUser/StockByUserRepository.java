package com.scaleup.backend.stocksByUser;

import com.scaleup.backend.market.Market;
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

public interface StockByUserRepository extends CassandraRepository <StockByUser, String>{

    @AllowFiltering
    List<StockByUser> findAllByUseridEquals(String userid);

    @AllowFiltering
    List<StockByUser> findAllByLeagueIdEqualsAndUserIdEquals(String leagueid, String userid);

    @AllowFiltering
    Optional<StockByUser> findAllByLeagueIdEqualsAndUserIdEqualsAndSymbolEquals(String leagueid, String userid, String symbol);

}

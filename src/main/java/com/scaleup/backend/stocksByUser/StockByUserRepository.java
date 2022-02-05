package com.scaleup.backend.stocksByUser;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockByUserRepository extends CassandraRepository<StockByUser, String> {

    List<StockByUser> findUserStocksByLeagueIdAndUserId(String leagueId, String userId);

    Optional<StockByUser> findUserStockByLeagueIdAndUserIdAndSymbol(String leagueId, String userId, String symbol);

    void deleteUserStockByLeagueIdAndUserIdAndSymbol(String leagueId, String userId, String symbol);
}

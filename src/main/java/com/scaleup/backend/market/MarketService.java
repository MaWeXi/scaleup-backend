package com.scaleup.backend.market;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.league.League;
import com.scaleup.backend.league.LeagueRepository;
import com.scaleup.backend.stock.Stock;
import com.scaleup.backend.stock.StockRepository;
import com.scaleup.backend.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class MarketService {
    final MarketRepository marketRepository;
    final StockRepository stockRepository;
    final LeagueRepository leagueRepository;

    public MarketService(MarketRepository marketRepository, StockRepository stockRepository, LeagueRepository leagueRepository) {
        this.marketRepository = marketRepository;
        this.stockRepository = stockRepository;
        this.leagueRepository = leagueRepository;
    }

    public ResponseEntity<List<Market>> findMarketByLeague(String league) {
        try {
            List<Market> markets = marketRepository.findMarketByLeagueid(league);
            if (markets.isEmpty()) {
                System.out.println("markt ist leer -> marketrefresh");
                marketRefresh(league);
                markets = marketRepository.findMarketByLeagueid(league);
            }
            if (markets.iterator().next().getDate_left().before(new Timestamp(System.currentTimeMillis()))){
                System.out.println("markt hat veraltete Einträge -> marketrefresh");
                marketRefresh(league);
                markets = marketRepository.findMarketByLeagueid(league);
            }
            return new ResponseEntity<>(markets, HttpStatus.OK);
        }catch (Exception e){
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<List<Market>> findMarketByLeagueWithJokerActive(String league) {
        try {
            List<Market> markets = marketRepository.findMarketByLeagueid(league);
            if (markets.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "Keine Aktien gefunden fuer diese Liga");
            }
            List<Market> marketJoker = new ArrayList<Market>();
            for( int i=0; i<markets.size(); i++ ){
                if (markets.get(i).getJoker_active()){
                    marketJoker.add(markets.get(i));
                }
            }
            return new ResponseEntity<>(marketJoker, HttpStatus.OK);
        }catch (Exception e){
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<Market> addSymbolToMarket(String league, String symbol, BigDecimal current_value, Timestamp date_entered, Timestamp date_left) {
        try {
            Market market = marketRepository.save(new Market(league, symbol, current_value, date_entered, date_left, false));
            return new ResponseEntity<>(market, HttpStatus.CREATED);
        } catch (Exception e) {
            // TODO: Implement logging of errors
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<User> updateJoker(String userid, String leagueid) {
        Optional<User> userOptional = userRepository.findById(id);
        Optional<League> leagueOptional = leagueRepository.findLeagueByLeagueCode(leagueCode);

        // Check if league and user are store in DB for given IDs
        if (userOptional.isPresent() && leagueOptional.isPresent()) {
            try {
                League league = leagueOptional.get();

                // Check if user is already in given league
                if (userOptional.get().getLeagues().containsKey(league.getLeagueId())) {
                    throw new CustomErrorException(HttpStatus.CONFLICT,
                            "User is already in this league",
                            league);
                } else {
                    // Add new league to already stored leagues
                    LinkedHashMap<String, String> leagues = userOptional.get().getLeagues();
                    leagues.put(league.getLeagueId(), league.getLeagueName());

                    // Store all leagues in DB
                    userRepository.updateUserLeagues(leagues, id);
                    return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
                }
            } catch (Exception e) {

                // TODO: Implement logging of errors
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage(), leagueCode);
            }
        } else {
            throw new CustomErrorException(HttpStatus.NOT_FOUND,
                    "Either the user or the league with this id does not exist",
                    leagueCode);
        }
    }

    public ResponseEntity<HttpStatus> deleteMarket(String league) {
        try {
            marketRepository.deleteMarketsByLeagueidEquals(league);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //-------------  MarketRefresh ------------
    public void marketRefresh(String league){
        Optional<League> leagueSettings = leagueRepository.findLeagueByLeagueId(league);
        if (leagueSettings.isPresent()){
            try {
                System.out.println("Eintritt Methode marketRefresh");
                // Get stocks in current market which are marked with a joker, so that they can be transferred into the next market
                List<Market> markets = marketRepository.findMarketByLeagueid(league);
                List<Market> marketActivatedJoker = new ArrayList<Market>();
                if (!markets.isEmpty()) {
                    for( int i=0; i<markets.size(); i++ ){
                        if (markets.get(i).getJoker_active()){
                            marketActivatedJoker.add(markets.get(i));
                        }
                    }
                }

                // Get composition of market from "league"
                LinkedHashMap<String, Double> marketProbability = leagueSettings.get().getProbability();
                Set<String> keys = marketProbability.keySet();
                Integer amountStocksInLeague = leagueSettings.get().getStockAmount();
                double amountStockPerSector = Math.floor(amountStocksInLeague*marketProbability.get(keys.iterator().next()));
                double amountStockRandom = amountStocksInLeague-amountStockPerSector*keys.size();

                // Delete old market
                marketRepository.deleteMarketsByLeagueidEquals(league);

                // set timestamps
                Timestamp tsNow = new Timestamp(System.currentTimeMillis());
                Calendar cal = Calendar.getInstance();
                cal.setTime(tsNow);
                cal.add(Calendar.DAY_OF_WEEK, 14);
                Timestamp ts14 = new Timestamp(cal.getTime().getTime());


                for (String key : keys) {
                    // Get random symbols from stocks
                    List<Stock> stocksSector= pickNRandom(stockRepository.findStocksBySectorEquals(key), (int) amountStockPerSector);
                    // Add symbols to market
                    for ( int i=0; i<amountStockPerSector; i++){
                        addSymbolToMarket(league, stocksSector.get(i).getSymbol(), stocksSector.get(i).getPrice(), tsNow, ts14);
                    }
                }

                for ( int i=0; i<marketActivatedJoker.size(); i++){
                    addSymbolToMarket(league, marketActivatedJoker.get(i).getSymbol(), marketActivatedJoker.get(i).getCurrent_value(), tsNow, ts14);
                }

                List<Stock> stockList = stockRepository.findAll();
                for ( int i=0; i<amountStockRandom; i++){

                }
            }catch (Exception e) {
                System.out.println("Fehler");
            }
        }

    }

    //helperclass: pick n random Stocks from specific sector (sector is defined in methodcall)
    public static List<Stock> pickNRandom(List<Stock> lst, int n) {
        List<Stock> copy = new ArrayList<Stock>(lst);
        Collections.shuffle(copy);
        return n > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, n);
    }
}

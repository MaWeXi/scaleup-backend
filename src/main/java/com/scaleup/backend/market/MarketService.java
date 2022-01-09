package com.scaleup.backend.market;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.league.League;
import com.scaleup.backend.league.LeagueRepository;
import com.scaleup.backend.market.DTO.UpdateJoker;
import com.scaleup.backend.stock.Stock;
import com.scaleup.backend.stock.StockRepository;
import com.scaleup.backend.userByLeague.UserByLeague;
import com.scaleup.backend.userByLeague.UserByLeagueRepository;
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
    final UserByLeagueRepository userByLeagueRepository;

    public MarketService(MarketRepository marketRepository, StockRepository stockRepository, LeagueRepository leagueRepository, UserByLeagueRepository userByLeagueRepository) {
        this.marketRepository = marketRepository;
        this.stockRepository = stockRepository;
        this.leagueRepository = leagueRepository;
        this.userByLeagueRepository = userByLeagueRepository;
    }

    public ResponseEntity<List<Market>> findMarketByLeague(String league) {
        try {
            Optional<League> leagueOptional = leagueRepository.findLeagueByLeagueId(league);
            if (leagueOptional.isPresent()){
                List<Market> markets = marketRepository.findMarketByLeagueid(league);
                if (markets.isEmpty()) {
                    System.out.println("markt ist leer -> marketrefresh");
                    marketRefresh(league);
                    markets = marketRepository.findMarketByLeagueid(league);
                } else if (markets.iterator().next().getDate_left().before(new Timestamp(System.currentTimeMillis()))){
                    System.out.println("markt hat veraltete Einträge -> marketrefresh");
                    marketRefresh(league);
                    markets = marketRepository.findMarketByLeagueid(league);
                } else {
                    markets = updateCurrentPrices(markets).getBody();
                }
                return new ResponseEntity<>(markets, HttpStatus.OK);
            } else {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "Es konnte keine Liga mit dieser leagueId gefunden werden");
            }
        }catch (Exception e){
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private ResponseEntity<List<Market>> updateCurrentPrices(List<Market> markets) {
        try {
            for( int i=0; i<markets.size(); i++ ){
                Market market = marketRepository.findMarketByLeagueidAndSymbolEquals(markets.get(i).getLeagueid(), markets.get(i).getSymbol()).get();
                market.setCurrent_value(stockRepository.findStockBySymbol(markets.get(i).getSymbol()).get().getCurrentPrice());
                marketRepository.save(market);
            }
            List<Market> marketUpdated = marketRepository.findMarketByLeagueid(markets.get(0).getLeagueid());
            return new ResponseEntity<>(marketUpdated, HttpStatus.OK);
        } catch (Exception e) {
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
            marketJoker = updateCurrentPrices(marketJoker).getBody();
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

    public ResponseEntity<Market> updateJoker(String leagueid, UpdateJoker updateJoker) {
        try {
            String userid = updateJoker.getUserid();
            String symbol = updateJoker.getSymbol();
            //check whether the user has jokers available
            Optional<UserByLeague> userByLeagueOptional = userByLeagueRepository.findByLeagueIdAndUserId(leagueid, userid);
            if (userByLeagueOptional.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "Der User konnte in dieser Liga nicht gefunden werden");
            }
            UserByLeague userByLeague = userByLeagueOptional.get();
            Integer jokerAmount = 0;
            String jokerToActivate = "1";
            if (userByLeague.getJoker1()==Boolean.FALSE){ jokerAmount++; jokerToActivate = "1";}
            if (userByLeague.getJoker2()==Boolean.FALSE){ jokerAmount++; jokerToActivate = "2";}
            if (userByLeague.getJoker3()==Boolean.FALSE){ jokerAmount++; jokerToActivate = "3";}
            if (jokerAmount > 0) {
                //update market for symbol: set joker FALSE -> TRUE
                Market market = marketRepository.findMarketByLeagueidAndSymbolEquals(leagueid, symbol).get();
                market.setJoker_active(true);
                market = marketRepository.save(market);
                //update userByLeague for user: set one joker FALSE -> TRUE
                if (jokerToActivate == "1") {
                    userByLeague.setJoker1(true);
                } else if (jokerToActivate == "2") {
                    userByLeague.setJoker2(true);
                } else {
                    userByLeague.setJoker3(true);
                }
                userByLeagueRepository.save(userByLeague);
                //return symbol in Market with updated Joker
                return new ResponseEntity<>(market, HttpStatus.CREATED);
            } else {
                throw new CustomErrorException(HttpStatus.CONFLICT, "Der User hat keine Joker übrig");
            }
        }catch (Exception e) {
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    //-------------  MarketRefresh ------------
    public void marketRefresh(String leagueid){
        Optional<League> leagueSettings = leagueRepository.findLeagueByLeagueId(leagueid);
        if (leagueSettings.isPresent()){
            try {
                System.out.println("Eintritt Methode marketRefresh");
                // Get stocks in current market which are marked with a joker, so that they can be transferred into the next market
                List<Market> markets = marketRepository.findMarketByLeagueid(leagueid);
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

                // set timestamps ------
                Timestamp tsNow = new Timestamp(System.currentTimeMillis());
                if (!markets.isEmpty()){
                    tsNow = markets.iterator().next().getDate_left();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(tsNow);
                cal.add(Calendar.DAY_OF_WEEK, 14);
                Timestamp ts14 = new Timestamp(cal.getTime().getTime());
                //if date_left is older than current timestamp, than tsNow equals current timestamp
                if (ts14.before(new Timestamp(System.currentTimeMillis()))){
                    tsNow = new Timestamp(System.currentTimeMillis());
                    cal.setTime(tsNow);
                    cal.add(Calendar.DAY_OF_WEEK, 14);
                    ts14 = new Timestamp(cal.getTime().getTime());
                }

                // Delete old market
                marketRepository.deleteMarketsByLeagueidEquals(leagueid);

                //Add stocks to market
                //Add the same amount of stocks from each sector selected in league-settings
                List<String> arraySymbols = new ArrayList<String>();
                for (String key : keys) {
                    // Get random symbols from table stocks
                    List<Stock> stocksSector1= pickNRandom(stockRepository.findStocksBySectorEquals(key), (int) amountStockPerSector);
                    // Add symbols to market
                    for ( int i=0; i<amountStockPerSector; i++){
                        addSymbolToMarket(leagueid, stocksSector1.get(i).getSymbol(), stocksSector1.get(i).getCurrentPrice(), tsNow, ts14);
                        arraySymbols.add(stocksSector1.get(i).getSymbol());
                    }
                }
                //Add the stocks which have been marked with a joker in the last market
                for ( int i=0; i<marketActivatedJoker.size(); i++){
                    addSymbolToMarket(leagueid, marketActivatedJoker.get(i).getSymbol(), marketActivatedJoker.get(i).getCurrent_value(), tsNow, ts14);
                    arraySymbols.add(marketActivatedJoker.get(i).getSymbol());
                }
                //Add the missing stocks (there is a amount of stocks declared in the market settings)
                // convert HashSet to an array
                String[] arrayKeys = keys.toArray(new String[keys.size()]);
                for ( int i=0; i<amountStockRandom; i++){
                    // generate a random number
                    Random rndm = new Random();
                    // this will generate a random number between 0 and HashSet.size - 1
                    int rndmNumber = rndm.nextInt(keys.size());
                    List<Stock> randomSectorStockList = stockRepository.findStocksBySectorEquals(arrayKeys[rndmNumber]);
                    // get one stock from random (but in league-settings selected) sector
                    Boolean stockNotYetIncluded = Boolean.FALSE;
                    while (!stockNotYetIncluded) {
                        List<Stock> stocksSector2 = pickNRandom(randomSectorStockList, 1);
                        if (!arraySymbols.contains(stocksSector2.get(0).getSymbol())) {
                            addSymbolToMarket(leagueid, stocksSector2.get(i).getSymbol(), stocksSector2.get(i).getCurrentPrice(), tsNow, ts14);
                            stockNotYetIncluded=Boolean.TRUE;
                        }
                    }
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

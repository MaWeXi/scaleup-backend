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

    public ResponseEntity<List<Market>> findMarketByLeague(String leagueId) {
        try {
            Optional<League> leagueOptional = leagueRepository.findLeagueByLeagueId(leagueId);

            if (leagueOptional.isPresent()){
                List<Market> markets = marketRepository.findMarketByLeagueId(leagueId);

                if (markets.isEmpty()) {
                    System.out.println("market empty -> market refresh");
                    marketRefresh(leagueId);
                    markets = marketRepository.findMarketByLeagueId(leagueId);
                } else if (markets.iterator().next().getDateLeft().before(new Timestamp(System.currentTimeMillis()))){
                    System.out.println("market entries are old -> market refresh");
                    marketRefresh(leagueId);
                    markets = marketRepository.findMarketByLeagueId(leagueId);
                } else {
                    markets = updateCurrentPrices(markets, leagueId);
                }
                return new ResponseEntity<>(markets, HttpStatus.OK);
            } else {
                throw new CustomErrorException(
                        HttpStatus.NO_CONTENT,
                        "Could not find a league with this id");
            }
        }catch (Exception e){
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<List<Market>> findMarketByLeagueWithJokerActive(String leagueId) {
        try {
            List<Market> markets = marketRepository.findMarketByLeagueId(leagueId);

            if (markets.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "There are no stocks in this league");
            }

            List<Market> marketJoker = new ArrayList<>();
            for (Market market : markets) {
                if (market.getJokerActive()) {
                    marketJoker.add(market);
                }
            }
            marketJoker = updateCurrentPrices(marketJoker, leagueId);
            return new ResponseEntity<>(marketJoker, HttpStatus.OK);
        }catch (Exception e){
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<Market> updateJoker(String leagueId, UpdateJoker updateJoker) {
        try {
            String userId = updateJoker.getUserid();
            String symbol = updateJoker.getSymbol();

            // Check if the user has an unused joker left
            Optional<UserByLeague> userByLeagueOptional = userByLeagueRepository.findByLeagueIdAndUserId(leagueId, userId);

            if (userByLeagueOptional.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "User could not be found in this league");
            }

            UserByLeague userByLeague = userByLeagueOptional.get();
            int jokerAmount = 0;
            String jokerToActivate = "1";

            if (userByLeague.getJoker1()==Boolean.FALSE){ jokerAmount++; jokerToActivate = "1";}
            else if (userByLeague.getJoker2()==Boolean.FALSE){ jokerAmount++; jokerToActivate = "2";}
            else if (userByLeague.getJoker3()==Boolean.FALSE){ jokerAmount++; jokerToActivate = "3";}

            if (jokerAmount > 0) {

                // Update joker to true in market
                Optional<Market> marketOptional = marketRepository.findMarketByLeagueIdAndSymbol(leagueId, symbol);

                if (marketOptional.isEmpty()) {
                    throw new CustomErrorException(
                            HttpStatus.CONFLICT,
                            "This league does not have a market yet or the stock is not in this market");
                }

                Market market = marketOptional.get();
                if (market.getJokerActive()) {
                    throw new CustomErrorException(HttpStatus.CONFLICT, "Another user already activated a joker for this stock");
                }
                market.setJokerActive(true);
                market = marketRepository.save(market);

                // Update userByLeague for user: set one joker FALSE -> TRUE
                switch (jokerToActivate) {
                    case "1":
                        userByLeague.setJoker1(true);
                        break;
                    case "2":
                        userByLeague.setJoker2(true);
                        break;
                    case "3":
                        userByLeague.setJoker3(true);
                        break;
                    default:
                        throw new CustomErrorException(
                                HttpStatus.CONFLICT,
                                "Something went wrong trying to activate a joker for stock " + symbol + " with user " + userId);
                }
                userByLeagueRepository.save(userByLeague);

                return new ResponseEntity<>(market, HttpStatus.CREATED);
            } else {
                throw new CustomErrorException(HttpStatus.CONFLICT, "This user does not have an unused joker left");
            }
        }catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /*
    Helper methods
     */

    public void marketRefresh(String leagueId){
        Optional<League> leagueSettings = leagueRepository.findLeagueByLeagueId(leagueId);

        if (leagueSettings.isPresent()){
            try {

                System.out.println("Market refresh starting...");

                // Get stocks in current market which are marked with a joker, so that they can be transferred into the next market
                List<Market> markets = marketRepository.findMarketByLeagueId(leagueId);
                List<Market> marketActivatedJoker = new ArrayList<>();

                if (!markets.isEmpty()) {
                    for (Market market : markets) {
                        if (market.getJokerActive()) {
                            marketActivatedJoker.add(market);
                        }
                    }
                }

                // Get composition of market from "league"
                LinkedHashMap<String, Double> marketProbability = leagueSettings.get().getProbability();
                Set<String> keys = marketProbability.keySet();
                Integer amountStocksInLeague = leagueSettings.get().getStockAmount();
                double amountStockPerSector = Math.floor(amountStocksInLeague*marketProbability.get(keys.iterator().next()));
                double amountStockRandom = amountStocksInLeague-amountStockPerSector*keys.size();

                // Set timestamps ------
                Timestamp tsNow = new Timestamp(System.currentTimeMillis());
                if (!markets.isEmpty()){
                    tsNow = markets.iterator().next().getDateLeft();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(tsNow);
                cal.add(Calendar.DAY_OF_WEEK, 14);
                Timestamp ts14 = new Timestamp(cal.getTime().getTime());

                // If dateLeft is older than current timestamp, than tsNow equals current timestamp
                if (ts14.before(new Timestamp(System.currentTimeMillis()))){
                    tsNow = new Timestamp(System.currentTimeMillis());
                    cal.setTime(tsNow);
                    cal.add(Calendar.DAY_OF_WEEK, 14);
                    ts14 = new Timestamp(cal.getTime().getTime());
                }

                // Delete old market
                marketRepository.deleteMarketByLeagueId(leagueId);

                //Add stocks to market
                //Add the same amount of stocks from each sector selected in league-settings
                List<String> arraySymbols = new ArrayList<>();
                for (String key : keys) {
                    // Get random symbols from table stocks
                    List<Stock> stocksSector1= pickNRandom(stockRepository.findStocksBySectorEquals(key), (int) amountStockPerSector);
                    // Add symbols to market
                    for ( int i=0; i<amountStockPerSector; i++){
                        addSymbolToMarket(leagueId, stocksSector1.get(i).getSymbol(), stocksSector1.get(i).getStockName(), stocksSector1.get(i).getCurrentPrice(), tsNow, ts14);
                        arraySymbols.add(stocksSector1.get(i).getSymbol());
                    }
                }
                //Add the stocks which have been marked with a joker in the last market
                for (Market market : marketActivatedJoker) {
                    addSymbolToMarket(leagueId, market.getSymbol(), market.getStockName(), market.getCurrentValue(), tsNow, ts14);
                    arraySymbols.add(market.getSymbol());
                }

                //Add the missing stocks (there is an amount of stocks declared in the market settings)
                // convert HashSet to an array
                String[] arrayKeys = keys.toArray(new String[keys.size()]);
                for ( int i=0; i<amountStockRandom; i++){
                    // generate a random number
                    Random random = new Random();
                    // this will generate a random number between 0 and HashSet.size - 1
                    int randomNumber = random.nextInt(keys.size());
                    List<Stock> randomSectorStockList = stockRepository.findStocksBySectorEquals(arrayKeys[randomNumber]);
                    // get one stock from random (but in league-settings selected) sector
                    boolean stockNotYetIncluded = Boolean.FALSE;
                    while (!stockNotYetIncluded) {
                        List<Stock> stocksSector2 = pickNRandom(randomSectorStockList, 1);
                        if (!arraySymbols.contains(stocksSector2.get(0).getSymbol())) {
                            addSymbolToMarket(leagueId, stocksSector2.get(0).getSymbol(), stocksSector2.get(0).getStockName(), stocksSector2.get(0).getCurrentPrice(), tsNow, ts14);
                            stockNotYetIncluded=Boolean.TRUE;
                        }
                    }
                }
            }catch (Exception e) {
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        } else {
            throw new CustomErrorException(HttpStatus.NO_CONTENT, "This id matches no league");
        }
    }

    private List<Market> updateCurrentPrices(List<Market> markets, String leagueId) {
        try {
            for (Market value : markets) {
                Optional<Market> marketOptional = marketRepository.findMarketByLeagueIdAndSymbol(
                        value.getLeagueId(),
                        value.getSymbol()
                );
                Optional<Stock> stockOptional = stockRepository.findStockBySymbol(value.getSymbol());

                if (marketOptional.isPresent() && stockOptional.isPresent()) {
                    Market market = marketOptional.get();
                    Stock stock = stockOptional.get();
                    market.setCurrentValue(stock.getCurrentPrice());
                } else {
                    throw new CustomErrorException(
                            HttpStatus.NO_CONTENT,
                            "Market id or symbol is wrong");
                }
            }
            return marketRepository.findMarketByLeagueId(leagueId);
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void addSymbolToMarket(String league, String symbol, String stockName, BigDecimal current_value,
            Timestamp date_entered, Timestamp date_left
    ) {
        try {
            marketRepository.save(new Market(league, symbol, stockName, current_value, date_entered, date_left,
                    false));
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public static List<Stock> pickNRandom(List<Stock> lst, int n) {
        List<Stock> copy = new ArrayList<>(lst);
        Collections.shuffle(copy);
        return n > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, n);
    }
}

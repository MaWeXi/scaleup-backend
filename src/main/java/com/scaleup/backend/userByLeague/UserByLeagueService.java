package com.scaleup.backend.userByLeague;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.stock.DTO.CurrentPriceUpdate;
import com.scaleup.backend.stock.StockRepository;
import com.scaleup.backend.stock.StockService;
import com.scaleup.backend.stocksByUser.StockByUser;
import com.scaleup.backend.stocksByUser.StockByUserRepository;
import com.scaleup.backend.userByLeague.DTO.DepotUser;
import com.scaleup.backend.userByLeague.DTO.ValueDepotUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserByLeagueService {

    final UserByLeagueRepository userByLeagueRepository;
    final StockByUserRepository stockByUserRepository;
    final StockRepository stockRepository;
    final StockService stockService;

    public UserByLeagueService(UserByLeagueRepository userByLeagueRepository, StockByUserRepository stockByUserRepository, StockRepository stockRepository, StockService stockService) {
        this.userByLeagueRepository = userByLeagueRepository;
        this.stockByUserRepository = stockByUserRepository;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
    }

    public ResponseEntity<UserByLeague> findUserByLeagueByLeagueId(String league_id, String user_id) {
        try {
            Optional<UserByLeague> userByLeague = userByLeagueRepository.findByLeagueIdAndUserId(league_id, user_id);
            if (userByLeague.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "Der User konnte in dieser Liga nicht gefunden werden");
            }
            return new ResponseEntity<>(userByLeague.get(), HttpStatus.OK);
        }catch (Exception e){
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<Integer> findNumberOfJokerAvailable(String league_id, String user_id) {
        try {
            Optional<UserByLeague> userByLeague = userByLeagueRepository.findByLeagueIdAndUserId(league_id, user_id);
            if (userByLeague.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "Der User konnte in dieser Liga nicht gefunden werden");
            }
            Integer jokerAmount = 0;
            if (userByLeague.get().getJoker1()==Boolean.FALSE){ jokerAmount++;}
            if (userByLeague.get().getJoker2()==Boolean.FALSE){ jokerAmount++;}
            if (userByLeague.get().getJoker3()==Boolean.FALSE){ jokerAmount++;}
            return new ResponseEntity<>(jokerAmount, HttpStatus.OK);
        }catch (Exception e){
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<BigDecimal> updateValueDepot(ValueDepotUpdate valueDepotUpdate){
        String leagueid = valueDepotUpdate.getLeagueid();
        String userid = valueDepotUpdate.getUserid();

        try {
            Optional<UserByLeague> userByLeague = userByLeagueRepository.findByLeagueIdAndUserId(leagueid, userid);

            if (userByLeague.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "Der User konnte in dieser Liga nicht gefunden werden");
            }

            List<StockByUser> stocksByUser = stockByUserRepository.findAllByLeagueIdEqualsAndUserIdEquals(leagueid, userid);
            BigDecimal valueDepot = new BigDecimal(BigInteger.valueOf(0));
            Integer amount;
            BigDecimal valueStock;

            for (int i=0; i<stocksByUser.size(); i++){
                amount = stocksByUser.get(i).getAmount();
                valueStock = stockRepository.findStockBySymbol(stocksByUser.get(i).getSymbol()).get().getCurrentPrice();
                valueDepot = valueDepot.add(valueStock.multiply(BigDecimal.valueOf(amount)));
            }

            return new ResponseEntity<>(valueDepot, HttpStatus.OK);
        } catch (Exception e) {
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<DepotUser> getDepotUser(ValueDepotUpdate valueDepotUpdate) {
        try {
            String leagueid = valueDepotUpdate.getLeagueid();;
            String userid = valueDepotUpdate.getUserid();
            DepotUser depotUser = new DepotUser();

            BigDecimal portfolio_value = updateValueDepot(valueDepotUpdate).getBody();
            // TODO: totale und prozentuale Veränderung des Depots
            BigDecimal porfolio_valueDevelopmentTotal = BigDecimal.valueOf(0);
            BigDecimal porfolio_valueDevelopmentPercent = BigDecimal.valueOf(0);

            // get all Stocks the user owns in this league
            List<StockByUser> stockByUserList = stockByUserRepository.findAllByLeagueIdEqualsAndUserIdEquals(leagueid, userid);
            List<String> stocksOwnedSymbol = new ArrayList<String>();
            List<Integer> stocksOwnedAmount = new ArrayList<Integer>();
            for (int i = 0; i<stockByUserList.size(); i++) {
                stocksOwnedSymbol.add(stockByUserList.get(i).getSymbol());
                stocksOwnedAmount.add(stockByUserList.get(i).getAmount());
            }

            // get information about single Value of stock
            CurrentPriceUpdate currentPriceUpdate = new CurrentPriceUpdate();
            List<BigDecimal> currentPriceSingleStock = new ArrayList<BigDecimal>();
            List<BigDecimal> currentPriceTotalValue = new ArrayList<BigDecimal>();
            List<BigDecimal> currentPriceDevelopmentPercent = new ArrayList<BigDecimal>();
            List<BigDecimal> currentPriceDevelopmentTotal = new ArrayList<BigDecimal>();
            for (int i = 0; i<stocksOwnedSymbol.size(); i++){
                currentPriceUpdate = stockService.getCurrentPrice(stocksOwnedSymbol.get(i)).getBody();
                currentPriceSingleStock.add(currentPriceUpdate.getCurrentPrice());
                currentPriceTotalValue.add(currentPriceUpdate.getCurrentPrice().multiply(BigDecimal.valueOf(stocksOwnedAmount.get(i))));
                currentPriceDevelopmentPercent.add(currentPriceUpdate.getCurrentPriceDevelopment());
                // TODO: calculate stockPrice Development as a total number since buying the stock
                currentPriceDevelopmentTotal.add(BigDecimal.valueOf(0));
            }

            // create List with Arrays for stocksValues
            BigDecimal[] stockValues = new BigDecimal[4];
            List<BigDecimal[]> stocksValues = new ArrayList<BigDecimal[]>();
            for (int i = 0; i<stocksOwnedSymbol.size(); i++){
                stockValues = new BigDecimal[4];
                stockValues[0] = currentPriceSingleStock.get(i);
                stockValues[1] = currentPriceTotalValue.get(i);
                stockValues[2] = currentPriceDevelopmentPercent.get(i);
                stockValues[3] = currentPriceDevelopmentTotal.get(i);
                stocksValues.add(stockValues);
            }

            // LinkedHashMap with historical Data about the portvalio_value
            // TODO: fill with historical Data
            Timestamp timestampNow = new Timestamp(System.currentTimeMillis());
            LinkedHashMap<Timestamp, BigDecimal> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put(timestampNow, portfolio_value);

            // get amount of usable jokers
            Integer amountJokers = findNumberOfJokerAvailable(leagueid, userid).getBody();

            // add data to depotUser
            depotUser.setPortfolio_value(portfolio_value);
            depotUser.setPortfolio_valueDevelopmentTotal(porfolio_valueDevelopmentTotal);
            depotUser.setPortfolio_valueDevelopmentPercent(porfolio_valueDevelopmentPercent);
            depotUser.setStocks(stocksOwnedSymbol);
            depotUser.setStocksValues(stocksValues);
            depotUser.setHistoryPortfolio_value(linkedHashMap);
            depotUser.setAmountJoker(amountJokers);

            return new ResponseEntity<>(depotUser, HttpStatus.OK);
        } catch (Exception e) {
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}

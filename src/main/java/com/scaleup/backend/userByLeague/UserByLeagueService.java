package com.scaleup.backend.userByLeague;

import com.scaleup.backend.depotByUser.DepotByUser;
import com.scaleup.backend.depotByUser.DepotByUserRepository;
import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.stock.DTO.CurrentPriceUpdate;
import com.scaleup.backend.stock.StockRepository;
import com.scaleup.backend.stock.StockService;
import com.scaleup.backend.stocksByUser.StockByUser;
import com.scaleup.backend.stocksByUser.StockByUserRepository;
import com.scaleup.backend.userByLeague.DTO.DepotUser;
import com.scaleup.backend.userByLeague.DTO.PortfolioAndDepotValue;
import com.scaleup.backend.userByLeague.DTO.StockInDepot;
import com.scaleup.backend.userByLeague.DTO.ValueDepotUpdate;
import org.joda.time.Period;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    final DepotByUserRepository depotByUserRepository;

    public UserByLeagueService(UserByLeagueRepository userByLeagueRepository, StockByUserRepository stockByUserRepository, StockRepository stockRepository, StockService stockService, DepotByUserRepository depotByUserRepository) {
        this.userByLeagueRepository = userByLeagueRepository;
        this.stockByUserRepository = stockByUserRepository;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
        this.depotByUserRepository = depotByUserRepository;
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
                //get updated current price of single stock to determine current value of stocks owned by the user
                currentPriceUpdate = stockService.getCurrentPrice(stocksOwnedSymbol.get(i)).getBody();
                currentPriceSingleStock.add(currentPriceUpdate.getCurrentPrice());
                currentPriceTotalValue.add(currentPriceUpdate.getCurrentPrice().multiply(BigDecimal.valueOf(stocksOwnedAmount.get(i))));
                //valueWhenBought represents all the prices when this specific stock got bought or sold in the past
                //calculate development of stock during the time when hold by the user
                currentPriceDevelopmentPercent.add(currentPriceTotalValue.get(i).divide(stockByUserList.get(i).getValueWhenBought(), 2, RoundingMode.HALF_UP).subtract(BigDecimal.valueOf(1)));
                currentPriceDevelopmentTotal.add(currentPriceTotalValue.get(i).subtract(stockByUserList.get(i).getValueWhenBought()));
            }

            // create List with Arrays for stocksValues
            List<StockInDepot> stocksInDepot = new ArrayList<>();
            for (int i = 0; i<stocksOwnedSymbol.size(); i++){
                StockInDepot stockInDepot = new StockInDepot();
                stockInDepot.setSymbol(stocksOwnedSymbol.get(i));
                stockInDepot.setCurrentPriceSingleStock(currentPriceSingleStock.get(i));
                stockInDepot.setCurrentPriceTotalValue(currentPriceTotalValue.get(i));
                stockInDepot.setCurrentPriceDevelopmentPercent(currentPriceDevelopmentPercent.get(i));
                stockInDepot.setCurrentPriceDevelopmentTotal(currentPriceDevelopmentTotal.get(i));
                stocksInDepot.add(stockInDepot);
            }

            LocalDate todayDate = LocalDate.now();
            LocalDateTime today = todayDate.atStartOfDay();
            LocalDateTime yesterday = today.minusDays(1);
            Optional<DepotByUser> depotByUser = depotByUserRepository.findAllByLeagueIdEqualsAndUserIdEqualsAndDateEquals(leagueid, userid, yesterday);
            BigDecimal porfolio_valueDevelopmentTotal = BigDecimal.valueOf(0);
            BigDecimal porfolio_valueDevelopmentPercent = BigDecimal.valueOf(0);
            BigDecimal portfolioValueToday = BigDecimal.valueOf(0);
            for (int i = 0; i<currentPriceTotalValue.size(); i++){
                portfolioValueToday = currentPriceTotalValue.get(i).add(portfolioValueToday);
            }
            if (depotByUser.isPresent()) {
                // if there is a portfolio value for the user in the league from yesterday, we calculate the development. There is no such entity, if the user joined the league today
                BigDecimal portfolioValueYesterday = depotByUser.get().getPortfolioValue();
                porfolio_valueDevelopmentPercent = portfolioValueToday.divide(portfolioValueYesterday, 2, RoundingMode.HALF_UP).subtract(BigDecimal.valueOf(1));
                porfolio_valueDevelopmentTotal = portfolioValueToday.subtract(portfolioValueYesterday);
            }


            // LinkedHashMap with historical Data about the portfolioValue in the past 30 days
            // TODO: fill with historical Data
            LocalDateTime thirtyDaysAgo = today.minusDays(30);
            List<DepotByUser> past30DaysPortfolioValue = depotByUserRepository.findAllByLeagueIdEqualsAndUserIdEqualsAndDateAfter(leagueid, userid, thirtyDaysAgo);
            LinkedHashMap<LocalDateTime, BigDecimal> linkedHashMap = new LinkedHashMap<>();
            LocalDateTime dateIterator = thirtyDaysAgo;
            // for the amount of days, that do not have an entity in depotByUser yet (because back then the user was not in the league yet) we add a 0 to the HashMap
            for (int i = 0; i<30-past30DaysPortfolioValue.size(); i++){
                linkedHashMap.put(dateIterator, BigDecimal.valueOf(0));
                dateIterator = dateIterator.plusDays(1);
            }
            // add the date and portfoliovalue to the hashmap
            for (int i = 0; i<past30DaysPortfolioValue.size(); i++){
                linkedHashMap.put(past30DaysPortfolioValue.get(i).getDate(), past30DaysPortfolioValue.get(i).getPortfolioValue());
            }



            // get amount of usable jokers
            Integer amountJokers = findNumberOfJokerAvailable(leagueid, userid).getBody();

            // add data to depotUser
            depotUser.setPortfolio_value(portfolioValueToday);
            depotUser.setPortfolio_valueDevelopmentTotal(porfolio_valueDevelopmentTotal);
            depotUser.setPortfolio_valueDevelopmentPercent(porfolio_valueDevelopmentPercent);
            depotUser.setStocksInDepot(stocksInDepot);
            depotUser.setHistoryPortfolio_value(linkedHashMap);
            depotUser.setAmountJoker(amountJokers);

            return new ResponseEntity<>(depotUser, HttpStatus.OK);
        } catch (Exception e) {
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<PortfolioAndDepotValue> getCurrentPortfolioAndDepotValue(String leagueId, String userId) {
        try {
            Optional<UserByLeague> userByLeague = userByLeagueRepository.findByLeagueIdAndUserId(leagueId, userId);
            if (userByLeague.isEmpty()){
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "Der User mit id " + userId + " ist nicht in der Liga mit id " + leagueId);
            } else {
                ValueDepotUpdate valueDepotUpdate = new ValueDepotUpdate(leagueId, userId);
                BigDecimal freeBudget = userByLeague.get().getFreeBudget();
                BigDecimal portfolioValue = updateValueDepot(valueDepotUpdate).getBody();
                PortfolioAndDepotValue portfolioAndDepotValue = new PortfolioAndDepotValue(portfolioValue, portfolioValue.add(freeBudget));
                return new ResponseEntity<>(portfolioAndDepotValue, HttpStatus.OK);
            }
        } catch (Exception e) {
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}

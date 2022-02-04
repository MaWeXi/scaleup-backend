package com.scaleup.backend.userByLeague;

import com.scaleup.backend.depotByUser.DepotByUser;
import com.scaleup.backend.depotByUser.DepotByUserRepository;
import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.stock.Stock;
import com.scaleup.backend.stock.StockRepository;
import com.scaleup.backend.stock.StockService;
import com.scaleup.backend.stocksByUser.UserStock;
import com.scaleup.backend.stocksByUser.UserStockRepository;
import com.scaleup.backend.userByLeague.DTO.DepotUser;
import com.scaleup.backend.userByLeague.DTO.PortfolioAndDepotValue;
import com.scaleup.backend.userByLeague.DTO.StockInDepot;
import com.scaleup.backend.userByLeague.DTO.ValueDepotUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserByLeagueService {

    final UserByLeagueRepository userByLeagueRepository;
    final UserStockRepository userStockRepository;
    final StockRepository stockRepository;
    final StockService stockService;
    final DepotByUserRepository depotByUserRepository;

    public UserByLeagueService(
            UserByLeagueRepository userByLeagueRepository,
            UserStockRepository userStockRepository,
            StockRepository stockRepository,
            StockService stockService,
            DepotByUserRepository depotByUserRepository
    ) {
        this.userByLeagueRepository = userByLeagueRepository;
        this.userStockRepository = userStockRepository;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
        this.depotByUserRepository = depotByUserRepository;
    }

    public ResponseEntity<Integer> findNumberOfJokerAvailable(String league_id, String user_id) {
        try {
            Optional<UserByLeague> userByLeague = userByLeagueRepository.findByLeagueIdAndUserId(league_id, user_id);
            if (userByLeague.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "User could not be found in this league");
            }
            Integer jokerAmount = 0;
            if (userByLeague.get().getJoker1()==Boolean.FALSE){ jokerAmount++;}
            if (userByLeague.get().getJoker2()==Boolean.FALSE){ jokerAmount++;}
            if (userByLeague.get().getJoker3()==Boolean.FALSE){ jokerAmount++;}
            return new ResponseEntity<>(jokerAmount, HttpStatus.OK);
        }catch (Exception e){
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<BigDecimal> updateValueDepot(ValueDepotUpdate valueDepotUpdate){
        String leagueId = valueDepotUpdate.getLeagueId();
        String userId = valueDepotUpdate.getUserId();

        try {
            Optional<UserByLeague> userByLeague = userByLeagueRepository.findByLeagueIdAndUserId(leagueId, userId);

            if (userByLeague.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "User could not be found in this league");
            }

            List<UserStock> userStocks = userStockRepository.findUserStocksByLeagueIdAndUserId(leagueId, userId);
            BigDecimal valueDepot = new BigDecimal(BigInteger.valueOf(0));
            Integer amount;
            BigDecimal valueStock;

            if (userStocks.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "User has no stocks");
            }

            for (UserStock userStock : userStocks) {
                amount = userStock.getAmount();
                Optional<Stock> stock = stockRepository.findStockBySymbol(userStock.getSymbol());
                if (stock.isPresent()) {
                    valueStock = stock.get().getCurrentPrice();
                    valueDepot = valueDepot.add(valueStock.multiply(BigDecimal.valueOf(amount)));
                } else {
                    throw new CustomErrorException(
                            HttpStatus.NO_CONTENT,
                            "The stock with the symbol " + userStock.getSymbol() + " does not exist");
                }
            }

            return new ResponseEntity<>(valueDepot, HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<DepotUser> getDepotUser(ValueDepotUpdate valueDepotUpdate) {
        try {
            String leagueId = valueDepotUpdate.getLeagueId();
            String userId = valueDepotUpdate.getUserId();

            // Get all stocks the user owns in this league
            List<UserStock> userStocks = userStockRepository.findUserStocksByLeagueIdAndUserId(leagueId, userId);

            // Collect all info of the stocks owned by the user in this league
            List<StockInDepot> stocksInDepot = new ArrayList<>();
            BigDecimal portfolioValue = BigDecimal.ZERO;
            for (UserStock userStock : userStocks) {
                Optional<Stock> stockOptional = stockRepository.findStockBySymbol(userStock.getSymbol());

                if (stockOptional.isEmpty()) {
                    throw new CustomErrorException(
                            HttpStatus.NO_CONTENT,
                            "The stock with the symbol " + userStock.getSymbol() + " does not exist");
                }

                Stock stock = stockOptional.get();
                BigDecimal totalStockValue = stock.getCurrentPrice().multiply(BigDecimal.valueOf(userStock.getAmount()));
                stocksInDepot.add(new StockInDepot(
                        userStock.getSymbol(),
                        userStock.getStockName(),
                        stock.getCurrentPrice(),
                        totalStockValue,
                        stock.getCurrentPrice().divide(userStock.getValueWhenBought(), 2, RoundingMode.HALF_UP).subtract(BigDecimal.ONE),
                        stock.getCurrentPrice().subtract(userStock.getValueWhenBought())
                        ));

                portfolioValue = portfolioValue.add(totalStockValue);
            }

            LocalDate todayDate = LocalDate.now();
            LocalDateTime today = todayDate.atStartOfDay();
            LocalDateTime yesterday = today.minusDays(1);
            Optional<DepotByUser> depotByUser = depotByUserRepository.findByLeagueIdAndUserIdAndDate(leagueId, userId, yesterday);

            BigDecimal portfolioValueDevelopmentTotal = BigDecimal.ZERO;
            BigDecimal portfolioValueDevelopmentPercent = BigDecimal.ZERO;

            if (depotByUser.isPresent()) {
                // If there is a portfolio value for the user in the league from yesterday the development is calculated.
                // There is no such entity, if the user joined the league today
                BigDecimal portfolioValueYesterday = depotByUser.get().getPortfolioValue();
                portfolioValueDevelopmentPercent = portfolioValue.divide(portfolioValueYesterday, 2, RoundingMode.HALF_UP).subtract(BigDecimal.ONE);
                portfolioValueDevelopmentTotal = portfolioValue.subtract(portfolioValueYesterday);
            }


            // LinkedHashMap with historical Data about the portfolioValue in the past 30 days
            // TODO: fill with historical Data
            LocalDateTime thirtyDaysAgo = today.minusDays(30);
            List<DepotByUser> past30DaysPortfolioValue = depotByUserRepository.findAllByLeagueIdAndUserIdAndDate(leagueId, userId, thirtyDaysAgo);
            LinkedHashMap<LocalDateTime, BigDecimal> linkedHashMap = new LinkedHashMap<>();
            LocalDateTime dateIterator = thirtyDaysAgo;
            // for the amount of days, that do not have an entity in depotByUser yet (because back then the user was not in the league yet) we add a 0 to the HashMap
            for (int i = 0; i<30-past30DaysPortfolioValue.size(); i++){
                linkedHashMap.put(dateIterator, BigDecimal.valueOf(0.0));
                dateIterator = dateIterator.plusDays(1);
            }
            // Add the date and portfolioValue to the hashmap
            for (DepotByUser byUser : past30DaysPortfolioValue) {
                linkedHashMap.put(byUser.getDate(), byUser.getPortfolioValue());
            }
            linkedHashMap.put(today, portfolioValue);

            // Get free Budget
            Optional<UserByLeague> userByLeagueOptional = userByLeagueRepository.findByLeagueIdAndUserId(leagueId, userId);
            if (userByLeagueOptional.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "This user does not exist");
            }

            BigDecimal freeBudget = userByLeagueOptional.get().getFreeBudget();
            Integer amountJokers = findNumberOfJokerAvailable(leagueId, userId).getBody();

            // Add data to depotUser
            DepotUser depotUser = new DepotUser(
                portfolioValue,
                portfolioValueDevelopmentTotal,
                portfolioValueDevelopmentPercent,
                freeBudget,
                stocksInDepot,
                linkedHashMap,
                amountJokers
            );

            return new ResponseEntity<>(depotUser, HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<BigDecimal> getFreeBudget(ValueDepotUpdate valueDepotUpdate) {
        String leagueId = valueDepotUpdate.getLeagueId();
        String userId = valueDepotUpdate.getUserId();

        try {
            Optional<UserByLeague> userByLeague = userByLeagueRepository.findByLeagueIdAndUserId(leagueId, userId);
            if (userByLeague.isEmpty()){
                throw new CustomErrorException(
                        HttpStatus.NO_CONTENT,
                        "User " + userId + " is not in this league with the id: " + leagueId);
            } else {
                BigDecimal freeBudget = userByLeague.get().getFreeBudget();
                return new ResponseEntity<>(freeBudget, HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /*
    Helper methods
     */

    public PortfolioAndDepotValue getCurrentPortfolioAndDepotValue(String leagueId, String userId) {
        try {
            Optional<UserByLeague> userByLeague = userByLeagueRepository.findByLeagueIdAndUserId(leagueId, userId);
            if (userByLeague.isEmpty()){
                throw new CustomErrorException(
                        HttpStatus.NO_CONTENT,
                        "User with id: " + userId + " is not in this league: " + leagueId);
            } else {
                ValueDepotUpdate valueDepotUpdate = new ValueDepotUpdate(leagueId, userId);
                BigDecimal freeBudget = userByLeague.get().getFreeBudget();
                BigDecimal portfolioValue = updateValueDepot(valueDepotUpdate).getBody();
                assert portfolioValue != null;
                return new PortfolioAndDepotValue(portfolioValue, portfolioValue.add(freeBudget));
            }
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}

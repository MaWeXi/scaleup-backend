package com.scaleup.backend.stocksByUser;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.stock.Stock;
import com.scaleup.backend.stock.StockRepository;
import com.scaleup.backend.stocksByUser.DTO.StockAmountGetInformation;
import com.scaleup.backend.stocksByUser.DTO.StockBuy;
import com.scaleup.backend.stocksByUser.DTO.StockSell;
import com.scaleup.backend.transactions.Transaction;
import com.scaleup.backend.transactions.TransactionRepository;
import com.scaleup.backend.userByLeague.UserByLeague;
import com.scaleup.backend.userByLeague.UserByLeagueRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

@Service
public class UserStockService {

    final UserStockRepository userStockRepository;
    final StockRepository stockRepository;
    final UserByLeagueRepository userByLeagueRepository;
    final TransactionRepository transactionRepository;

    public UserStockService(
            UserStockRepository userStockRepository,
            StockRepository stockRepository,
            UserByLeagueRepository userByLeagueRepository,
            TransactionRepository transactionRepository
    ) {
        this.userStockRepository = userStockRepository;
        this.stockRepository = stockRepository;
        this.userByLeagueRepository = userByLeagueRepository;
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<UserStock> buyStock(StockBuy stockBuy) {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String leagueId = stockBuy.getLeagueId();
        String userId = stockBuy.getUserId();
        String symbol = stockBuy.getSymbol();
        Integer amount = stockBuy.getAmount();

        Optional<UserStock> userStockOptional = userStockRepository.findUserStockByLeagueIdAndUserIdAndSymbol(
                leagueId,
                userId,
                symbol
        );
        Optional<Stock> stockOptional = stockRepository.findStockBySymbol(symbol);
        Optional<UserByLeague> userOptional = userByLeagueRepository.findByLeagueIdAndUserId(leagueId, userId);

        try {

            // Check if stockId and userId are valid
            if (stockOptional.isPresent() && userOptional.isPresent()) {

                Stock stock = stockOptional.get();
                UserByLeague user = userOptional.get();
                BigDecimal buyCost = stock.getAskPrice().multiply(BigDecimal.valueOf(amount));
                BigDecimal freeBudget = user.getFreeBudget();

                // Check whether the user has enough balance to buy this amount of stock
                if (freeBudget.compareTo(buyCost) >= 0) {
                    UserStock userStock;

                    // Check if the user already owns this particular stock and either just update the amount or
                    // create a new UserStock object to store in the DB
                    if (userStockOptional.isPresent()) {
                        Integer ownedStockAmount = userStockOptional.get().getAmount();
                        userStock = userStockOptional.get();
                        userStock.setAmount(ownedStockAmount + amount);
                        userStock.setTimeLastUpdated(timestamp);
                    } else {
                        userStock = new UserStock(
                                leagueId,
                                userId,
                                symbol,
                                stock.getStockName(),
                                timestamp,
                                amount,
                                stock.getAskPrice().multiply(BigDecimal.valueOf(amount)));
                    }

                    // Save updated UserStock object
                    UserStock _userStock = userStockRepository.save(userStock);

                    // Create a new Transaction object and store in DB
                    transactionRepository.save(new Transaction(
                            leagueId,
                            timestamp.getYear(),
                            userId,
                            timestamp,
                            symbol,
                            stock.getStockName(),
                            user.getUsername(),
                            stock.getBidPrice(),
                            amount,
                            "buy"
                    ));

                    // Update the portfolioValue and the freeBudget of the user in UserByLeague
                    user.setFreeBudget(freeBudget.subtract(buyCost));
                    user.setPortfolioValue(user.getPortfolioValue().add(buyCost));

                    return new ResponseEntity<>(_userStock, HttpStatus.OK);
                } else {
                    throw new CustomErrorException(HttpStatus.CONFLICT,  "Balance too low to buy this amount");
                }
            } else {
                throw new CustomErrorException(HttpStatus.CONFLICT,  "Either stockId or userId is incorrect");
            }
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<UserStock> sellStock(StockSell stockSell) {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String leagueId = stockSell.getLeagueId();
        String userId = stockSell.getUserId();
        String symbol = stockSell.getSymbol();
        Integer amount = stockSell.getAmount();

        Optional<UserStock> userStockOptional = userStockRepository.findUserStockByLeagueIdAndUserIdAndSymbol(
                leagueId,
                userId,
                symbol
        );
        Optional<Stock> stockOptional = stockRepository.findStockBySymbol(symbol);
        Optional<UserByLeague> userOptional = userByLeagueRepository.findByLeagueIdAndUserId(leagueId, userId);

        try {

            // Check if stockId and userId are valid
            if (stockOptional.isPresent() && userOptional.isPresent()) {
                Stock stock = stockOptional.get();
                UserByLeague user = userOptional.get();
                BigDecimal sellCost = stock.getBidPrice().multiply(BigDecimal.valueOf(amount));
                BigDecimal freeBudget = user.getFreeBudget();

                if (userStockOptional.isPresent()) {
                    UserStock userStock = userStockOptional.get();

                    if (userStock.getAmount() >= amount) {
                        userStock.setAmount(userStock.getAmount() - amount);
                        userStock.setTimeLastUpdated(timestamp);

                        // Save updated UserStock object
                        UserStock _userStock = userStockRepository.save(userStock);

                        // Create a new Transaction object and store in DB
                        transactionRepository.save(new Transaction(
                                leagueId,
                                timestamp.getYear(),
                                userId,
                                timestamp,
                                symbol,
                                stock.getStockName(),
                                user.getUsername(),
                                stock.getBidPrice(),
                                amount,
                                "sell"
                        ));

                        // Update the portfolioValue and the freeBudget of the user in UserByLeague
                        user.setFreeBudget(freeBudget.add(sellCost));
                        user.setPortfolioValue(user.getPortfolioValue().subtract(sellCost));

                        return new ResponseEntity<>(_userStock, HttpStatus.OK);
                    } else {
                        throw new CustomErrorException(
                                HttpStatus.CONFLICT,
                                "This user does not have enough of this stock to sell this amount");
                    }
                } else {
                    throw new CustomErrorException(HttpStatus.CONFLICT,  "This user does not own this stock");
                }
            } else {
                throw new CustomErrorException(HttpStatus.CONFLICT,  "Either stockId or userId is incorrect");
            }

        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<Integer> getStockAmountOwned(StockAmountGetInformation stock) {
        try {
            Optional<UserStock> userStock = userStockRepository.findUserStockByLeagueIdAndUserIdAndSymbol(
                    stock.getLeagueId(),
                    stock.getUserId(),
                    stock.getSymbol()
            );

            if (userStock.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "User does not own this stock");
            }

            Integer amountStockOwned = userStock.get().getAmount();
            return new ResponseEntity<>(amountStockOwned, HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}

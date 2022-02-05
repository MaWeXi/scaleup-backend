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
import java.time.LocalDate;
import java.util.Optional;

@Service
public class StockByUserService {

    final StockByUserRepository stockByUserRepository;
    final StockRepository stockRepository;
    final UserByLeagueRepository userByLeagueRepository;
    final TransactionRepository transactionRepository;

    public StockByUserService(
            StockByUserRepository stockByUserRepository,
            StockRepository stockRepository,
            UserByLeagueRepository userByLeagueRepository,
            TransactionRepository transactionRepository
    ) {
        this.stockByUserRepository = stockByUserRepository;
        this.stockRepository = stockRepository;
        this.userByLeagueRepository = userByLeagueRepository;
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<StockByUser> buyStock(StockBuy stockBuy) {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String leagueId = stockBuy.getLeagueid();
        String userId = stockBuy.getUserid();
        String symbol = stockBuy.getSymbol();
        Integer amount = stockBuy.getAmount();

        Optional<StockByUser> userStockOptional = stockByUserRepository.findUserStockByLeagueIdAndUserIdAndSymbol(
                leagueId,
                userId,
                symbol
        );
        Optional<Stock> stockOptional = stockRepository.findStockBySymbol(symbol);
        Optional<UserByLeague> userOptional = userByLeagueRepository.findByLeagueIdAndUserId(leagueId, userId);

        try {

            // Check if stockId and userid are valid
            if (stockOptional.isPresent() && userOptional.isPresent()) {

                Stock stock = stockOptional.get();
                UserByLeague user = userOptional.get();
                BigDecimal buyCost = stock.getAskPrice().multiply(BigDecimal.valueOf(amount));
                BigDecimal freeBudget = user.getFreeBudget();

                // Check whether the user has enough balance to buy this amount of stock
                if (freeBudget.compareTo(buyCost) >= 0) {
                    StockByUser stockByUser;

                    // Check if the user already owns this particular stock and either just update the amount or
                    // create a new StockByUser object to store in the DB
                    if (userStockOptional.isPresent()) {
                        Integer ownedStockAmount = userStockOptional.get().getAmount();
                        stockByUser = userStockOptional.get();
                        stockByUser.setAmount(ownedStockAmount + amount);
                        stockByUser.setTimeLastUpdated(timestamp);
                    } else {
                        stockByUser = new StockByUser(
                                leagueId,
                                userId,
                                symbol,
                                stock.getStockName(),
                                timestamp,
                                amount,
                                stock.getAskPrice().multiply(BigDecimal.valueOf(amount)));
                    }

                    // Save updated StockByUser object
                    StockByUser _StockByUser = stockByUserRepository.save(stockByUser);

                    // Create a new Transaction object and store in DB
                    transactionRepository.save(new Transaction(
                            leagueId,
                            LocalDate.now().getYear(),
                            userId,
                            timestamp,
                            symbol,
                            stock.getStockName(),
                            user.getUsername(),
                            stock.getBidPrice(),
                            amount,
                            "buy"
                    ));

                    // Update the portfolio_value and the freeBudget of the user in UserByLeague
                    user.setFreeBudget(freeBudget.subtract(buyCost));
                    user.setPortfolioValue(user.getPortfolioValue().add(buyCost));
                    userByLeagueRepository.save(user);

                    return new ResponseEntity<>(_StockByUser, HttpStatus.OK);
                } else {
                    throw new CustomErrorException(HttpStatus.CONFLICT,  "Balance too low to buy this amount");
                }
            } else {
                throw new CustomErrorException(HttpStatus.CONFLICT,  "Either stockId or userid is incorrect");
            }
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<StockByUser> sellStock(StockSell stockSell) {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String leagueId = stockSell.getLeagueid();
        String userId = stockSell.getUserid();
        String symbol = stockSell.getSymbol();
        Integer amount = stockSell.getAmount();

        Optional<StockByUser> userStockOptional = stockByUserRepository.findUserStockByLeagueIdAndUserIdAndSymbol(
                leagueId,
                userId,
                symbol
        );
        Optional<Stock> stockOptional = stockRepository.findStockBySymbol(symbol);
        Optional<UserByLeague> userOptional = userByLeagueRepository.findByLeagueIdAndUserId(leagueId, userId);

        try {

            // Check if stockId and userid are valid
            if (stockOptional.isPresent() && userOptional.isPresent()) {
                Stock stock = stockOptional.get();
                UserByLeague user = userOptional.get();
                BigDecimal sellCost = stock.getBidPrice().multiply(BigDecimal.valueOf(amount));
                BigDecimal freeBudget = user.getFreeBudget();

                if (userStockOptional.isPresent()) {
                    StockByUser stockByUser = userStockOptional.get();
                    StockByUser _StockByUser;

                    if (stockByUser.getAmount() > amount) {
                        stockByUser.setAmount(stockByUser.getAmount() - amount);
                        stockByUser.setTimeLastUpdated(timestamp);

                        // Save updated StockByUser object
                        _StockByUser = stockByUserRepository.save(stockByUser);

                    }

                    // Delete stock completely if amount equals zero
                    else if (stockByUser.getAmount().equals(amount)) {

                        stockByUserRepository.deleteUserStockByLeagueIdAndUserIdAndSymbol(leagueId, userId, symbol);
                        stockByUser.setAmount(stockByUser.getAmount() - amount);
                        stockByUser.setTimeLastUpdated(timestamp);
                        _StockByUser = stockByUser;

                    } else {
                        throw new CustomErrorException(
                                HttpStatus.CONFLICT,
                                "This user does not have enough of this stock to sell this amount");
                    }

                    // Create a new Transaction object and store in DB
                    transactionRepository.save(new Transaction(
                            leagueId,
                            LocalDate.now().getYear(),
                            userId,
                            timestamp,
                            symbol,
                            stock.getStockName(),
                            user.getUsername(),
                            stock.getAskPrice(),
                            amount,
                            "sell"
                    ));

                    // Update the portfolio_value and the freeBudget of the user in UserByLeague
                    user.setFreeBudget(freeBudget.add(sellCost));
                    user.setPortfolioValue(user.getPortfolioValue().subtract(sellCost));
                    userByLeagueRepository.save(user);

                    return new ResponseEntity<>(_StockByUser, HttpStatus.OK);

                } else {
                    throw new CustomErrorException(HttpStatus.CONFLICT,  "This user does not own this stock");
                }
            } else {
                throw new CustomErrorException(HttpStatus.CONFLICT,  "Either stockId or userid is incorrect");
            }

        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<Integer> getStockAmountOwned(StockAmountGetInformation stock) {
        try {
            Optional<StockByUser> userStock = stockByUserRepository.findUserStockByLeagueIdAndUserIdAndSymbol(
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

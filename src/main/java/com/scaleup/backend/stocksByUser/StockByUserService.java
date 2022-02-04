package com.scaleup.backend.stocksByUser;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.stock.Stock;
import com.scaleup.backend.stock.StockRepository;
import com.scaleup.backend.stocksByUser.DTO.StockAmountGetInformation;
import com.scaleup.backend.transactions.Transaction;
import com.scaleup.backend.transactions.TransactionRepository;
import com.scaleup.backend.stocksByUser.DTO.StockBuy;
import com.scaleup.backend.stocksByUser.DTO.StockSell;
import com.scaleup.backend.userByLeague.UserByLeague;
import com.scaleup.backend.userByLeague.UserByLeagueRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

@Service
public class StockByUserService {

    final StockByUserRepository stockByUserRepository;
    final TransactionRepository transactionRepository;
    final StockRepository stockRepository;
    final UserByLeagueRepository userByLeagueRepository;

    public StockByUserService(StockByUserRepository stockByUserRepository, TransactionRepository transactionRepository, StockRepository stockRepository, UserByLeagueRepository userByLeagueRepository) {
        this.stockByUserRepository = stockByUserRepository;
        this.transactionRepository = transactionRepository;
        this.stockRepository = stockRepository;
        this.userByLeagueRepository = userByLeagueRepository;
    }

    public ResponseEntity<StockByUser> buyStock(StockBuy stockBuy) {
        String leagueid = stockBuy.getLeagueId();
        String userid = stockBuy.getUserId();
        String symbol = stockBuy.getSymbol();
        Integer amount = stockBuy.getAmount();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            Optional<StockByUser> stockByUser = stockByUserRepository.findAllByLeagueIdEqualsAndUserIdEqualsAndSymbolEquals(leagueid, userid, symbol);
            Optional<Stock> stock = stockRepository.findStockBySymbol(symbol);
            Optional<UserByLeague> userByLeague = userByLeagueRepository.findAllByLeagueIdEqualsAndUserIdEquals(leagueid, userid);
            StockByUser stockByUserReturn = new StockByUser();

            //check whether user and stock exist and if user has enough freeBudget to buy the amount of stock
            if (stock.isPresent() && userByLeague.isPresent() && (userByLeague.get().getFreeBudget()).intValue()>(stock.get().getCurrentPrice().multiply(BigDecimal.valueOf(amount))).intValue()){
                BigDecimal askPrice = stock.get().getAskPrice();

                //check whether Stock is already in depot of user
                if (stockByUser.isPresent()){
                    //case: stock is already in depot of user
                    //get information and update the entity in stocksByUser for amount (he owns of the stock) and valueWhenBought
                    stockByUserReturn = stockByUser.get();
                    stockByUserReturn.setAmount(stockByUser.get().getAmount()+amount);
                    stockByUserReturn.setValueWhenBought(stockByUser.get().getValueWhenBought().add(askPrice.multiply(BigDecimal.valueOf(amount))));
                    stockByUserReturn = stockByUserRepository.save(stockByUserReturn);
                } else {
                    //case: stock is not yet in depot of user
                    //define new stockByUser, which then will be used to create a new entity in table
                    StockByUser stockByUser1 = new StockByUser();
                    stockByUser1.setLeagueId(leagueid);
                    stockByUser1.setUserId(userid);
                    stockByUser1.setSymbol(symbol);
                    stockByUser1.setAmount(amount);
                    stockByUser1.setTimeLastUpdated(timestamp);
                    stockByUser1.setValueWhenBought(askPrice.multiply(BigDecimal.valueOf(amount)));
                    stockByUserReturn = stockByUserRepository.save(stockByUser1);
                }

                //add entity in transactions
                Transaction transaction = new Transaction();
                transaction.setLeagueId(leagueid);
                transaction.setUserId(userid);
                transaction.setSymbol(symbol);
                transaction.setUsername(userByLeague.get().getUsername());
                transaction.setAmount(amount);
                transaction.setTypeOfTransaction("buy");
                transaction.setYear(timestamp.getYear());
                transaction.setTimestampTransaction(timestamp);
                transaction.setSingleStockValue(askPrice);
                transactionRepository.save(transaction);

                //reduce freeBudget and increase DepotValue
                UserByLeague userByLeagueUpdate = userByLeague.get();
                BigDecimal freeBudget = userByLeagueUpdate.getFreeBudget();
                freeBudget = freeBudget.subtract(BigDecimal.valueOf(amount).multiply(askPrice));
                userByLeagueUpdate.setFreeBudget(freeBudget);
                BigDecimal portfolioValue = userByLeagueUpdate.getPortfolioValue();
                portfolioValue = portfolioValue.add(BigDecimal.valueOf(amount).multiply(askPrice));
                userByLeagueUpdate.setPortfolioValue(portfolioValue);
                userByLeagueRepository.save(userByLeagueUpdate);
            } else {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "Der User oder die Aktie existieren nicht oder der User hat zu wenig Geld");
            }
            return new ResponseEntity<>(stockByUserReturn, HttpStatus.OK);
        }catch (Exception e){
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<StockByUser> sellStock(StockSell stockSell) {
        String leagueid = stockSell.getLeagueId();
        String userid = stockSell.getUserId();
        String symbol = stockSell.getSymbol();
        Integer amount = stockSell.getAmount();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            Optional<StockByUser> stockByUser = stockByUserRepository.findAllByLeagueIdEqualsAndUserIdEqualsAndSymbolEquals(leagueid, userid, symbol);
            Optional<Stock> stock = stockRepository.findStockBySymbol(symbol);
            Optional<UserByLeague> userByLeague = userByLeagueRepository.findAllByLeagueIdEqualsAndUserIdEquals(leagueid, userid);
            StockByUser stockByUserReturn = new StockByUser();

            //check whether user and stock exist
            if (stock.isPresent() && userByLeague.isPresent()){
                BigDecimal bidPrice = stock.get().getBidPrice();
                Boolean enoughStocks = stockByUser.get().getAmount()>=stockSell.getAmount();

                //add entity in stocksByUser
                //check whether Stock is in depot of user and if the user has enough stocks
                if (stockByUser.isPresent() && enoughStocks){
                    //case: stock is already in depot of user and user has enough stocks
                    //get information and update the entity in stocksByUser for amount (he owns of the stock) and valueWhenBought
                    stockByUserReturn = stockByUser.get();
                    stockByUserReturn.setAmount(stockByUser.get().getAmount()-amount);
                    stockByUserReturn.setValueWhenBought(stockByUser.get().getValueWhenBought().subtract(bidPrice.multiply(BigDecimal.valueOf(amount))));
                    stockByUserReturn = stockByUserRepository.save(stockByUserReturn);
                } else if (enoughStocks) {
                    //case: stock is not yet in depot of user and user has enough stocks
                    //create new entity
                    stockByUserReturn.setUserId(userid);
                    stockByUserReturn.setLeagueId(leagueid);
                    stockByUserReturn.setSymbol(symbol);
                    stockByUserReturn.setAmount(amount);
                    stockByUserReturn.setTimeLastUpdated(timestamp);
                    stockByUserReturn = stockByUserRepository.save(stockByUserReturn);
                } else {
                    //case: user doesnt have the stock or he has too less of it
                    throw new CustomErrorException(HttpStatus.NO_CONTENT, "Der User hat die Aktie nicht oder er hat zu wenig Aktien");
                }

                //add entity in transactions
                Transaction transaction = new Transaction();
                transaction.setLeagueId(leagueid);
                transaction.setUserId(userid);
                transaction.setSymbol(symbol);
                transaction.setUsername(userByLeague.get().getUsername());
                transaction.setAmount(amount);
                transaction.setTypeOfTransaction("sell");
                transaction.setYear(timestamp.getYear());
                transaction.setTimestampTransaction(timestamp);
                transaction.setSingleStockValue(bidPrice);
                transactionRepository.save(transaction);

                //add profit to freeBudget and decrease portfolioValue in userByLeague
                UserByLeague userByLeagueUpdate = userByLeague.get();
                BigDecimal budget = userByLeagueUpdate.getFreeBudget();
                budget = budget.add(BigDecimal.valueOf(amount).multiply(bidPrice));
                userByLeagueUpdate.setFreeBudget(budget);
                BigDecimal portfolioValue = userByLeagueUpdate.getPortfolioValue();
                portfolioValue = portfolioValue.subtract(BigDecimal.valueOf(amount).multiply(bidPrice));
                userByLeagueUpdate.setPortfolioValue(portfolioValue);
                userByLeagueRepository.save(userByLeagueUpdate);
            } else {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "Der User oder die Aktie existieren nicht oder der User hat zu wenig Geld");
            }
            return new ResponseEntity<>(stockByUserReturn, HttpStatus.OK);
        }catch (Exception e){
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<Integer> getStockAmountOwned(StockAmountGetInformation stockAmountGetInformation) {
        try {
            String leagueId = stockAmountGetInformation.getLeagueId();
            String userId = stockAmountGetInformation.getUserId();
            String symbol = stockAmountGetInformation.getSymbol();
            Optional<StockByUser> stockByUser = stockByUserRepository.findAllByLeagueIdEqualsAndUserIdEqualsAndSymbolEquals(leagueId, userId, symbol);
            if (stockByUser.isEmpty()) {
                throw new CustomErrorException(HttpStatus.NO_CONTENT, "Der User besitzt diese Aktie nicht");
            }
            Integer amountStockOwned = stockByUser.get().getAmount();
            return new ResponseEntity<>(amountStockOwned, HttpStatus.OK);
        } catch (Exception e) {
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}

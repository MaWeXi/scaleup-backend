package com.scaleup.backend.userByLeague;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.stock.StockRepository;
import com.scaleup.backend.stocksByUser.StockByUser;
import com.scaleup.backend.stocksByUser.StockByUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class UserByLeagueService {

    final UserByLeagueRepository userByLeagueRepository;
    final StockByUserRepository stockByUserRepository;
    final StockRepository stockRepository;

    public UserByLeagueService(UserByLeagueRepository userByLeagueRepository, StockByUserRepository stockByUserRepository, StockRepository stockRepository) {
        this.userByLeagueRepository = userByLeagueRepository;
        this.stockByUserRepository = stockByUserRepository;
        this.stockRepository = stockRepository;
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

    public ResponseEntity<BigDecimal> updateValueDepot(String leagueid, String userid){
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
}

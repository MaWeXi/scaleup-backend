package com.scaleup.backend.stock;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.stock.DTO.AskUpdate;
import com.scaleup.backend.stock.DTO.BidUpdate;
import com.scaleup.backend.stock.DTO.CurrentPriceUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public ResponseEntity<?> updateAllStocks(List<Stock> stocks) {
        try {
            stockRepository.saveAll(stocks);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {

            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<Stock> getStockBySymbol(String symbol) {
        try {
            Optional<Stock> stockOptional = stockRepository.findStockBySymbol(symbol);

            if (stockOptional.isPresent()) {
                return new ResponseEntity<>(stockOptional.get(), HttpStatus.OK);
            } else {
                throw new CustomErrorException(HttpStatus.NOT_FOUND, "Stock with this symbol could not be found");
            }
        } catch (Exception e) {

            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<BidUpdate> getBidPrice(String symbol) {
        try {
            Optional<Stock> stock = stockRepository.findStockBySymbol(symbol);
            if (stock.isPresent()){
                BidUpdate bidUpdate = new BidUpdate();
                BigDecimal bidPrice = stock.get().getBidPrice();
                bidUpdate.setBidPrice(bidPrice);
                BigDecimal dayOpen = stock.get().getDayOpen();
                bidUpdate.setBidPriceDevelopment((dayOpen.subtract(bidPrice)).divide(dayOpen, 4, RoundingMode.HALF_UP));
                //bidUpdate.setBidPriceDevelopment(BigDecimal.valueOf(12));
                return new ResponseEntity<>(bidUpdate, HttpStatus.OK);
            } else {
                throw new CustomErrorException(HttpStatus.NOT_FOUND, "Stock with this symbol could not be found");
            }
        } catch (Exception e) {
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<AskUpdate> getAskPrice(String symbol) {
        try {
            Optional<Stock> stock = stockRepository.findStockBySymbol(symbol);
            if (stock.isPresent()){
                AskUpdate askUpdate = new AskUpdate();
                BigDecimal askPrice = stock.get().getAskPrice();
                askUpdate.setAskPrice(askPrice);
                BigDecimal dayOpen = stock.get().getDayOpen();
                askUpdate.setAskPriceDevelopment((dayOpen.subtract(askPrice)).divide(dayOpen, 4, RoundingMode.HALF_UP));
                return new ResponseEntity<>(askUpdate, HttpStatus.OK);
            } else {
                throw new CustomErrorException(HttpStatus.NOT_FOUND, "Stock with this symbol could not be found");
            }
        } catch (Exception e) {
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<CurrentPriceUpdate> getCurrentPrice(String symbol) {
        try {
            Optional<Stock> stock = stockRepository.findStockBySymbol(symbol);
            if (stock.isPresent()){
                CurrentPriceUpdate currentPriceUpdate = new CurrentPriceUpdate();
                BigDecimal currentPrice = stock.get().getCurrentPrice();
                currentPriceUpdate.setCurrentPrice(currentPrice);
                BigDecimal dayOpen = stock.get().getDayOpen();
                currentPriceUpdate.setCurrentPriceDevelopment((dayOpen.subtract(currentPrice)).divide(dayOpen, 4, RoundingMode.HALF_UP));
                //bidUpdate.setBidPriceDevelopment(BigDecimal.valueOf(12));
                return new ResponseEntity<>(currentPriceUpdate, HttpStatus.OK);
            } else {
                throw new CustomErrorException(HttpStatus.NOT_FOUND, "Stock with this symbol could not be found");
            }
        } catch (Exception e) {
            // TODO: Implement logging of errors
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

//    public ResponseEntity<List<Stock>> getStocksByUserId(String userId) {
//        try {
//
//        } catch (Exception e) {
//
//            // TODO: Implement logging of errors
//            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }
}

package com.scaleup.backend.stock;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.stock.DTO.AskUpdate;
import com.scaleup.backend.stock.DTO.BidUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
                bidUpdate.setBidPriceDevelopment((dayOpen.subtract(bidPrice)).divide(dayOpen));
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
                askUpdate.setAskPriceDevelopment((dayOpen.subtract(askPrice)).divide(dayOpen));
                return new ResponseEntity<>(askUpdate, HttpStatus.OK);
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

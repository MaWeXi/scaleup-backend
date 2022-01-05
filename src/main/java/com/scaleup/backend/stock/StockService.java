package com.scaleup.backend.stock;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
            for (Stock stock:stocks) {
                System.out.println(stock);
            }
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

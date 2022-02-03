package com.scaleup.backend.stock;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.stock.DTO.AskUpdate;
import com.scaleup.backend.stock.DTO.BidUpdate;
import com.scaleup.backend.stock.DTO.StockDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/")
public class StockController {

    final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/stock")
    public ResponseEntity<?> postStockValues(@RequestBody List<Stock> stocks) {
        if (stocks.isEmpty()) {
            throw new CustomErrorException(HttpStatus.CONFLICT, "List of stocks is empty");
        }
        return stockService.updateAllStocks(stocks);
    }

    @GetMapping("/stock/{symbol}")
    public ResponseEntity<StockDTO> getStockBySymbol(@PathVariable("symbol") String symbol, @RequestParam Optional<String> interval) {
        if (interval.isEmpty()) {
            return stockService.getStockBySymbol(symbol);
        } else {
            return stockService.getStockWithHistory(symbol, interval.get());
        }
    }

    @GetMapping("stock/bidPrice/{symbol}")
    public ResponseEntity<BidUpdate> getBidPrice(@PathVariable("symbol") String symbol) {
        return stockService.getBidPrice(symbol);
    }

    @GetMapping("stock/askPrice/{symbol}")
    public ResponseEntity<AskUpdate> getAskPrice(@PathVariable("symbol") String symbol) {
        return stockService.getAskPrice(symbol);
    }
}

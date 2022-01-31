package com.scaleup.backend.stock;

import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.stock.DTO.AskUpdate;
import com.scaleup.backend.stock.DTO.BidUpdate;
import com.scaleup.backend.stock.DTO.ChartData;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class StockController {

    final StockService stockService;
    final Test test;

    public StockController(StockService stockService, Test test) {
        this.stockService = stockService;
        this.test = test;
    }

    @PostMapping("/stock")
    public ResponseEntity<?> postStockValues(@RequestBody List<Stock> stocks) {
        if (stocks.isEmpty()) {
            throw new CustomErrorException(HttpStatus.CONFLICT, "List of stocks is empty");
        }
        return stockService.updateAllStocks(stocks);
    }

    @GetMapping("/stock/{symbol}")
    public ResponseEntity<Stock> getStockBySymbol(@PathVariable("symbol") String symbol) {
        return stockService.getStockBySymbol(symbol);
    }

    @GetMapping("stock/bidPrice/{symbol}")
    public ResponseEntity<BidUpdate> getBidPrice(@PathVariable("symbol") String symbol) {
        return stockService.getBidPrice(symbol);
    }

    @GetMapping("stock/askPrice/{symbol}")
    public ResponseEntity<AskUpdate> getAskPrice(@PathVariable("symbol") String symbol) {
        return stockService.getAskPrice(symbol);
    }

    @GetMapping("stock/test")
    public ResponseEntity<List<ChartData>> getUserTest() {
        List<ChartData> list = test.getUserId();

//        for (Stock stock : rdd.collect()) {
//            System.out.println(stock);
//        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}

package com.scaleup.backend.stocksByUser;

import com.scaleup.backend.stocksByUser.DTO.StockAmountGetInformation;
import com.scaleup.backend.stocksByUser.DTO.StockBuy;
import com.scaleup.backend.stocksByUser.DTO.StockSell;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class StockByUserController {

    final StockByUserService stockByUserService;

    public StockByUserController(StockByUserService stockByUserService) {
        this.stockByUserService = stockByUserService;
    }

    @PostMapping("/stockByUser/buyStock")
    public ResponseEntity<StockByUser> buyStock(@RequestBody StockBuy stockBuy) {
        return stockByUserService.buyStock(stockBuy);
    }

    @PostMapping("/stockByUser/sellStock")
    public ResponseEntity<StockByUser> sellStock(@RequestBody StockSell stockSell) {
        return stockByUserService.sellStock(stockSell);
    }

    @GetMapping("/stockByUser/stockAmount")
    public ResponseEntity<Integer> getStockAmountOwned(@RequestBody StockAmountGetInformation stockAmountGetInformation) {
        return  stockByUserService.getStockAmountOwned(stockAmountGetInformation);
    }
}

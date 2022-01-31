package com.scaleup.backend.stocksByUser;

import com.scaleup.backend.stocksByUser.DTO.StockAmountGetInformation;
import com.scaleup.backend.stocksByUser.DTO.StockBuy;
import com.scaleup.backend.stocksByUser.DTO.StockSell;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/")
public class StockByUserController {

    final StockByUserRepository stockByUserRepository;
    final StockByUserService stockByUserService;

    public StockByUserController(StockByUserRepository stockByUserRepository, StockByUserService stockByUserService) {
        this.stockByUserRepository = stockByUserRepository;
        this.stockByUserService = stockByUserService;
    }

    @PostMapping("/stockByUser/BuyStock")
    public ResponseEntity<StockByUser> buyStock(@RequestBody StockBuy stockBuy) {
        return stockByUserService.buyStock(stockBuy);
    }

    @PostMapping("/stockByUser/SellStock")
    public ResponseEntity<StockByUser> sellStock(@RequestBody StockSell stockSell) {
        return stockByUserService.sellStock(stockSell);
    }

    @GetMapping("/stockByUser/GetStockAmountOwned")
    public ResponseEntity<Integer> getStockAmountOwned(@RequestBody StockAmountGetInformation stockAmountGetInformation) {
        return  stockByUserService.getStockAmountOwned(stockAmountGetInformation);
    }
}

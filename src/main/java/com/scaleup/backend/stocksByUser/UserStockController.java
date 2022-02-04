package com.scaleup.backend.stocksByUser;

import com.scaleup.backend.stocksByUser.DTO.StockAmountGetInformation;
import com.scaleup.backend.stocksByUser.DTO.StockBuy;
import com.scaleup.backend.stocksByUser.DTO.StockSell;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class UserStockController {

    final UserStockService userStockService;

    public UserStockController(UserStockService userStockService) {
        this.userStockService = userStockService;
    }

    @PostMapping("/userStock/buyStock")
    public ResponseEntity<UserStock> buyStock(@RequestBody StockBuy stockBuy) {
        return userStockService.buyStock(stockBuy);
    }

    @PostMapping("/userStock/sellStock")
    public ResponseEntity<UserStock> sellStock(@RequestBody StockSell stockSell) {
        return userStockService.sellStock(stockSell);
    }

    @GetMapping("/userStock/stockAmount")
    public ResponseEntity<Integer> getStockAmountOwned(@RequestBody StockAmountGetInformation stockAmountGetInformation) {
        return  userStockService.getStockAmountOwned(stockAmountGetInformation);
    }
}

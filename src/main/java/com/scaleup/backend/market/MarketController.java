package com.scaleup.backend.market;

import com.scaleup.backend.market.Market;
import com.scaleup.backend.market.MarketService;
import com.scaleup.backend.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
public class MarketController {

    final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping("/market/{league}")
    public ResponseEntity<List<Market>> getMarketByLeague(@PathVariable("league") String league) {
        return marketService.findMarketByLeague(league);
    }

    @GetMapping("market/jokeractive/{league}")
    public ResponseEntity<List<Market>> getMarketByLeagueWithJokerActive(@PathVariable("league") String league) {
        return marketService.findMarketByLeagueWithJokerActive(league);
    }

}

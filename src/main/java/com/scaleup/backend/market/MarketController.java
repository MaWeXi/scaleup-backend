package com.scaleup.backend.market;

import com.scaleup.backend.league.DTO.AddLeagueDTO;
import com.scaleup.backend.market.DTO.UpdateJoker;
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

    @GetMapping("market/joker/active/{leagueid}")
    public ResponseEntity<List<Market>> getMarketByLeagueWithJokerActive(@PathVariable("leagueid") String leagueid) {
        return marketService.findMarketByLeagueWithJokerActive(leagueid);
    }

    @PutMapping("/market/joker/update/{leagueid}")
    public ResponseEntity<Market> updateJoker(@PathVariable("leagueid") String leagueid, @RequestBody UpdateJoker updateJoker) {
        return marketService.updateJoker(leagueid, updateJoker);
    }
}

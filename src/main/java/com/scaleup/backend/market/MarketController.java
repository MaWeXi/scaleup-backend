package com.scaleup.backend.market;

import com.scaleup.backend.market.DTO.UpdateJoker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class MarketController {

    final MarketRepository marketRepository;
    final MarketService marketService;

    public MarketController(MarketRepository marketRepository, MarketService marketService) {
        this.marketRepository = marketRepository;
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

    @PostMapping("/market/addEntity")
    public ResponseEntity<Market> addEntity(@RequestBody Market market) {
        return new ResponseEntity<>(marketRepository.save(market), HttpStatus.OK);
    }
}

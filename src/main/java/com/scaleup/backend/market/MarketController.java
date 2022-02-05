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

    // For development purposes only
    @GetMapping("market/joker/active/{leagueId}")
    public ResponseEntity<List<Market>> getMarketByLeagueWithJokerActive(@PathVariable("leagueId") String leagueId) {
        return marketService.findMarketByLeagueWithJokerActive(leagueId);
    }

    @PutMapping("/market/joker/update/{leagueId}")
    public ResponseEntity<Market> updateJoker(@PathVariable("leagueId") String leagueId, @RequestBody UpdateJoker updateJoker) {
        return marketService.updateJoker(leagueId, updateJoker);
    }

    // For development purposes only
    @PostMapping("/market/addEntity")
    public ResponseEntity<Market> addEntity(@RequestBody Market market) {
        return new ResponseEntity<>(marketRepository.save(market), HttpStatus.OK);
    }
}

package com.scaleup.backend.transactions;

import com.scaleup.backend.transactions.DTO.TransactionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/")
public class TransactionController {

    final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions/league/all/{leagueId}")
    public ResponseEntity<List<TransactionDTO>> getTransaction(@PathVariable String leagueId) {
        return transactionService.getTransactionsByLeague(leagueId);
    }
}

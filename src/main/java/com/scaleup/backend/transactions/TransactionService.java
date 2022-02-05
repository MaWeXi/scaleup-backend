package com.scaleup.backend.transactions;


import com.scaleup.backend.exceptionHandling.CustomErrorException;
import com.scaleup.backend.transactions.DTO.TransactionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<List<TransactionDTO>> getTransactionsByLeague(String leagueId) {

        try {
            List<TransactionDTO> transactionDTOList = transactionRepository.findTransactionByLeagueIdAndYear(
                    leagueId,
                    LocalDate.now().getYear()
            ).stream().map(transaction ->
                    new TransactionDTO(
                    transaction.getUsername(),
                    transaction.getTimestampTransaction(),
                    transaction.getSymbol(),
                    transaction.getStockName(),
                    transaction.getSingleStockValue(),
                    transaction.getAmount(),
                    transaction.getTypeOfTransaction())).collect(Collectors.toList());
            return new ResponseEntity<>(transactionDTOList, HttpStatus.OK);
        } catch (Exception e) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}

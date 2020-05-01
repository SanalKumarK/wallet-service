package com.leovegas.wallet.api;

import com.leovegas.wallet.entities.Player;
import com.leovegas.wallet.entities.Transaction;
import com.leovegas.wallet.entities.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    PlayerController playerController;

    @Autowired
    TransactionController transactionController;

    @GetMapping(value = "/balance")
    public ResponseEntity<BigDecimal> getBalanceByName(@RequestParam(name = "name") String name) {
        List<Player> players = playerController.findByFirstName(name);
        if(!players.isEmpty()) {
            return ResponseEntity.ok(players.get(0).getBalance());
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/transaction")
    public void processTransaction(@RequestBody Transaction transaction) {
        if(validateTransaction(transaction)) {
            transactionController.save(transaction);
            applyTransaction(transaction);
        }
    }

    /*TODO Manage paging*/
    @GetMapping(value = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Transaction> getTransactionHistory(@RequestParam(name = "player") String name,
                                                   @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                   @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Instant startTime = start.toInstant(ZoneOffset.UTC), endTime = null;
        if(end == null) {
            endTime = Instant.now();
        } else {
            endTime = end.toInstant(ZoneOffset.UTC);
        }

        List<Player> playerList = playerController.findByFirstName(name);
        if(!playerList.isEmpty()) {
            Player player = playerList.get(0);
            return transactionController.findByPlayerIdAndDateBetweenOrderByDateDesc(player.getId(),
                    startTime, endTime);
            //return transactionController.findByPlayerIdOrderByDate(player.getId());
        }
        return null;
    }

    private void applyTransaction(Transaction transaction) {
        Optional<Player> playerOpt = playerController.findById(transaction.getPlayerId());
        if(playerOpt.isPresent()) {
            Player player = playerOpt.get();
            if(transaction.getType().equals(TransactionType.DEBIT)) {
                player.setBalance(player.getBalance().subtract(transaction.getAmount()));
            }
            if(transaction.getType().equals(TransactionType.CREDIT)) {
                player.setBalance(transaction.getAmount().add(player.getBalance()));
            }
            playerController.save(player);
        }
    }

    private boolean validateTransaction(Transaction transaction) {
        try {
            Objects.requireNonNull(transaction);
            if(transaction.getType().equals(TransactionType.DEBIT)) {
                Optional<Player> player = playerController.findById(transaction.getPlayerId());
                if(player.isPresent()) {
                    return (player.get().getBalance().compareTo(transaction.getAmount()) >= 0) ? true : false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
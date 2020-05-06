package com.leovegas.wallet.api;

import com.leovegas.wallet.api.entities.ApiError;
import com.leovegas.wallet.api.entities.Response;
import com.leovegas.wallet.api.entities.ResponseStatusType;
import com.leovegas.wallet.repository.entities.Account;
import com.leovegas.wallet.repository.entities.Player;
import com.leovegas.wallet.repository.entities.Transaction;
import com.leovegas.wallet.repository.entities.TransactionType;
import com.leovegas.wallet.service.PlayerService;
import com.leovegas.wallet.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PlayerService playerService;

    /**
     * Return balance information of the given player id.
     * @param playerId Player Id
     * @return Balance information in 1000 USD format.
     */
    @GetMapping(value = "/balance")
    public ResponseEntity<String> getBalanceById(@RequestParam(name = "id") String playerId) {
        Player player = playerService.findPlayer(playerId);
        if(player != null) {
            Account account = player.getAccount();
            return ResponseEntity.ok(account.getBalance() + " " + account.getCurrency());
        } else {
            logger.info(ApiError.PLAYER_NOT_FOUND.message() +" "+playerId);
            return new ResponseEntity(null, HttpStatus.ACCEPTED);
        }
    }

    /**
     * Given transaction is processed, and persisted to the db.
     * @param transaction Transaction as JSON.
     * @return Success or Failure with error message.
     */
    @PostMapping(value = "/transaction")
    public ResponseEntity<Response> processTransaction(@RequestBody Transaction transaction) {
        Response respone = validateTransaction(transaction);
        try{
            if(respone == null && transactionService.saveTransaction(transaction)) {
                return ResponseEntity.ok(new Response(ResponseStatusType.SUCCESS));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return new ResponseEntity<>(respone,HttpStatus.ACCEPTED);
    }

    /**
     * Returns history of transaction for the given playerId.
     * @param playerId Player Id.
     * @param startTime Start Time when history is required
     * @param endTime End time till when history is required.
     * @return List of Transaction ordered in last transaction first.
     */
    @GetMapping(value = "/transaction")
    public List<Transaction> getTransactionHistory(@RequestParam(name = "id") String playerId,
                                                   @RequestParam(name = "start")
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
                                                   @RequestParam(name = "end", required = false)
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        Account account = playerService.findAccount(playerId);
        if(account != null) {
            return transactionService.getTransactions(account.getAccountNumber(), startTime, endTime);
        }
        logger.info("No transaction history found for " +" "+playerId);
        return null;
    }

    /**
     * Validate the transaction.
     * @param transaction
     * @return If invalid return Response object with error message
     *         If valid, return null.
     */
    private Response validateTransaction(Transaction transaction) {
        Response response;
        try {
            if(transactionService.isTransactionWithSameIdExist(transaction.getTransactionId())) {
                return new Response(ResponseStatusType.FAILURE, ApiError.DUPLICATE_TRANSACTION_ID.message());
            }
            Player player = playerService.findPlayerFromAccountNumber(transaction.getAccountNumber());
            if(player == null) {
                return new Response(ResponseStatusType.FAILURE, ApiError.UNKNOWN_ACCOUNT.message());
            } else if(transaction.getType().equals(TransactionType.DEBIT) && (player.getAccount().getBalance()
                    .compareTo(transaction.getAmount()) < 0)) {
                return new Response(ResponseStatusType.FAILURE, ApiError.NOT_ENOUGH_BALANCE.message());
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage());
            response = new Response(ResponseStatusType.FAILURE, e.getMessage());
        }
        return response;
    }
}
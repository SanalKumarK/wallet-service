package com.leovegas.wallet.api;

import com.leovegas.wallet.AbstractBaseTest;
import com.leovegas.wallet.api.entities.ApiError;
import com.leovegas.wallet.api.entities.Response;
import com.leovegas.wallet.api.entities.ResponseStatusType;
import com.leovegas.wallet.repository.entities.Player;
import com.leovegas.wallet.repository.entities.Transaction;
import com.leovegas.wallet.repository.entities.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WalletControllerTest extends AbstractBaseTest {

    private static final String ACCOUNT_NUMBER = "09876543210987654";
    private static final String PLAYER_ID = "george_don";

    @Autowired
    PlayerController playerController;

    @Autowired
    WalletController walletController;

    private Player player;

    @BeforeAll
    void setUp() {
        player = createDummyPlayer(PLAYER_ID, ACCOUNT_NUMBER);
        ResponseEntity<Response> response = playerController.createPlayer(player);
        Assertions.assertEquals(ResponseStatusType.SUCCESS, response.getBody().getStatus());
    }


    @Test
    void processTransactionCredit() {
        BigDecimal creditAmount = new BigDecimal("1000.00");
        Transaction transaction = createTransaction(ACCOUNT_NUMBER,1000L, TransactionType.CREDIT, creditAmount);
        ResponseEntity<Response> responseEntity = walletController.processTransaction(transaction);

        Assertions.assertEquals(ResponseStatusType.SUCCESS, responseEntity.getBody().getStatus());
        ResponseEntity<String> balance = walletController.getBalanceById(PLAYER_ID);
        Assertions.assertNotNull(balance);
        Assertions.assertEquals(player.getAccount().getBalance().add(creditAmount)+" " +
                        player.getAccount().getCurrency(), balance.getBody());
    }

    @Test
    void processTransactionDebit() {
        BigDecimal debitAmount = new BigDecimal("1000.00");
        Transaction transaction = createTransaction(ACCOUNT_NUMBER,1001L, TransactionType.DEBIT, debitAmount);
        ResponseEntity<Response> responseEntity = walletController.processTransaction(transaction);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(ResponseStatusType.SUCCESS, responseEntity.getBody().getStatus());
        ResponseEntity<String> balance = walletController.getBalanceById(PLAYER_ID);
        Assertions.assertNotNull(balance);
        Assertions.assertEquals("1000.00 USD", balance.getBody());
    }

    @Test
    void processDuplicateTransactionWithSameId() {
        BigDecimal creditAmount = new BigDecimal("1000.00");
        Transaction transaction = createTransaction(ACCOUNT_NUMBER,2000L, TransactionType.CREDIT, creditAmount);
        ResponseEntity<Response> responseEntity = walletController.processTransaction(transaction);

        transaction = createTransaction(ACCOUNT_NUMBER,2000L, TransactionType.CREDIT, creditAmount);
        responseEntity = walletController.processTransaction(transaction);

        Assertions.assertEquals(ResponseStatusType.FAILURE, responseEntity.getBody().getStatus());
        Assertions.assertEquals(ApiError.DUPLICATE_TRANSACTION_ID.message(), responseEntity.getBody().getError());
    }

    @Test
    void getTransactionHistory() {
        String playerId = "george_";
        String accountNumber = ACCOUNT_NUMBER.replace('0','1');
        Player player = createDummyPlayer(playerId, accountNumber);
        player.setPlayerId(playerId);
        player.getAccount().setAccountNumber(accountNumber);
        ResponseEntity<Response> response = playerController.createPlayer(player);
        Assertions.assertEquals(ResponseStatusType.SUCCESS, response.getBody().getStatus());

        BigDecimal creditAmount = new BigDecimal("1000.00");
        Transaction transaction = createTransaction(accountNumber,1003L, TransactionType.CREDIT, creditAmount);
        transaction.setAccountNumber(accountNumber);
        ResponseEntity<Response> responseEntity = walletController.processTransaction(transaction);
        Assertions.assertNotNull(responseEntity);

        BigDecimal debitAmount = new BigDecimal("1000.00");
        transaction = createTransaction(accountNumber,1004L, TransactionType.DEBIT, debitAmount);
        transaction.setAccountNumber(accountNumber);
        responseEntity = walletController.processTransaction(transaction);
        Assertions.assertNotNull(responseEntity);

        Instant start = Instant.now().minusSeconds(5*60);
        Instant end = Instant.now().plusSeconds(5*60);
        List<Transaction> transactionHistory = walletController.getTransactionHistory(playerId, start, end);

        Assertions.assertNotNull(transactionHistory);
        Assertions.assertEquals(2, transactionHistory.size());
    }
}
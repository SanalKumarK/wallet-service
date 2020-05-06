package com.leovegas.wallet.service;

import com.leovegas.wallet.AbstractBaseTest;
import com.leovegas.wallet.repository.entities.Player;
import com.leovegas.wallet.repository.entities.Transaction;
import com.leovegas.wallet.repository.entities.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionServiceTest extends AbstractBaseTest {

    private static final String ACCOUNT_NUMBER = "2394829347972344";
    private static final String PLAYER_ID = "fernandes_dsouza";
    private static final Long TRANS_ID = 5000L;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PlayerService playerService;

    private Transaction transaction;
    private Player player;

    @BeforeAll
    void setUp() {
        player = createDummyPlayer(PLAYER_ID, ACCOUNT_NUMBER);
        playerService.savePlayer(player);
        transaction = createTransaction(ACCOUNT_NUMBER,
                TRANS_ID, TransactionType.CREDIT, BigDecimal.valueOf(1000L));
    }

    @Test
    void saveTransaction() {
        Assertions.assertTrue(transactionService.saveTransaction(transaction));
    }

    @Test
    void isTransactionWithSameIdExist() {
        Assertions.assertTrue(transactionService.isTransactionWithSameIdExist(TRANS_ID));
    }

    @Test
    void getTransactions() {
        String playerId = "alice_bob";
        String accountNumber = "8597363738292012";
        Player player = createDummyPlayer(playerId, accountNumber);
        playerService.savePlayer(player);
        Transaction transaction1 = createTransaction(accountNumber,
                6000L, TransactionType.CREDIT, BigDecimal.valueOf(1000L));
        Assertions.assertTrue(transactionService.saveTransaction(transaction1));

        Transaction transaction2 = createTransaction(accountNumber,
                6001L, TransactionType.DEBIT, BigDecimal.valueOf(1000L));
        Assertions.assertTrue(transactionService.saveTransaction(transaction2));

        List<Transaction> transactions = transactionService.getTransactions(accountNumber, Instant.now().minusSeconds(60 * 2),
                Instant.now().plusSeconds(60 * 2));
        Assertions.assertNotNull(transactions);
        Assertions.assertEquals(2, transactions.size());
        Assertions.assertEquals(accountNumber, transactions.get(0).getAccountNumber());
    }
}
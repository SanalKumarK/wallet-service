package com.leovegas.wallet;

import com.leovegas.wallet.repository.entities.Account;
import com.leovegas.wallet.repository.entities.Player;
import com.leovegas.wallet.repository.entities.Transaction;
import com.leovegas.wallet.repository.entities.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public class AbstractBaseTest {

    public Player createDummyPlayer(String playerId, String accountNumber) {
        Player player = new Player();
        player.setPlayerId(playerId);
        player.setFirstName("Jacob");
        player.setLastName("Fernandes");
        player.setAge(50);
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(new BigDecimal("1000.00"));
        account.setCurrency("USD");
        player.setAccount(account);
        return player;
    }

    public Transaction createTransaction(String accountNumber,
                                  Long transId, TransactionType type, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transId);
        transaction.setAccountNumber(accountNumber);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDate(Instant.now());
        return transaction;
    }
}

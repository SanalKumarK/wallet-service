package com.leovegas.wallet.service;

import com.leovegas.wallet.repository.AccountRepository;
import com.leovegas.wallet.repository.TransactionRepository;
import com.leovegas.wallet.repository.entities.Account;
import com.leovegas.wallet.repository.entities.Transaction;
import com.leovegas.wallet.repository.entities.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Given transaction is persisted, and updates the balance information
     * of the player.
     * @param transaction Transaction detail.
     * @return true if transaction is processed successfully or false.
     */
    @Transactional
    public boolean saveTransaction(Transaction transaction) {
        Objects.requireNonNull(transaction);
        Objects.requireNonNull(transaction.getTransactionId());
        Objects.requireNonNull(transaction.getAccountNumber());

        if(transactionRepository.save(transaction) != null) {
            if(applyTransaction(transaction) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any transaction with given id exist in the Transaction.
     * @param transId transaction id to check.
     * @return true if transaction with given id exist.
     */
    public boolean isTransactionWithSameIdExist(Long transId) {
        return transactionRepository.existsById(transId);
    }

    /**
     * Find all the transactions of the given account number.
     * @param accountNumber Account number.
     * @param start Start time when history is required.
     * @param end End time till when history is required.
     * @return List of transactions associated with the account number.
     */
    public List<Transaction> getTransactions(String accountNumber, Instant start, Instant end) {
        Objects.requireNonNull(accountNumber);

        return transactionRepository.findAllByAccountNumberAndDateBetweenOrderByDateDesc(accountNumber,
                start, end);
    }

    /**
     * When a transaction is received in the system, updates the account
     * with the balance information.
     * @param transaction
     * @return Account associated with the player.
     */
    private Account applyTransaction(Transaction transaction) {
        Optional<Account> accountOpt = accountRepository.findById(transaction.getAccountNumber());
        if(accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if(transaction.getType().equals(TransactionType.DEBIT)) {
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            }
            if(transaction.getType().equals(TransactionType.CREDIT)) {
                account.setBalance(transaction.getAmount().add(account.getBalance()));
            }
            return accountRepository.save(account);
        }
        return null;
    }
}
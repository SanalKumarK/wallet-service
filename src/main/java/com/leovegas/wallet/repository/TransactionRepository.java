package com.leovegas.wallet.repository;

import com.leovegas.wallet.repository.entities.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findAllByAccountNumberAndDateBetweenOrderByDateDesc(String accountNumber, Instant start, Instant end);
}
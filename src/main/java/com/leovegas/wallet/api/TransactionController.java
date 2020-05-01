package com.leovegas.wallet.api;

import com.leovegas.wallet.entities.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "transaction", path = "transaction")
public interface TransactionController extends PagingAndSortingRepository<Transaction, Long> {

    List<Transaction> findByPlayerIdOrderByDate(@Param("playerId") long playerId);

    List<Transaction> findByPlayerIdAndDateBetweenOrderByDateDesc(@Param("playerId") long playerId,
                                                                  @Param("date") Instant start, @Param("date") Instant end);
}
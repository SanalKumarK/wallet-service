package com.leovegas.wallet.repository;

import com.leovegas.wallet.repository.entities.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, String> {
}
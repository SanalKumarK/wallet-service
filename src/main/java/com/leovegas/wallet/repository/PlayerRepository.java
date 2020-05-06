package com.leovegas.wallet.repository;

import com.leovegas.wallet.repository.entities.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlayerRepository extends CrudRepository<Player, String> {
    Optional<Player> findByAccount_AccountNumber(String s);
}
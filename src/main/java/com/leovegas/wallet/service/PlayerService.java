package com.leovegas.wallet.service;

import com.leovegas.wallet.repository.AccountRepository;
import com.leovegas.wallet.repository.PlayerRepository;
import com.leovegas.wallet.repository.entities.Account;
import com.leovegas.wallet.repository.entities.Player;
import com.leovegas.wallet.repository.entities.PlayerStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Find the player from the given playerId.
     * @param playerId Player Id.
     * @return Player.
     */
    public Player findPlayer(String playerId) {
        Objects.requireNonNull(playerId);
        return playerRepository.findById(playerId).orElse(null);
    }

    /**
     * Find the account of the given playerId.
     * @param playerId Player Id.
     * @return Account associated with the player.
     */
    public Account findAccount(String playerId) {
        Objects.requireNonNull(playerId);
        Player player = playerRepository.findById(playerId).orElse(null);
        if(player != null) {
            return player.getAccount();
        }
        return null;
    }

    /**
     * Find player from the given account number.
     * @param accountNumber account number of the player.
     * @return Player associated with the account.
     */
    public Player findPlayerFromAccountNumber(String accountNumber) {
        Objects.requireNonNull(accountNumber);
        return playerRepository.findByAccount_AccountNumber(accountNumber).orElse(null);
    }

    /**
     * Check if player with given id exist.
     * @param playerId Player Id.
     * @return True or False if Player exist.
     */
    public boolean isPlayerExist(String playerId) {
        Objects.requireNonNull(playerId);
        return playerRepository.existsById(playerId);
    }

    /**
     * Persist the player and accont to the db.
     * @param player Player.
     * @return Player info.
     */
    @Transactional
    public Player savePlayer(Player player) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(player.getPlayerId());
        if(player.getStatus() == null) {
            player.setStatus(PlayerStatusType.ACTIVE);
        }
        if(player.getAccount() != null) {
            saveAccount(player.getAccount());
        }
        return playerRepository.save(player);
    }

    private Account saveAccount(Account account) {
        Objects.requireNonNull(account);
        Objects.requireNonNull(account.getAccountNumber());
        return accountRepository.save(account);
    }
}
package com.leovegas.wallet.service;

import com.leovegas.wallet.AbstractBaseTest;
import com.leovegas.wallet.repository.entities.Account;
import com.leovegas.wallet.repository.entities.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayerServiceTest extends AbstractBaseTest {

    private static final String ACCOUNT_NUMBER = "8872634723472834";
    private static final String PLAYER_ID = "jacob_dsouza";

    @Autowired
    private PlayerService playerService;

    private Player player;

    @BeforeAll
    void setUp() {
        player = createDummyPlayer(PLAYER_ID, ACCOUNT_NUMBER);
        playerService.savePlayer(player);
    }

    @Test
    void savePlayer() {
        String playerId = "jacob_d";
        Player player1 = createDummyPlayer(playerId, "9872634723472834");
        Player player_saved = playerService.savePlayer(player1);
        Assertions.assertNotNull(player_saved);
        Assertions.assertEquals(playerId, player_saved.getPlayerId());
    }

    @Test
    void findPlayer() {
        Player player1 = playerService.findPlayer(PLAYER_ID);
        Assertions.assertNotNull(player1);
        Assertions.assertEquals(PLAYER_ID, player1.getPlayerId());
    }

    @Test
    void findAccount() {
        Account account = playerService.findAccount(PLAYER_ID);
        Assertions.assertNotNull(account);
        Assertions.assertEquals(ACCOUNT_NUMBER, account.getAccountNumber());
    }

    @Test
    void findPlayerFromAccountNumber() {
        Player player1 = playerService.findPlayerFromAccountNumber(ACCOUNT_NUMBER);
        Assertions.assertNotNull(player1);
        Assertions.assertEquals(PLAYER_ID, player1.getPlayerId());
    }

    @Test
    void isPlayerExist() {
        Assertions.assertTrue(playerService.isPlayerExist(PLAYER_ID));
    }

}
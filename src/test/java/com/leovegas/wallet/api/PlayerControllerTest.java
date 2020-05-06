package com.leovegas.wallet.api;

import com.leovegas.wallet.AbstractBaseTest;
import com.leovegas.wallet.api.entities.ApiError;
import com.leovegas.wallet.api.entities.Response;
import com.leovegas.wallet.api.entities.ResponseStatusType;
import com.leovegas.wallet.repository.entities.Player;
import com.leovegas.wallet.repository.entities.PlayerStatusType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class PlayerControllerTest extends AbstractBaseTest {

    public static final String ACCOUNT_NUMBER = "1234567890123456";
    
    @Autowired
    PlayerController playerController;

    @Test
    void findPlayerNotFound() {
        ResponseEntity<Player> response = playerController.findPlayer("jacob_fern");
        Assertions.assertNull(response.getBody());
        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void createPlayer() {
        String acc = "3234567890123456";
        Player player = createDummyPlayer("jacob_fern1", acc);
        ResponseEntity<Response> response = playerController.createPlayer(player);
        Assertions.assertEquals(ResponseStatusType.SUCCESS, response.getBody().getStatus());
    }

    @Test
    void updatePlayer() {
        String acc = "2234567890123456";
        String playerId = "jacob_fern2";
        Player player = createDummyPlayer(playerId, acc);
        ResponseEntity<Response> response = playerController.createPlayer(player);
        Assertions.assertEquals(ResponseStatusType.SUCCESS, response.getBody().getStatus());

        player.setStatus(PlayerStatusType.SUSPENDED);
        response = playerController.createPlayer(player);
        Assertions.assertEquals(ResponseStatusType.SUCCESS, response.getBody().getStatus());
        ResponseEntity<Player> check = playerController.findPlayer(playerId);
        Assertions.assertEquals(PlayerStatusType.SUSPENDED, check.getBody().getStatus());
    }

    @Test
    void findPlayer() {
        String playerId = "jacob_fern3";
        Player player = createDummyPlayer(playerId, ACCOUNT_NUMBER);
        ResponseEntity<Response> response = playerController.createPlayer(player);
        Assertions.assertEquals(ResponseStatusType.SUCCESS, response.getBody().getStatus());

        ResponseEntity<Player> playerResponse = playerController.findPlayer(playerId);
        Assertions.assertNotNull(playerResponse.getBody());
        Assertions.assertEquals(playerId, playerResponse.getBody().getPlayerId());
        Assertions.assertEquals(PlayerStatusType.ACTIVE, playerResponse.getBody().getStatus());
        Assertions.assertEquals(HttpStatus.OK, playerResponse.getStatusCode());
    }

    @Test
    void createPlayerEmptyAccount() {
        Player player = createDummyPlayer("jacob_fern4", null);
        player.setAccount(null);

        ResponseEntity<Response> response = playerController.createPlayer(player);
        Assertions.assertEquals(ResponseStatusType.FAILURE, response.getBody().getStatus());
        Assertions.assertEquals(ApiError.INVALID_ACCOUNT.message(), response.getBody().getError());
    }
}
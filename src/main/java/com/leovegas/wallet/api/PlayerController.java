package com.leovegas.wallet.api;

import com.leovegas.wallet.api.entities.ApiError;
import com.leovegas.wallet.api.entities.Response;
import com.leovegas.wallet.api.entities.ResponseStatusType;
import com.leovegas.wallet.repository.entities.Player;
import com.leovegas.wallet.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/player")
public class PlayerController {

    private final Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    PlayerService playerService;

    /**
     * Returns the player information, if the player with given id exists.
     * @param playerId Player Id.
     * @return Player if exist or null.
     */
    @GetMapping
    public ResponseEntity<Player> findPlayer(@RequestParam(name = "id") String playerId) {
        Player player = playerService.findPlayer(playerId);
        if(player != null) {
            return ResponseEntity.ok(player);
        }
        logger.info(ApiError.PLAYER_NOT_FOUND.message()+" " + playerId);
        return new ResponseEntity(null, HttpStatus.ACCEPTED);
    }

    /**
     * Create a player, based on the given details.
     * @param player Player as JSON.
     * @return Success or Failure with error message.
     */
    @PostMapping
    public ResponseEntity<Response> createPlayer(@RequestBody Player player) {
        Response response = validPlayer(player);
        //If player exists, update the player information.
        if(playerService.isPlayerExist(player.getPlayerId()) && playerService.savePlayer(player)!=null) {
            return ResponseEntity.ok(new Response(ResponseStatusType.SUCCESS, "Successully updated the player."));
        } else if(response == null && playerService.savePlayer(player)!=null) {
            return ResponseEntity.ok(new Response(ResponseStatusType.SUCCESS, "Successfully created the player."));
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Validate the player.
     * @param player
     * @return if player is valid, returns null, If player is invalid a Response
     * containing details will return.
     */
    private Response validPlayer(Player player) {
        if(player == null || player.getPlayerId() == null || player.getPlayerId().isBlank()) {
            return new Response(ResponseStatusType.FAILURE, ApiError.INVALID_PLAYER.message());
        } else if(!playerService.isPlayerExist(player.getPlayerId()) &&
                (player.getAccount() == null || player.getAccount().getAccountNumber() == null ||
                        player.getAccount().getAccountNumber().isBlank())) {
            return new Response(ResponseStatusType.FAILURE, ApiError.INVALID_ACCOUNT.message());
        } else if(playerService.findPlayerFromAccountNumber(player.getAccount().getAccountNumber()) != null) {
            return new Response(ResponseStatusType.FAILURE, ApiError.DUPLICATE_ACCOUNT_NUM.message());
        } else {
            return null;
        }
    }
}
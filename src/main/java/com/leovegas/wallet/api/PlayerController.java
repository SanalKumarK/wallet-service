package com.leovegas.wallet.api;

import com.leovegas.wallet.entities.Player;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "player", path = "player")
public interface PlayerController extends PagingAndSortingRepository<Player, Long> {
    List<Player> findByFirstName(@Param("name") String name);
}

package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.Query;
import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;

import java.util.Optional;

public interface UserRepository extends IRepository<UserEntity, Integer> {

    boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT u.id FROM UserEntity u WHERE u.username = :username")
    Optional<Integer> findIdByUsername(String username);
}

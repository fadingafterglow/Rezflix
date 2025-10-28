package ua.edu.ukma.springers.rezflix.repositories;

import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;

import java.util.Optional;

public interface UserRepository extends IRepository<UserEntity, Integer> {

    boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);
}

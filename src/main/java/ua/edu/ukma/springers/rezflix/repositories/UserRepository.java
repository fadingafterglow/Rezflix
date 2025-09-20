package ua.edu.ukma.springers.rezflix.repositories;

import ua.edu.ukma.springers.rezflix.domain.entities.UserEntity;

public interface UserRepository extends IRepository<UserEntity, Integer> {

    boolean existsByUsername(String username);
}

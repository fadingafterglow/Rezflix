package ua.edu.ukma.springers.rezflix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

import java.util.Optional;

@NoRepositoryBean
public interface IRepository<E extends IGettableById<ID>, ID extends Comparable<ID>> extends JpaRepository<E, ID> {

    Optional<E> findByIdFetchAll(ID id);
}

package ua.edu.ukma.springers.rezflix.services;

import org.springframework.lang.NonNull;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

public interface ICRUDService<E extends IGettableById<I>, CV, UV, I extends Comparable<I>> {

    E getById(@NonNull I id);

    I create(@NonNull CV view);

    boolean update(@NonNull I id, @NonNull UV view);

    void delete(@NonNull I id);
}

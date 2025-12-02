package ua.edu.ukma.springers.rezflix.events;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

@ToString
@EqualsAndHashCode(callSuper = true)
public class DeleteEntityEvent<E extends IGettableById<I>, I extends Comparable<I>> extends EntityEvent<E, I> {

    public DeleteEntityEvent(E entity) {
        super(entity);
    }
}
package ua.edu.ukma.springers.rezflix.events;

import lombok.*;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class EntityEvent<E extends IGettableById<I>, I extends Comparable<I>> implements ResolvableTypeProvider {

    private final E entity;

    public I getId() {
        return entity.getId();
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(
                getClass(),
                ResolvableType.forInstance(entity),
                ResolvableType.forInstance(entity.getId())
        );
    }
}

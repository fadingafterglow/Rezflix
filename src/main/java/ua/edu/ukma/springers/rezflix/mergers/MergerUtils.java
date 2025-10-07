package ua.edu.ukma.springers.rezflix.mergers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;
import ua.edu.ukma.springers.rezflix.repositories.IRepository;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MergerUtils {

    public static <ID extends Comparable<ID>, RELATED extends IGettableById<ID>> void mergeRelatedId(
            ID id,
            IRepository<RELATED, ID> repository,
            Consumer<RELATED> setter,
            Supplier<? extends RuntimeException> exceptionSupplier
    ) {
        if (id != null)
            setter.accept(repository.findById(id).orElseThrow(exceptionSupplier));
        else
            setter.accept(null);
    }

    public static <ID extends Comparable<ID>, RELATED extends IGettableById<ID>> void mergeRelatedIds(
            Collection<ID> ids,
            IRepository<RELATED, ID> repository,
            Consumer<List<RELATED>> setter,
            Supplier<? extends RuntimeException> exceptionSupplier
    ) {
        if (CollectionUtils.isEmpty(ids)) {
            setter.accept(new ArrayList<>());
        } else {
            Set<ID> idSet = new HashSet<>(ids);
            List<RELATED> entities = repository.findAllById(idSet);
            if (entities.size() != idSet.size())
                throw exceptionSupplier.get();
            setter.accept(entities);
        }
    }
}

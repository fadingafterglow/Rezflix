package ua.edu.ukma.springers.rezflix.domain.interfaces;

import org.springframework.lang.Nullable;

public interface IGettableById<ID extends Comparable<ID>> {
    ID getId();
    void setId(@Nullable ID id);
}

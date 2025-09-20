package ua.edu.ukma.springers.rezflix.domain.interfaces;

public interface IGettableById<ID extends Comparable<ID>> {
    ID getId();
}

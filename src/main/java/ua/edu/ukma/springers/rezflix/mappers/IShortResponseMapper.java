package ua.edu.ukma.springers.rezflix.mappers;

public interface IShortResponseMapper<E, R> {
    R toShortResponse(E entity);
}

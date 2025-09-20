package ua.edu.ukma.springers.rezflix.mappers;

public interface IResponseMapper<E, R> {
    R toResponse(E entity);
}

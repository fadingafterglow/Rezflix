package ua.edu.ukma.springers.rezflix.mappers;

import java.util.List;

public interface IListResponseMapper<E, LR> {
    LR toListResponse(Long total, List<E> items);
}
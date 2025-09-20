package ua.edu.ukma.springers.rezflix.mappers;

import java.util.List;

public interface IListResponseMapper<E, LR> {
    LR toListResponse(long total, List<E> items);
}
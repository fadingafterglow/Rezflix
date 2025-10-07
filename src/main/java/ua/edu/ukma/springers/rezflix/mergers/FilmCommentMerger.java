package ua.edu.ukma.springers.rezflix.mergers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.CreateCommentDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.UpdateCommentDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmCommentEntity;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;
import ua.edu.ukma.springers.rezflix.repositories.FilmRepository;
import ua.edu.ukma.springers.rezflix.utils.TimeUtils;

import static ua.edu.ukma.springers.rezflix.mergers.MergerUtils.mergeRelatedId;

@Component
@RequiredArgsConstructor
public class FilmCommentMerger implements IMerger<FilmCommentEntity, CreateCommentDto, UpdateCommentDto> {
    private final FilmRepository filmRepo;

    @Override
    public void mergeForCreate(FilmCommentEntity entity, CreateCommentDto view) {
        mergeRelatedId(
            view.getFilmId(), filmRepo, entity::setFilm,
            () -> new ValidationException("error.film_comment.film.not_existent")
        );
        entity.setText(view.getText());
        entity.setCreatedAt(TimeUtils.getCurrentDateTimeUTC());
    }

    @Override
    public void mergeForUpdate(FilmCommentEntity entity, UpdateCommentDto view) {
        entity.setText(view.getText());
    }
}

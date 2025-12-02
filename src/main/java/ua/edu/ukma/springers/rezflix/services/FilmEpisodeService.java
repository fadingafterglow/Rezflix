package ua.edu.ukma.springers.rezflix.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.InputStreamResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.*;
import ua.edu.ukma.springers.rezflix.criteria.FilmEpisodeCriteria;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmDubbingEntity;
import ua.edu.ukma.springers.rezflix.domain.entities.FilmEpisodeEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FilmEpisodeStatus;
import ua.edu.ukma.springers.rezflix.domain.enums.UserRole;
import ua.edu.ukma.springers.rezflix.events.DeleteEntityEvent;
import ua.edu.ukma.springers.rezflix.exceptions.BaseException;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;
import ua.edu.ukma.springers.rezflix.mappers.FilmEpisodeMapper;
import ua.edu.ukma.springers.rezflix.repositories.FilmEpisodeRepository;
import ua.edu.ukma.springers.rezflix.security.SecurityUtils;
import ua.edu.ukma.springers.rezflix.services.rendering.RenderingService;
import ua.edu.ukma.springers.rezflix.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class FilmEpisodeService extends BaseCRUDService<FilmEpisodeEntity, CreateEpisodeDto, UpdateEpisodeDto, UUID> {

    private final FilmEpisodeMapper mapper;
    private final EnumsMapper enumsMapper;
    private final RenderingService renderingService;
    private final SecurityUtils securityUtils;

    private final Path unrenderedEpisodesDir;
    private final DataSize unrenderedEpisodeMaxSize;

    @SneakyThrows
    public FilmEpisodeService(FilmEpisodeMapper mapper, EnumsMapper enumsMapper, SecurityUtils securityUtils,
                              RenderingService renderingService,
                              @Value("${storage.unrendered-episodes.path}") Path unrenderedEpisodesDir,
                              @Value("${storage.unrendered-episodes.max-size}") DataSize unrenderedEpisodeMaxSize
    ) {
        super(FilmEpisodeEntity.class, FilmEpisodeEntity::new);
        this.mapper = mapper;
        this.enumsMapper = enumsMapper;
        this.securityUtils = securityUtils;
        this.renderingService = renderingService;
        this.unrenderedEpisodesDir = unrenderedEpisodesDir;
        this.unrenderedEpisodeMaxSize = unrenderedEpisodeMaxSize;
        Files.createDirectories(unrenderedEpisodesDir);
    }

    @Transactional(readOnly = true)
    public EpisodeDto getResponseById(UUID id) {
        return mapper.toResponse(getById(id));
    }

    @Transactional(readOnly = true)
    public EpisodeListDto getListResponseByCriteria(int filmDubbingId, EpisodeCriteriaDto criteriaDto) {
        FilmEpisodeCriteria criteria = new FilmEpisodeCriteria(criteriaDto, filmDubbingId, !securityUtils.hasRole(UserRole.CONTENT_MANAGER), enumsMapper);
        List<FilmEpisodeEntity> entities = getList(criteria);
        long total = count(criteria);
        return mapper.toListResponse(total, entities);
    }

    @Override
    @Transactional
    public FilmEpisodeEntity createEntity(UUID id, @NonNull CreateEpisodeDto view) {
        FilmEpisodeEntity episode = super.createEntity(id, view);
        Path episodePath = unrenderedEpisodesDir.resolve(episode.getId().toString());
        downloadEpisodeFile(view.getFile(), episodePath);
        renderingService.launchRenderingJob(episode.getId(), episodePath);
        return episode;
    }

    private void downloadEpisodeFile(InputStreamResource file, Path toPath)  {
        try {
            FileUtils.transferWithLimit(file.getInputStream(), toPath, unrenderedEpisodeMaxSize);
        } catch (IOException e) {
            throw new BaseException(e);
        }
    }

    @Override
    protected void postCreate(@NonNull FilmEpisodeEntity entity, @NonNull CreateEpisodeDto view) {
        entity.setStatus(FilmEpisodeStatus.BEING_RENDERED);
    }

    @Override
    @Transactional
    public void deleteEntity(@NonNull FilmEpisodeEntity entity) {
        super.deleteEntity(entity);
        renderingService.cleanupRenderingFiles(entity.getId());
    }

    @EventListener
    public void clearEpisodes(DeleteEntityEvent<? extends FilmDubbingEntity, Integer> deleteEvent) {
        ((FilmEpisodeRepository) repository).findAllByFilmDubbingId(deleteEvent.getId()).forEach(this::deleteEntity);
    }
}

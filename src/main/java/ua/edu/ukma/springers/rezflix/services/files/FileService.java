package ua.edu.ukma.springers.rezflix.services.files;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.EntityFilesInfoDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FileTypeDto;
import ua.edu.ukma.springers.rezflix.domain.entities.FileInfoEntity;
import ua.edu.ukma.springers.rezflix.domain.enums.FileType;
import ua.edu.ukma.springers.rezflix.domain.interfaces.IGettableById;
import ua.edu.ukma.springers.rezflix.events.DeleteEntityEvent;
import ua.edu.ukma.springers.rezflix.exceptions.BaseException;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.mappers.EnumsMapper;
import ua.edu.ukma.springers.rezflix.repositories.FileInfoRepository;
import ua.edu.ukma.springers.rezflix.utils.ResourceInfo;
import ua.edu.ukma.springers.rezflix.validators.IFileValidator;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

    private final FileStorageService storageService;
    private final FileInfoRepository fileInfoRepository;
    private final EnumsMapper enumsMapper;

    private final Map<Class<?>, FileType> fileTypesMap;
    private final Map<FileType, IFileValidator> fileValidatorsMap;

    public FileService(FileStorageService storageService, FileInfoRepository fileInfoRepository, EnumsMapper enumsMapper,
                       Collection<IFileValidator> fileValidators)
    {
        this.storageService = storageService;
        this.fileInfoRepository = fileInfoRepository;
        this.enumsMapper = enumsMapper;
        this.fileTypesMap = Arrays.stream(FileType.values()).collect(Collectors.toMap(FileType::getEntityClass, Function.identity()));
        this.fileValidatorsMap = fileValidators.stream().collect(Collectors.toMap(IFileValidator::supportedFileType, Function.identity()));
    }

    @Transactional(readOnly = true)
    public ResourceInfo getFile(UUID id) {
        FileInfoEntity fileInfo = getFileInfo(id);
        fileValidatorsMap.get(fileInfo.getFileType()).validForView(fileInfo);
        Resource file = storageService.getFile(id);
        try {
            return new ResourceInfo(file, fileInfo.getMimeType(), file.lastModified());
        }
        catch (IOException e) {
            throw new BaseException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<EntityFilesInfoDto> getEntitiesFilesInfo(FileTypeDto fileTypeDto, List<Integer> entitiesIds) {
        FileType fileType = enumsMapper.map(fileTypeDto);
        List<FileInfoEntity> fileInfos = fileInfoRepository.findAllByFileTypeAndBoundEntityIdIn(fileType, entitiesIds);
        fileValidatorsMap.get(fileType).validForView(fileInfos);
        Map<Integer, List<UUID>> idsMap = computeIdsMap(fileInfos);
        return entitiesIds.stream()
                .map(i -> new EntityFilesInfoDto(i, idsMap.getOrDefault(i, List.of())))
                .toList();
    }

    private Map<Integer, List<UUID>> computeIdsMap(List<FileInfoEntity> fileInfos) {
        return fileInfos.stream()
                .collect(
                    Collectors.groupingBy(
                        FileInfoEntity::getBoundEntityId,
                        Collectors.mapping(FileInfoEntity::getId, Collectors.toList())
                    )
                );
    }

    @Transactional
    public UUID uploadFile(MultipartFile file, FileTypeDto fileTypeDto, int boundEntityId) {
        FileType fileType = enumsMapper.map(fileTypeDto);
        fileValidatorsMap.get(fileType).validForUpload(file, boundEntityId);
        UUID id = UUID.randomUUID();
        try {
            storageService.saveFile(id, file.getBytes());
        }
        catch (IOException e) {
            throw new BaseException(e);
        }
        if (fileType.isUnique())
            fileInfoRepository.deleteByFileTypeAndBoundEntityId(fileType, boundEntityId).forEach(storageService::deleteFile);
        FileInfoEntity fileInfo = new FileInfoEntity(id, fileType, boundEntityId, StringUtils.defaultIfBlank(file.getContentType(), DEFAULT_MIME_TYPE));
        fileInfoRepository.save(fileInfo);
        return id;
    }

    @Transactional
    public void deleteFile(UUID id) {
        FileInfoEntity fileInfo = getFileInfo(id);
        fileValidatorsMap.get(fileInfo.getFileType()).validForDelete(fileInfo);
        fileInfoRepository.deleteById(id);
        storageService.deleteFile(id);
    }

    @EventListener
    public void clearFiles(DeleteEntityEvent<? extends IGettableById<Integer>, Integer> event) {
        FileType fileType = getFileType(event.getEntity().getClass());
        if (fileType == null) return;
        fileInfoRepository.deleteByFileTypeAndBoundEntityId(fileType, event.getId()).forEach(storageService::deleteFile);
    }

    private FileType getFileType(Class<?> entityClass) {
        while (entityClass != null) {
            FileType fileType = fileTypesMap.get(entityClass);
            if (fileType != null)
                return fileType;
            entityClass = entityClass.getSuperclass();
        }
        return null;
    }

    private FileInfoEntity getFileInfo(UUID id) {
        return fileInfoRepository.findById(id).orElseThrow(() -> new NotFoundException(FileInfoEntity.class, "id: " + id));
    }
}

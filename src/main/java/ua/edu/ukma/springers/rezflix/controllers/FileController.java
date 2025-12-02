package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.FileControllerApi;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.EntityFilesInfoDto;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FileTypeDto;
import ua.edu.ukma.springers.rezflix.services.files.FileService;
import ua.edu.ukma.springers.rezflix.utils.ResourceInfo;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController implements FileControllerApi {

    private static final CacheControl CACHE_CONTROL = CacheControl.noCache().sMaxAge(Duration.ofDays(5));
    private final FileService service;

    @Override
    public ResponseEntity<Resource> getFile(UUID id) {
        ResourceInfo file = service.getFile(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, file.contentType())
                .cacheControl(CACHE_CONTROL)
                .lastModified(file.lastModified())
                .body(file.resource());
    }

    @Override
    public ResponseEntity<List<EntityFilesInfoDto>> getEntitiesFilesInfo(FileTypeDto fileType, List<Integer> entitiesIds) {
        return ResponseEntity.ok(service.getEntitiesFilesInfo(fileType, entitiesIds));
    }

    @Override
    public ResponseEntity<UUID> uploadFile(MultipartFile file, FileTypeDto fileType, Integer boundEntityId) {
        log.info("Upload file {} for {}", fileType, boundEntityId);
        return ResponseEntity.ok(service.uploadFile(file, fileType, boundEntityId));
    }

    @Override
    public ResponseEntity<Void> deleteFile(UUID id) {
        log.info("Delete file {}", id);
        service.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}
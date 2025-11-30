package ua.edu.ukma.springers.rezflix.services.files;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path filesDir;

    public FileStorageService(@Value("${storage.files.path}") Path filesDir) throws IOException {
        this.filesDir = filesDir;
        Files.createDirectories(filesDir);
    }

    public Resource getFile(UUID fileId) {
        Path path = filePath(fileId);
        if (Files.notExists(path))
            throw new NotFoundException();
        return new FileSystemResource(path);
    }

    public void saveFile(UUID fileId, byte[] file) throws IOException {
        Path path = filePath(fileId);
        log.info("Saving file under path {}", path);
        Files.write(path, file, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void deleteFile(UUID fileId) {
        Path path = filePath(fileId);
        log.info("Deleting file under path {}", path);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Failed to delete file under path {}", path, e);
        }
    }

    private Path filePath(UUID fileId) {
        return filesDir.resolve(fileId.toString());
    }
}

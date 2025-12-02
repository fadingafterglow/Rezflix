package ua.edu.ukma.springers.rezflix.utils;

import lombok.experimental.UtilityClass;
import org.springframework.util.unit.DataSize;
import ua.edu.ukma.springers.rezflix.exceptions.ValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class FileUtils {

    private static final int DEFAULT_BUFFER_SIZE = 16384;

    public void transferWithLimit(InputStream from, Path toPath, DataSize limit) throws IOException {
        try (from; OutputStream to = Files.newOutputStream(toPath)) {
            long allowedToTransfer = limit.toBytes();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int read;
            while ((read = from.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
                to.write(buffer, 0, read);
                allowedToTransfer -= read;
                if (allowedToTransfer < 0) {
                    throw new ValidationException("error.application.file-too-large");
                }
            }
        } catch (Exception e) {
            Files.deleteIfExists(toPath);
            throw e;
        }
    }
}

package ua.edu.ukma.springers.rezflix.controllers.rest.model;

import lombok.Data;
import org.springframework.core.io.InputStreamResource;

@Data
public class CreateEpisodeDto {
    private final int filmDubbingId;
    private final UpdateEpisodeDto metadata;
    private final InputStreamResource file;
}

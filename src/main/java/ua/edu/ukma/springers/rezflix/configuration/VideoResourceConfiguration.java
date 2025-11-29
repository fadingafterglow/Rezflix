package ua.edu.ukma.springers.rezflix.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.time.Duration;

@Configuration
public class VideoResourceConfiguration implements WebMvcConfigurer {

    private final Path renderedEpisodesPath;

    public VideoResourceConfiguration(@Value("${storage.rendered-episodes.path}") Path renderedEpisodesPath) {
        this.renderedEpisodesPath = renderedEpisodesPath;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/video/**")
                .addResourceLocations("file:" + renderedEpisodesPath.toAbsolutePath())
                .setCacheControl(
                    CacheControl
                        .maxAge(Duration.ofDays(30))
                        .immutable()
                        .cachePublic()
                )
                .resourceChain(true);
    }
}

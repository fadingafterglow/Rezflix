package ua.edu.ukma.springers.rezflix.utils;

import org.springframework.core.io.Resource;

public record ResourceInfo(Resource resource, String contentType, long lastModified) {}

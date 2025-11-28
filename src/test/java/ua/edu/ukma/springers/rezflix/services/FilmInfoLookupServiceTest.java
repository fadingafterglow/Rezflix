package ua.edu.ukma.springers.rezflix.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import ua.edu.ukma.springers.rezflix.controllers.rest.model.FilmInfoLookupResultDto;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;
import ua.edu.ukma.springers.rezflix.mappers.FilmInfoLookupMapper;
import ua.edu.ukma.springers.rezflix.services.FilmInfoLookupService.FilmInfoLookupApiResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmInfoLookupServiceTest {
    @Mock private FilmInfoLookupMapper mapper;
    @Mock private RestClient restClient;
    @Mock private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock private RestClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private RestClient.ResponseSpec responseSpec;
    private FilmInfoLookupService service;
    private static final String API_KEY = "testKey";
    private static final String TITLE = "Inception";

    @BeforeEach
    void setUp() {
        service = new FilmInfoLookupService(mapper, API_KEY);
        ReflectionTestUtils.setField(service, "restClient", restClient);
    }

    @Test
    @DisplayName("Should return film info DTO when API returns valid response")
    void lookupFilmInfo() {
        FilmInfoLookupApiResponse apiResponse = mock(FilmInfoLookupApiResponse.class);
        FilmInfoLookupResultDto expectedDto = new FilmInfoLookupResultDto();
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class), eq(API_KEY), eq(TITLE))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(FilmInfoLookupApiResponse.class)).thenReturn(apiResponse);
        when(apiResponse.response()).thenReturn("True");
        when(mapper.map(apiResponse)).thenReturn(expectedDto);
        FilmInfoLookupResultDto result = service.lookupFilmInfo(TITLE);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Should throw NotFoundException when API response is False")
    void lookupFilmInfo_NotFound_WhenResponseIsFalse() {
        FilmInfoLookupApiResponse apiResponse = mock(FilmInfoLookupApiResponse.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class), eq(API_KEY), eq(TITLE))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(FilmInfoLookupApiResponse.class)).thenReturn(apiResponse);
        when(apiResponse.response()).thenReturn("False");
        assertThrows(NotFoundException.class, () -> service.lookupFilmInfo(TITLE));
    }

    @Test
    @DisplayName("Should throw NotFoundException when API body is null")
    void lookupFilmInfo_NotFound_WhenBodyIsNull() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class), eq(API_KEY), eq(TITLE))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(FilmInfoLookupApiResponse.class)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> service.lookupFilmInfo(TITLE));
    }

    @Test
    @DisplayName("Should not throw exception when health check fails")
    void healthCheck_ShouldNotThrowException_WhenApiFails() {
        when(restClient.get()).thenThrow(new RuntimeException("API Error"));
        assertDoesNotThrow(() -> service.healthCheck());
    }
}
package ua.edu.ukma.springers.rezflix.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.Set;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Rezflix Backend",
                version = "1.0",
                description = "Rezflix application backend"
        ),
        servers = @Server(url = "http://localhost:8080")
)
@SecurityScheme(
        name = OpenAPIConfiguration.BEARER_AUTH,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        paramName = HttpHeaders.AUTHORIZATION
)
public class OpenAPIConfiguration {

    public static final String BEARER_AUTH = "bearerAuth";
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
        "registerUser"
    );

    static {
        io.swagger.v3.core.jackson.ModelResolver.enumsAsRef = true;
    }

    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder()
                .group("api")
                .packagesToScan("ua.edu.ukma.springers.rezflix.controllers")
                .addOperationCustomizer((operation, method) -> {
                    if (!PUBLIC_ENDPOINTS.contains(operation.getOperationId()))
                        operation.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
                    return operation;
                })
                .build();
    }
}

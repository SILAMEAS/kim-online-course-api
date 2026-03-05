package com.sila.config;

import com.sila.config.properties.ApplicationProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  private final ApplicationProperties applicationProperties;

  public OpenApiConfig(ApplicationProperties props) {
    this.applicationProperties = props;
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title(applicationProperties.getName())
                .version(applicationProperties.getVersion())
                .description(applicationProperties.getDescription()))
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearer-key",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
  }

  @Bean
  public GroupedOpenApi authApi() {
    return GroupedOpenApi.builder().group("auth").pathsToMatch("/**/auths/**").build();
  }

  @Bean
  public GroupedOpenApi courseApi() {
    return GroupedOpenApi.builder().group("course").pathsToMatch("/**/courses/**").build();
  }

  @Bean
  public GroupedOpenApi paymentApi() {
    return GroupedOpenApi.builder().group("payment").pathsToMatch("/**/payments/**").build();
  }

  @Bean
  public GroupedOpenApi videoApi() {
    return GroupedOpenApi.builder().group("video").pathsToMatch("/**/videos/**").build();
  }
}

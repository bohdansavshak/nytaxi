package com.bohdansavshak.config;

import static org.springframework.security.config.Customizer.withDefaults;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

  @Bean
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    // @formatter:off
    http.authorizeExchange(
            (authorize) ->
                authorize
                    .pathMatchers(
                        "/webjars/**", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**")
                    .permitAll()
                    .anyExchange()
                    .authenticated())
        .oauth2ResourceServer((resourceServer) -> resourceServer.jwt(withDefaults()));
    // @formatter:on
    return http.build();
  }

  private SecurityScheme createAPIKeyScheme() {
    return new SecurityScheme().type(SecurityScheme.Type.HTTP).bearerFormat("JWT").scheme("bearer");
  }

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
        .components(
            new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
        .info(new Info().title("Promotion project").version("1.0"));
  }
}

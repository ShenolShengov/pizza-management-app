package shengov.bg.pizzza_management_app.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  private static final String OAUTH2_SCHEME = "oauth2";

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String issuerUri;

  @Bean
  public OpenAPI openAPI() {
    String authorizationUrl = issuerUri + "/protocol/openid-connect/auth";
    String tokenUrl = issuerUri + "/protocol/openid-connect/token";

    OAuthFlow authorizationCodeFlow =
        new OAuthFlow().authorizationUrl(authorizationUrl).tokenUrl(tokenUrl);

    SecurityScheme securityScheme =
        new SecurityScheme()
            .type(SecurityScheme.Type.OAUTH2)
            .flows(new OAuthFlows().authorizationCode(authorizationCodeFlow));

    return new OpenAPI()
        .info(new Info().title("Pizza Management API").version("1.0"))
        .components(new Components().addSecuritySchemes(OAUTH2_SCHEME, securityScheme))
        .addSecurityItem(new SecurityRequirement().addList(OAUTH2_SCHEME));
  }
}

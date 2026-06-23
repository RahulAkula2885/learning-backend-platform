package in.rahul.learning.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Rahul",
                        email = "rahulakula2015@gmail.com"
                ),
                description = "Learning Full-stack Documentation",
                title = "Learning Full stack Api's",
                version = "1.0",
                license = @License(
                        name = "License name"
                ),
                termsOfService = "Terms of service"
        ),
        /*servers = {
                @Server(
                description = "Local ENV",
                url = "http://localhost:8080"
                ),

                @Server(
                description = "Dev ENV",
                url = ""
                 ),
                @Server(
                        description = "PROD ENV",
                        url = ""
                )
        },*/
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}

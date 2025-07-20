package capstone._4.controller.doc;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name="bearerAuth")
@Parameter(
        in = ParameterIn.HEADER,
        name = "Authorization", required = true,
        schema = @Schema(type = "string"),
        description = "Bearer [Access 토큰]"
)
public interface BaseApi {
}

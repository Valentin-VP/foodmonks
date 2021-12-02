package org.foodmonks.backend.Demo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.foodmonks.backend.authentication.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/demo")
@Tag(name = "demo", description = "API para carga de Datos - Demo")
public class DemoController {

    private final DemoService demoService;
    private final TokenHelper tokenHelper;

    @Value("${super.admin.username}")
    private String superAdminUsername;

    @Autowired
    public DemoController(DemoService demoService, TokenHelper tokenHelper) {
        this.demoService = demoService;
        this.tokenHelper = tokenHelper;
    }

    @Operation(summary = "Modifica las fechas para la carga de datos inicial",
            description = "Modifica las fechas para la carga de datos inicial para la demo, según las fechas recibidas",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "demo" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fechas modificadas con éxito"),
            @ApiResponse(responseCode = "400", description = "Error: solicitud inválida")
    })
    @PostMapping(path = "/modificarFechas")
    public ResponseEntity<?> modificarFechas(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Rango de fechas Inicio y Final para cambiar los datos", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = {@ExampleObject(name = "Ejemplo de fechas", value = "{\"fechaInicio\": \"2020-03-28\","
                                    + "\"fechaFin\": \"2020-04-28\""
                                    + "}")}))
            @RequestBody String fechas){
        try{
            // Obtener correo del Header
            String correo = tokenHelper.getUsernameFromToken(token);
            // Luego comparar con variable de Super Admin del application.properties
            if(correo.equals(superAdminUsername)){
                JsonObject request = new Gson().fromJson(fechas, JsonObject.class);
                demoService.cambiarFechas(request.get("fechaInicio").getAsString(), request.get("fechaFin").getAsString());
                return new ResponseEntity<>(HttpStatus.OK);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Admin no autorizado. Solo el Super Admin puede realizar esta operación.");
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

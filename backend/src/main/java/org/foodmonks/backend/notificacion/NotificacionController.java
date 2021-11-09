package org.foodmonks.backend.notificacion;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.jav.exposerversdk.PushClientException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cliente")
@Slf4j
public class NotificacionController {

    private final NotificacionExpoService notificacionExpoService;

    @Autowired
    NotificacionController (NotificacionExpoService notificacionExpoService){
        this.notificacionExpoService = notificacionExpoService;
    }

    @Operation(summary = "Crea una notificacion",
            description = "Enviar notificacion a un mobile",
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Envio exitoso"),
            @ApiResponse(responseCode = "400", description = "Error: solicitud inválida")
    })
    @PostMapping(path = "/enviarNotificacion")//Crear Notificacion
    public ResponseEntity<?> crearNotifacion(
            @Parameter(description = "Datos de la notificacion", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Notificacion.class)))
            @RequestBody String notificacion) {
        try {
            JsonObject jsonNotificacion = new Gson().fromJson(notificacion, JsonObject.class);
            notificacionExpoService.crearNotifacion(jsonNotificacion.get("token").getAsString(), jsonNotificacion.get("asunto").getAsString(),jsonNotificacion.get("menssage").getAsString());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

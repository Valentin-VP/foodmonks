package org.foodmonks.backend.Cliente;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.SneakyThrows;
import org.codehaus.jettison.json.JSONObject;
import org.foodmonks.backend.Direccion.Direccion;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.foodmonks.backend.authentication.TokenHelper;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cliente")
@Slf4j
public class ClienteController {

    private final TokenHelper tokenHelp;
    private final ClienteService clienteService;

    @Autowired
    ClienteController(ClienteService clienteService, TokenHelper tokenHelp) {
        this.clienteService = clienteService;
        this.tokenHelp = tokenHelp;
    }

    @Operation(summary = "Crea un nuevo Cliente",
            description = "Alta de un nuevo Cliente",
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro exitoso"),
            @ApiResponse(responseCode = "400", description = "Error: solicitud inválida")
    })
    @PostMapping(path = "/altaCliente")//CREAR CLIENTE
    public ResponseEntity<?> crearCliente(
            @Parameter(description = "Datos del nuevo Cliente", required = true)
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Cliente.class)))
            @RequestBody String cliente) {
        try {
            JsonObject jsonCliente = new Gson().fromJson(cliente, JsonObject.class);
            JsonObject jsonDireccion = jsonCliente.get("direccion").getAsJsonObject();

            clienteService.crearCliente(
                    jsonCliente.get("nombre").getAsString(),
                    jsonCliente.get("apellido").getAsString(),
                    jsonCliente.get("correo").getAsString(),
                    new String (Base64.getDecoder().decode(jsonCliente.get("password").getAsString())),
                    LocalDate.now(),
                    5.0f,
                    jsonDireccion,
                    EstadoCliente.valueOf("ACTIVO")
                    // pedidos se crea el array vacio en el back
                    // y mobileToken es null hasta que instale la aplicacion
            );

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping//LISTAR CLIENTE
    //@GetMapping("/rutaEspecifica")
    public List<Cliente> listarCliente(){
        return clienteService.listarCliente();
    }

    @GetMapping("/buscar")
    public void buscarCliente(@RequestParam String correo) {
        clienteService.buscarCliente(correo);
    }

    @Operation(summary = "Elimina cuenta propia de Cliente",
            description = "Baja logica de Cliente, se cierra sesion al finalizar",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa. Se ha dado de baja."),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @PreAuthorize("hasRole('ROLE_CLIENTE')")
    @DeleteMapping(path = "eliminarCuenta")//ELIMINAR CLIENTE
    public ResponseEntity<?> eliminarCuentaPropiaCliente(
            @RequestHeader("Authorization") String token) {
        try {
            String newToken = null;
            if ( token != null && token.startsWith("Bearer ")) {
                newToken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newToken);
            clienteService.modificarEstadoCliente(correo, EstadoCliente.ELIMINADO);
            log.debug("Cliente eliminado, enviando a cerrar sesion");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping//EDITAR CLIENTE
    public void modificarCliente(@RequestBody Cliente cliente) {
        clienteService.modificarCliente(cliente);

    }

    @Operation(summary = "Agregar una Direccion",
            description = "Agrega una nueva Direccion al Cliente",
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Direccion agregada"),
            @ApiResponse(responseCode = "400", description = "Error: solicitud inválida")
    })
    @SneakyThrows
    @PostMapping(path = "/agregarDireccion")
    public ResponseEntity<?> agregarDireccion(@RequestHeader("Authorization") String token,
                                              @Parameter(description = "Datos del nuevo Cliente", required = true)
                                              @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                      content = @Content(mediaType = "application/json",
                                                              schema = @Schema(implementation = Direccion.class)))
                                              @RequestBody String direccion) {
        try {
            String newToken = "";
            if ( token != null && token.startsWith("Bearer ")) {
                newToken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newToken);

            JsonObject jsonDireccion = new Gson().fromJson(direccion, JsonObject.class);

            clienteService.agregarDireccionCliente(correo, jsonDireccion);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Elimina direccion de Cliente",
            description = "Se elimina una de las direcciones del Cliente",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = { "cliente" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa. Se agrega la direccion"),
            @ApiResponse(responseCode = "400", description = "Ha ocurrido un error")
    })
    @DeleteMapping(path = "/eliminarDireccion")
    public ResponseEntity<?> eliminarDireccion(@RequestHeader("Authorization") String token,
                                               @RequestParam(name = "latitud") String latitud,
                                               @RequestParam(name = "longitud") String longitud) {
        try {
            String newToken = "";
            if ( token != null && token.startsWith("Bearer ")) {
                newToken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newToken);

            clienteService.eliminarDireccionCliente(correo, latitud, longitud);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(path = "/cambiarInfoBase")
    public ResponseEntity<?> cambiarInfoBase(@RequestHeader("Authorization") String token,
                                             @Parameter(description = "Datos nuevos del Cliente", required = true)
                                             @RequestBody String info) {

        JsonObject jsonInfo = new Gson().fromJson(info, JsonObject.class);
        try {
            String newToken = "";
            if ( token != null && token.startsWith("Bearer ")) {
                newToken = token.substring(7);
            }
            String correo = tokenHelp.getUsernameFromToken(newToken);

            //clienteService.funcion(correo, jsonInfo.get("nombre").getAsString, jsonInfo.get("apellido").getAsString);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

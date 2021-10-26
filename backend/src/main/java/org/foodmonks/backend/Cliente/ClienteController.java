package org.foodmonks.backend.Cliente;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.foodmonks.backend.authentication.TokenHelper;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cliente")
public class ClienteController {

    private final TokenHelper tokenHelp;

    private final ClienteService clienteService;

    @Autowired
    ClienteController(TokenHelper tokenHelp, ClienteService clienteService) {
        this.tokenHelp = tokenHelp;
        this.clienteService = clienteService;
    }

    @PostMapping//CREAR CLIENTE
    public void createCliente(@RequestBody Cliente cliente) {
        clienteService.crearCliente(cliente);
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
            // TODO: Enviar a cerrar sesion
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping//EDITAR CLIENTE
    public void modificarCliente(@RequestBody Cliente cliente) {
        clienteService.modificarCliente(cliente);

    }

}

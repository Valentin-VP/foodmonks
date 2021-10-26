package org.foodmonks.backend.Cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    ClienteController(ClienteService clienteService) {
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

    @DeleteMapping(path = "eliminarCuenta")//ELIMINAR CLIENTE
    public void elimiarCliente(@RequestParam String correo) {

        //clienteService.eliminarCliente(id);
    }

    @PutMapping//EDITAR CLIENTE
    public void modificarCliente(@RequestBody Cliente cliente) {
        clienteService.modificarCliente(cliente);

    }

}

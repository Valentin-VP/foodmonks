package org.foodmonks.backend.Cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("cliente")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping//CREAR CLIENTE
    public void createCliente(@RequestBody Cliente cliente) {
        System.out.println("Entro al post");
        clienteService.crearCliente(cliente);
    }

    @GetMapping//LISTAR CLIENTE
    //@GetMapping("/rutaEspecifica")
    public List<Cliente> listarCliente(){
        return clienteService.listarCliente();
    }

    @GetMapping("/buscar")
    public void buscarCliente(@RequestParam Long id) {
        System.out.println(id);
        clienteService.buscarCliente(id);
    }

    @DeleteMapping//ELIMINAR CLIENTE
    public void elimiarCliente(@RequestParam Long id) {
        System.out.println(id);
        clienteService.eliminarCliente(id);
    }

    @PutMapping//EDITAR CLIENTE
    public void modificarCliente(@RequestBody Cliente cliente) {
        if (cliente == null) {
            System.out.println("no existe el cliente");
        }else {
            System.out.println(cliente.getNombre());
            clienteService.modificarCliente(cliente);
        }

    }

}

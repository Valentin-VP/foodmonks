package org.foodmonks.backend.Cliente;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Exceptions.*;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Direccion.DireccionService;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.UsuarioService;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {

    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clienteRepository;
    private final UsuarioService usuarioService;
    private final DireccionService direccionService;
    private final ClienteConverter clienteConverter;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder , UsuarioService usuarioService, DireccionService direccionService, ClienteConverter clienteConverter) {
        this.clienteRepository = clienteRepository; this.passwordEncoder = passwordEncoder; this.usuarioService = usuarioService; this.direccionService = direccionService; this.clienteConverter = clienteConverter;
    }

    public void crearCliente(String nombre, String apellido, String correo, String password, LocalDate fechaRegistro,
                             Float calificacion, JsonObject jsonDireccion, EstadoCliente activo) throws ClienteDireccionException, UsuarioExisteException {
        if (usuarioService.ObtenerUsuario(correo) != null) {
            throw new UsuarioExisteException("Ya existe un Usuario registrado con el correo " + correo);
        }
        Direccion direccion = direccionService.crearDireccion(jsonDireccion);
        if (direccion == null){
            throw new ClienteDireccionException("Debe ingresar una dirección");
        }
        List<Direccion> direcciones = new ArrayList<>();
        direcciones.add(direccion);
        Cliente cliente = new Cliente(nombre,apellido,correo,passwordEncoder.encode(password),fechaRegistro,calificacion,direcciones,activo,"",null);
        clienteRepository.save(cliente);
        System.out.println("direccion " + clienteRepository.findByCorreo(correo).getDirecciones().get(0).getCalle());
    }


    public List<Cliente> listarCliente(){
        return clienteRepository.findAll();
    }

    public Cliente buscarCliente(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

/*    public void modificarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }*/


    public void modificarCliente(String correo, String nombre, String apellido) throws ClienteNoEncontradoException {

        Cliente clienteAux = clienteRepository.findByCorreo(correo);

        if (clienteAux == null) {
            throw new ClienteNoEncontradoException("No existe el Cliente " + correo);
        }

        clienteAux.setNombre(nombre);
        clienteAux.setApellido(apellido);
        clienteRepository.save(clienteAux);
    }


    public void modificarEstadoCliente(String correo, EstadoCliente estado) throws ClienteNoEncontradoException {
        Cliente cliente = obtenerCliente(correo);
        cliente.setEstado(estado);
        clienteRepository.save(cliente);
    }

    public EstadoCliente clienteEstado(String correo) throws ClienteNoEncontradoException {
        Cliente cliente = obtenerCliente(correo);
        return cliente.getEstado();
    }

    public JsonObject agregarDireccionCliente(String correo, JsonObject jsonDireccion) throws ClienteNoEncontradoException, ClienteDireccionException, ClienteExisteDireccionException {
        Cliente cliente = obtenerCliente(correo);
        if (jsonDireccion.get("latitud").getAsString().isEmpty() && jsonDireccion.get("longitud").getAsString().isEmpty()){
            throw new ClienteDireccionException("Debe ingresar una dirección");
        }
        Direccion direccion = direccionService.crearDireccion(jsonDireccion);
        List<Direccion> direcciones = cliente.getDirecciones();
        for (Direccion dire : direcciones){
            if (dire.getLatitud().equals(direccion.getLatitud()) && dire.getLongitud().equals(direccion.getLongitud())) {
                throw new ClienteExisteDireccionException("Esa dirección ya esta registrada para el Cliente " + correo);
            }
        }
        direcciones.add(direccion);
        cliente.setDirecciones(direcciones);
        clienteRepository.save(cliente);
        JsonObject idDirecccionJson = new JsonObject();
        idDirecccionJson.addProperty("id", obtenerDireccionCliente(cliente.getDirecciones(),direccion.getLatitud(),direccion.getLongitud()).getId());
        return idDirecccionJson;
    }

    public void eliminarDireccionCliente(String correo, Long id) throws ClienteNoEncontradoException, ClienteDireccionException, ClienteUnicaDireccionException, ClienteNoExisteDireccionException {
        Cliente cliente = obtenerCliente(correo);
        Direccion direccion = direccionService.obtenerDireccion(id);
        if (direccion == null){
            throw new ClienteDireccionException("No existe esa direccion en el sistema");
        }
        List<Direccion> direcciones = cliente.getDirecciones();
        if (direcciones.size() < 2){
            throw new ClienteUnicaDireccionException("No puede eliminar la única dirección registrada del Cliente " + correo);
        }
        for (Direccion dire : direcciones) {
            if (dire.getId().equals(id)) {
                direcciones.remove(direccion);
                cliente.setDirecciones(direcciones);
                clienteRepository.save(cliente);
                return;
            }
        }
        throw new ClienteNoExisteDireccionException("Esa dirección no esta registrada para el Cliente " + correo);

    }

    public void modificarDireccionCliente(String correo, Long idDireccionActual, JsonObject jsonDireccionNueva) throws ClienteNoEncontradoException, ClienteDireccionException, ClienteNoExisteDireccionException {
        Cliente cliente = obtenerCliente(correo);
        Direccion direccionActual = direccionService.obtenerDireccion(idDireccionActual);
        if (direccionActual == null){
            throw new ClienteDireccionException("No existe esa direccion en el sistema");
        }
        if (jsonDireccionNueva.get("latitud").getAsString().isEmpty() && jsonDireccionNueva.get("longitud").getAsString().isEmpty()){
            throw new ClienteDireccionException("Debe ingresar una dirección Nueva");
        }
        Direccion direccionNueva = direccionService.crearDireccion(jsonDireccionNueva);
        List<Direccion> direcciones = cliente.getDirecciones();
        for (Direccion dire : direcciones){
            if (dire.getId().equals(idDireccionActual)) {
                direccionService.modificarDireccion(dire,direccionNueva);
                return;
            }
        }
        throw new ClienteNoExisteDireccionException("La direccion actual ingresada no existe para el Cliente " + correo);

    }

    private Cliente obtenerCliente(String correo) throws ClienteNoEncontradoException {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente == null) {
            throw new ClienteNoEncontradoException("No existe el Cliente " + correo);
        }
        return cliente;
    }

    public JsonObject obtenerJsonCliente(String correo) throws ClienteNoEncontradoException {
        Cliente cliente = clienteRepository.findByCorreo(correo);
        if (cliente == null) {
            throw new ClienteNoEncontradoException("No existe el Cliente " + correo);
        }
        return clienteConverter.jsonCliente(cliente);
    }

    public Direccion obtenerDireccionCliente(List<Direccion> direcciones, String latitud, String longitud){
        for(Direccion direccion : direcciones){
            if (direccion.getLatitud().equals(latitud) && direccion.getLongitud().equals(longitud)){
                return direccion;
            }
        }
        return null;
    }

    public JsonObject crearPedido(String correo, JsonArray jsonRequestPedido) {
/*        [
            {
                "restaurante": "correoRestaurante@gmail.com", // String: correo del restaurante en que se solicitan los menus
                "direccionId": 2, // Long: direccion seleccionada del cliente, ya se cuenta con las direcciones del cliente en el front, entiendo se podría enviar solamente el ID
                "medioPago": "PayPal", //String: vale 'PayPal' o 'Efectivo'
                "ordenPaypal": "148asd8f412", // String: puede ser un String vacío si el pago fue en efectivo: ''
                "menus" : [ // JsonArray: Arreglo con todos los menus comprados
                    {
                        "id": 123,
                        "precio": 250.0, // Float: precio final que aplica por cada item, si es de promocion es el resutlado del precio x multiplicadorPromocion
                        "cantidad": 3, // int: cantidad de items de este menu/promocion
                        "detalles": "Los detalles o comentarios del pedido" // String
                    },
                    ...
                ]
            }
        ]*/
        return new JsonObject(); // <-- Representacion del Pedido
    }
}

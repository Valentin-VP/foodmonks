package org.foodmonks.backend.Cliente;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Exceptions.*;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Direccion.DireccionService;
import org.foodmonks.backend.Direccion.Exceptions.DireccionNumeroException;
import org.foodmonks.backend.Menu.Exceptions.MenuIdException;
import org.foodmonks.backend.Pedido.Exceptions.PedidoTotalException;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Pedido.Exceptions.PedidoIdException;
import org.foodmonks.backend.Pedido.Exceptions.PedidoNoExisteException;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Pedido.PedidoConverter;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoComentarioException;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoExisteException;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoNoFinalizadoException;
import org.foodmonks.backend.Reclamo.Exceptions.ReclamoRazonException;
import org.foodmonks.backend.Reclamo.ReclamoService;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.UsuarioService;
import org.foodmonks.backend.Menu.Menu;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Cliente.Exceptions.ClienteNoEncontradoException;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.foodmonks.backend.Menu.Exceptions.MenuNoEncontradoException;
import org.foodmonks.backend.MenuCompra.MenuCompra;
import org.foodmonks.backend.MenuCompra.MenuCompraService;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.Restaurante.RestauranteService;
import org.foodmonks.backend.datatypes.DtOrdenPaypal;
import org.foodmonks.backend.datatypes.EstadoCliente;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.MedioPago;
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
    private final PedidoService pedidoService;
    private final RestauranteService restauranteService;
    private final MenuCompraService menuCompraService;
    private final MenuService menuService;
    private final PedidoConverter pedidoConverter;
    private final EmailService emailService;
    private final ReclamoService reclamoService;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder, 
                          UsuarioService usuarioService, DireccionService direccionService, 
                          ClienteConverter clienteConverter, PedidoService pedidoService, 
                          RestauranteService restauranteService, MenuCompraService menuCompraService, 
                          MenuService menuService, PedidoConverter pedidoConverter,
                          EmailService emailService, ReclamoService reclamoService) {
        this.clienteRepository = clienteRepository; this.passwordEncoder = passwordEncoder; 
        this.usuarioService = usuarioService; this.direccionService = direccionService; 
        this.clienteConverter = clienteConverter; this.pedidoService = pedidoService;  
        this.restauranteService = restauranteService; this.menuCompraService = menuCompraService; 
        this.menuService = menuService; this.pedidoConverter = pedidoConverter;
        this.emailService = emailService; this.reclamoService = reclamoService;
    }

    public void crearCliente(String nombre, String apellido, String correo, String password, LocalDate fechaRegistro,
                             Float calificacion, JsonObject jsonDireccion, EstadoCliente activo) throws ClienteDireccionException, UsuarioExisteException, DireccionNumeroException {
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

    public void modificarCliente(String correo, String nombre, String apellido) throws ClienteNoEncontradoException {

        Cliente clienteAux = obtenerCliente(correo);
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

    public JsonObject agregarDireccionCliente(String correo, JsonObject jsonDireccion) throws ClienteNoEncontradoException, ClienteDireccionException, ClienteExisteDireccionException, DireccionNumeroException {
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

    public void modificarDireccionCliente(String correo, Long idDireccionActual, JsonObject jsonDireccionNueva) throws ClienteNoEncontradoException, ClienteDireccionException, ClienteNoExisteDireccionException, DireccionNumeroException {
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
        Cliente cliente = obtenerCliente(correo);
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

    public List<JsonObject> listarMenus (String correo, String categoria, Float precioInicial, Float precioFinal) throws RestauranteNoEncontradoException {

        Restaurante restaurante = restauranteService.obtenerRestaurante(correo);
        List<JsonObject> menus = menuService.obtenerListaMenu(restaurante);

        if(!categoria.isEmpty()){
            return menuService.listMenuRestauranteCategoria(restaurante,CategoriaMenu.valueOf(categoria));
        }

        return menus;
    }

    public JsonObject crearPedido(String correo, JsonObject jsonRequestPedido) throws ClienteNoEncontradoException, RestauranteNoEncontradoException, ClienteNoExisteDireccionException, MenuNoEncontradoException, MenuIdException, PedidoTotalException {
        verificarJsonPedido(jsonRequestPedido);
        Cliente cliente = obtenerCliente(correo);
        Restaurante restaurante = restauranteService.obtenerRestaurante(jsonRequestPedido.get("restaurante").getAsString());
        DtOrdenPaypal ordenPaypal = new DtOrdenPaypal();
        if (MedioPago.valueOf(jsonRequestPedido.get("medioPago").getAsString()).equals(MedioPago.PAYPAL)){
            if (!jsonRequestPedido.get("ordenId").getAsString().isEmpty() && !jsonRequestPedido.get("linkAprobacion").getAsString().isEmpty()){
                ordenPaypal.setOrdenId(jsonRequestPedido.get("ordenId").getAsString());
                ordenPaypal.setLinkAprobacion(jsonRequestPedido.get("linkAprobacion").getAsString());
            }
        }
        Direccion direccion = direccionService.obtenerDireccion(jsonRequestPedido.get("direccionId").getAsLong());
        if (direccion == null){
            throw new ClienteNoExisteDireccionException("No existe la direccion ingresada en el sistema");
        }
        if (!cliente.getDirecciones().contains(direccion)){
            throw new ClienteNoExisteDireccionException("La direccion ingresada no existe para el Cliente " + correo);
        }
        JsonArray jsonArray = jsonRequestPedido.get("menus").getAsJsonArray();
        List<MenuCompra> menus = new ArrayList<>();
        for (JsonElement jsonMenu : jsonArray){
            if (!jsonMenu.getAsJsonObject().get("id").getAsString().matches("[0-9]*") || jsonMenu.getAsJsonObject().get("id").getAsString().isBlank()){
                throw new MenuIdException("El formado del menu con id " + jsonMenu.getAsJsonObject().get("id").getAsString()
                + " es invalido");
            }
            Menu menu = menuService.obtenerMenu(jsonMenu.getAsJsonObject().get("id").getAsLong(),restaurante);
            if (menu == null) {
                throw new MenuNoEncontradoException("El menu no existe para el Restaurante " + restaurante.getNombreRestaurante());
            }
            MenuCompra menuCompra = menuCompraService.crearMenuCompraMenu(menu, jsonMenu.getAsJsonObject().get("cantidad").getAsInt());
            menus.add(menuCompra);
        }
        return pedidoService.crearPedido(EstadoPedido.PENDIENTE,jsonRequestPedido.get("total").getAsFloat(),
                MedioPago.valueOf(jsonRequestPedido.get("medioPago").getAsString()),ordenPaypal,direccion,cliente,
                restaurante,menus); // <-- Representacion del Pedido
    }

    public void verificarJsonPedido(JsonObject jsonRequestPedido) throws MenuIdException, PedidoTotalException {
        if (!jsonRequestPedido.get("direccionId").getAsString().matches("[0-9]*") || jsonRequestPedido.get("direccionId").getAsString().isBlank()){
            throw new MenuIdException("El id de la direccion no es un numero entero");
        }
        if (!jsonRequestPedido.get("total").getAsString().matches("^\\d+(.\\d+)*$") || jsonRequestPedido.get("total").getAsString().isBlank()){
            throw new PedidoTotalException("El total no esta en formato de numero real");
        }
    }
    public JsonArray listaPedidos(String correo) throws ClienteNoEncontradoException {
        Cliente cliente = obtenerCliente(correo);
        return pedidoConverter.arrayJsonPedido(cliente.getPedidos());
    }

    public JsonObject agregarReclamo(String correo, JsonObject jsonReclamo) throws PedidoNoExisteException, EmailNoEnviadoException,
            PedidoIdException, ReclamoComentarioException, ReclamoRazonException, ReclamoNoFinalizadoException, ReclamoExisteException, ClienteNoEncontradoException, ClientePedidoNoCoincideException {
        verificarJsonReclamo(jsonReclamo);
        Cliente cliente = obtenerCliente(correo);
        Pedido pedido = pedidoService.obtenerPedido(jsonReclamo.get("pedidoId").getAsLong());
        if (!cliente.getCorreo().equals(pedido.getCliente().getCorreo())){
            throw new ClientePedidoNoCoincideException("El cliente con correo " + cliente.getCorreo() + " no realizo el pedido a reclamar");
        }
        JsonObject reclamo = reclamoService.crearReclamo(jsonReclamo.get("razon").getAsString(),jsonReclamo.get("comentario").getAsString(),LocalDate.now(),pedido);
        String[] cc = new String[1];
        cc[0] = pedido.getCliente().getCorreo();
        emailService.enviarMail(pedido.getRestaurante().getCorreo(),jsonReclamo.get("razon").getAsString(),jsonReclamo.get("comentario").getAsString(),cc);
        return reclamo;
    }

    public void verificarJsonReclamo (JsonObject jsonReclamo) throws PedidoIdException, ReclamoRazonException, ReclamoComentarioException {
        if (!jsonReclamo.get("pedidoId").getAsString().matches("[0-9]*") || jsonReclamo.get("pedidoId").getAsString().isBlank()){
            throw new PedidoIdException("El id del pedido no es un numero entero");
        }
        if (jsonReclamo.get("razon").getAsString().isBlank()){
            throw new ReclamoRazonException("La razon del reclamo no puede ser vacia");
        }
        if (jsonReclamo.get("comentario").getAsString().isBlank()){
            throw new ReclamoComentarioException("El comentario del reclamo no puede ser vacio");
        }
    }

}

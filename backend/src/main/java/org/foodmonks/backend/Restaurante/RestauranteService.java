package org.foodmonks.backend.Restaurante;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Exceptions.ClienteDireccionException;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Menu.Exceptions.MenuMultiplicadorException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import org.foodmonks.backend.Menu.Exceptions.MenuPrecioException;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Pedido.Exceptions.PedidoNoExisteException;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Reclamo.Reclamo;
import org.foodmonks.backend.Reclamo.ReclamoConverter;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteFaltaMenuException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoRestaurante;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestauranteService {

    private final PasswordEncoder passwordEncoder;
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final MenuService menuService;
    private final RestauranteConverter restauranteConverter;
    private final PedidoService pedidoService;
    private final ReclamoConverter reclamoConverter;

    @Autowired
    public RestauranteService(RestauranteRepository restauranteRepository, PasswordEncoder passwordEncoder ,
                              UsuarioRepository usuarioRepository, MenuService menuService,
                              RestauranteConverter restauranteConverter, PedidoService pedidoService,
                              ReclamoConverter reclamoConverter) {
        this.restauranteRepository = restauranteRepository;this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository; this.menuService = menuService;
        this.restauranteConverter = restauranteConverter; this.pedidoService = pedidoService;
        this.reclamoConverter = reclamoConverter;
    }

    public List<Restaurante> listarRestaurante(){
        return restauranteRepository.findAll();
    }

    public Restaurante buscarRestaurante(String correo) {
        return restauranteRepository.findByCorreo(correo);
    }

    public void editarRestaurante(Restaurante restaurante) {
        restauranteRepository.save(restaurante);
    }

    public void modificarEstado(String correo, EstadoRestaurante estado) {
        Restaurante restauranteAux = restauranteRepository.findByCorreo(correo);
        restauranteAux.setEstado(estado);
        restauranteRepository.save(restauranteAux);
    }

    public void createSolicitudAltaRestaurante(String nombre, String apellido, String correo, String password, LocalDate now, float calificacion, String nombreRestaurante, String rut, Direccion direccion, EstadoRestaurante pendiente, String telefono, String descripcion, String cuentaPaypal, String url,ArrayList<JsonObject> jsonMenus) throws UsuarioExisteException, ClienteDireccionException, RestauranteFaltaMenuException, UsuarioNoRestaurante, MenuNombreExistente, MenuPrecioException, MenuMultiplicadorException {
        if (usuarioRepository.findByCorreo(correo) != null) {
            throw new UsuarioExisteException("Ya existe un Usuario registrado con el correo " + correo);
        }
        if (direccion == null){
            throw new ClienteDireccionException("Debe ingresar una direccion");
        }
        if (jsonMenus.size() < 3){
            throw new RestauranteFaltaMenuException("Debe ingresar al menos 3 menus");
        }
        Restaurante restaurante = new Restaurante(nombre,apellido,correo, passwordEncoder.encode(password),now,calificacion,nombreRestaurante,Integer.valueOf(rut),direccion,pendiente,Integer.valueOf(telefono),descripcion,cuentaPaypal,url);
        restauranteRepository.save(restaurante);
        for (JsonObject menu: jsonMenus){
            menuService.altaMenu(menu,restaurante.getCorreo());
        }
    }

    public EstadoRestaurante restauranteEstado (String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return restaurante.getEstado();
    }

    public JsonObject obtenerJsonRestaurante(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return restauranteConverter.jsonRestaurante(restaurante);

    }
  
    public void registrarPagoEfectivo(String correo, Long idPedido) throws RestauranteNoEncontradoException, PedidoNoExisteException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        if (!pedidoService.existePedido(idPedido)){
            throw new PedidoNoExisteException("No existe el pedido con id "+ idPedido);
        }
        if (!pedidoService.existePedidoRestaurante(idPedido,restaurante)){
            throw new RestauranteNoEncontradoException("No existe el pedido con id " + idPedido + " para el Restaurante " + correo);
        }
        pedidoService.cambiarEstadoPedido(idPedido, EstadoPedido.FINALIZADO);
      }
  
  
    public List<JsonObject> listarPedidosEfectivoConfirmados(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null){
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        //System.out.println(restaurante.getNombreRestaurante() + restaurante.getPedidos());
        return pedidoService.listaPedidosEfectivoConfirmados(restaurante);
    }
  
  
    public List<JsonObject> listaRestaurantesAbiertos(String nombreRestaurante, String categoriaMenu, boolean ordenCalificacion){

        if (ordenCalificacion) {
            if (!nombreRestaurante.isBlank()){
                return restauranteConverter.listaRestaurantes(restauranteRepository.findRestaurantesByNombreRestauranteContainsAndEstadoOrderByCalificacionDesc(nombreRestaurante,EstadoRestaurante.ABIERTO));
            }
            if (!categoriaMenu.isBlank()) {
                List<Restaurante> restaurantes = restauranteRepository.findRestaurantesByEstadoOrderByCalificacionDesc(EstadoRestaurante.ABIERTO);
                CategoriaMenu categoria = CategoriaMenu.valueOf(categoriaMenu);
                return obtenerRestauranteMenuConCategoria(restaurantes,categoria);
            }
            return  restauranteConverter.listaRestaurantes(restauranteRepository.findRestaurantesByEstadoOrderByCalificacionDesc(EstadoRestaurante.ABIERTO));
        } else {
            if (!nombreRestaurante.isBlank()){
                return restauranteConverter.listaRestaurantes(restauranteRepository.findRestaurantesByNombreRestauranteContainsAndEstado(nombreRestaurante,EstadoRestaurante.ABIERTO));
            }
            if (!categoriaMenu.isBlank()) {
                List<Restaurante> restaurantes = restauranteRepository.findRestaurantesByEstado(EstadoRestaurante.ABIERTO);
                CategoriaMenu categoria = CategoriaMenu.valueOf(categoriaMenu);
                return obtenerRestauranteMenuConCategoria(restaurantes,categoria);
            }
            return  restauranteConverter.listaRestaurantes(restauranteRepository.findRestaurantesByEstado(EstadoRestaurante.ABIERTO));
        }
    }

    public List<JsonObject> obtenerRestauranteMenuConCategoria(List<Restaurante> restaurantes, CategoriaMenu categoriaMenu){
        List<Restaurante> result = new ArrayList<>();
        for (Restaurante restaurante : restaurantes) {
            if (menuService.existeCategoriaMenu(restaurante, categoriaMenu)) {
                result.add(restaurante);
            }
        }
        return restauranteConverter.listaRestaurantes(result);
    }

    public Restaurante obtenerRestaurante(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return restaurante;
    }

    public void agregarReclamoRestaurante(Restaurante restaurante, Reclamo reclamo){
        List<Reclamo> reclamos = restaurante.getReclamos();
        reclamos.add(reclamo);
        restauranteRepository.save(restaurante);
    }

    public JsonArray listarReclamos(String correoRestaurante, boolean orden, String correoCliente, String razon) throws RestauranteNoEncontradoException {
        Restaurante restaurante = obtenerRestaurante(correoRestaurante);
        if (orden) {
            if (!correoCliente.isBlank()){

            }
            if (!razon.isBlank()){

            }
            return reclamoConverter.arrayJsonReclamo(restaurante.getReclamos());
        } else {
            if (!correoCliente.isBlank()){
                return reclamoConverter.arrayJsonReclamo(obtenerReclamoCliente(restaurante,correoCliente));
            }
            if (!razon.isBlank()){
                return reclamoConverter.arrayJsonReclamo(obtenerReclamoRazon(restaurante,razon));
            }
            return reclamoConverter.arrayJsonReclamo(restaurante.getReclamos());
        }
    }

    public List<Reclamo> obtenerReclamoCliente (Restaurante restaurante, String correoCliente){
        List<Reclamo> reclamos = new ArrayList<>();
        for (Reclamo reclamo : restaurante.getReclamos()){
            if (reclamo.getPedido() != null && reclamo.getPedido().getCliente() != null && !reclamo.getPedido().getCliente().getCorreo().isBlank()){
                if (reclamo.getPedido().getCliente().getCorreo().equals(correoCliente)){
                    reclamos.add(reclamo);
                }
            }
        }
        return reclamos;
    }

    public List<Reclamo> obtenerReclamoRazon (Restaurante restaurante, String razon){
        List<Reclamo> reclamos = new ArrayList<>();
        for (Reclamo reclamo : restaurante.getReclamos()){
            if (!reclamo.getRazon().isBlank() && reclamo.getRazon().contains(razon)){
                reclamos.add(reclamo);
            }
        }
        return reclamos;
    }

}

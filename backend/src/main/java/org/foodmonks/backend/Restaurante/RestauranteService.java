package org.foodmonks.backend.Restaurante;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Exceptions.ClienteDireccionException;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Menu.Exceptions.MenuIdException;
import org.foodmonks.backend.Menu.Exceptions.MenuMultiplicadorException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import org.foodmonks.backend.Menu.Exceptions.MenuPrecioException;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Pedido.Exceptions.PedidoIdException;
import org.foodmonks.backend.Pedido.Exceptions.PedidoNoExisteException;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteFaltaMenuException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoRestaurante;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.foodmonks.backend.datatypes.MedioPago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class RestauranteService {

    private final PasswordEncoder passwordEncoder;
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final MenuService menuService;
    private final RestauranteConverter restauranteConverter;
    private final PedidoService pedidoService;
    private final TemplateEngine templateEngine;

    @Autowired

    public RestauranteService(
        RestauranteRepository restauranteRepository,
        PasswordEncoder passwordEncoder,
        UsuarioRepository usuarioRepository,
        MenuService menuService,
        RestauranteConverter restauranteConverter,
        PedidoService pedidoService,
        TemplateEngine templateEngine
        ) {
            this.restauranteRepository = restauranteRepository;
            this.passwordEncoder = passwordEncoder;
            this.usuarioRepository = usuarioRepository; 
            this.menuService = menuService; 
            this.restauranteConverter = restauranteConverter;
            this.pedidoService = pedidoService;
            this.templateEngine = templateEngine;
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
        if (!nombreRestaurante.isEmpty()){
            return restauranteConverter.listaRestaurantes(restauranteRepository.findRestaurantesByNombreRestauranteContainsAndEstado(nombreRestaurante,EstadoRestaurante.ABIERTO));
        }
        if (ordenCalificacion) {
            return  restauranteConverter.listaRestaurantes(restauranteRepository.findRestaurantesByEstadoOrderByCalificacionDesc(EstadoRestaurante.ABIERTO));
        }
        List<Restaurante> restaurantes = restauranteRepository.findRestaurantesByEstado(EstadoRestaurante.ABIERTO);
        if (!categoriaMenu.isEmpty()) {
            List<Restaurante> result = new ArrayList<>();
            CategoriaMenu categoria = CategoriaMenu.valueOf(categoriaMenu);
            for (Restaurante restaurante : restaurantes){
                if (menuService.existeCategoriaMenu(restaurante,categoria)){
                    result.add(restaurante);
                }
            }
            return restauranteConverter.listaRestaurantes(result);
        }
        return restauranteConverter.listaRestaurantes(restaurantes);
    }

    //Si el estado es "CONFIRMADO" se agregan el tiempo a fechaHoraEntrega (a partir de fechaHoraProcesado)
    public void actualizarEstadoPedido(String correo, Long idPedido, String estado, Integer minutos) throws RestauranteNoEncontradoException, PedidoNoExisteException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        if (!pedidoService.existePedidoRestaurante(idPedido,restaurante)){
            throw new RestauranteNoEncontradoException("No existe el pedido con id " + idPedido + " para el Restaurante " + correo);
        }
        Pedido pedido = pedidoService.buscarPedidoId(idPedido);

        if (estado.equals("CONFIRMADO")){
            pedidoService.cambiarFechasEntregaProcesado(idPedido, minutos);
            if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)){
                pedidoService.cambiarEstadoPedido(idPedido, EstadoPedido.CONFIRMADO);
            }else{
                pedidoService.cambiarEstadoPedido(idPedido, EstadoPedido.FINALIZADO);
            }
        }else if (estado.equals("RECHAZADO")){
            pedidoService.cambiarEstadoPedido(idPedido, EstadoPedido.RECHAZADO);
            if (pedido.getMedioPago().equals(MedioPago.PAYPAL)){
                // HACER DEVOLUCION DE PAYPAL
            }
        }
    }
  
  
    public Restaurante obtenerRestaurante(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
      return restaurante;
    }

    public List<JsonObject> listarPedidosPendientes(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante==null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return pedidoService.listaPedidosPendientes(restaurante);
    }

    public JsonObject listarHistoricoPedidos(String correo, String estadoPedido, String medioPago, String orden, String fecha, String total, String page, String size) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreo(correo);
        if (restaurante==null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        String[] _total = (total!=null && total.contains(",")) ? total.split(",") : null;
        String[] _fecha = (fecha!=null && fecha.contains(",")) ? fecha.split(",") : null;
        //String[] _order = (!orden.isEmpty() && orden.contains(",")) ? orden.split(",") : null;
        Float[] totalFinal = new Float[2];
        LocalDateTime[] fechaFinal = new LocalDateTime[2];

        EstadoPedido estado = null;
        MedioPago pago = null;
        int pageFinal = 0;
        int sizeFinal = 10;
        if (estadoPedido!= null && !estadoPedido.equals("")) {
            try {
                estado = EstadoPedido.valueOf(estadoPedido.trim().toUpperCase(Locale.ROOT));
            }catch(IllegalArgumentException e){
                estado = null;
            }
        }
        if (medioPago!= null && !medioPago.equals("")) {
            try{
                pago = MedioPago.valueOf(medioPago.trim().toUpperCase(Locale.ROOT));
            }catch(IllegalArgumentException e){
                pago = null;
            }
        }

        if (_total != null && _total[0] != null && _total[1] != null){
            try{
                totalFinal[0] = Math.abs(Float.valueOf(_total[0]));
                totalFinal[1] = Math.abs(Float.valueOf(_total[1]));
            }catch(NumberFormatException e){
                totalFinal = null;
            }
        }else
            totalFinal = null;

        if (_fecha != null && _fecha[0] != null && _fecha[1] != null){
            try{
                fechaFinal[0] = LocalDateTime.of(LocalDate.parse(_fecha[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalTime.MIDNIGHT);
                fechaFinal[1] = LocalDateTime.of(LocalDate.parse(_fecha[1], DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalTime.MIDNIGHT);
            }catch(DateTimeException e){
                fechaFinal = null;
            }
        }else
            fechaFinal = null;

        try{
            pageFinal = Integer.parseInt(page);
            sizeFinal = Integer.parseInt(size);
        }catch(NumberFormatException e){
            e.printStackTrace();
            pageFinal = 0;
            sizeFinal = 5;
        }
        return pedidoService.listaPedidosHistorico(restaurante, estado, pago, orden, fechaFinal, totalFinal, pageFinal, sizeFinal);
    }

    public JsonObject realizarDevolucion(String correoRestaurante, String idPedido, String montoDevolucion, String motivoDevolucion, Boolean estadoDevolucion) throws PedidoNoExisteException, PedidoIdException, RestauranteNoEncontradoException {
        Restaurante restaurante = obtenerRestaurante(correoRestaurante);
        if (!idPedido.matches("[0-9]*") || idPedido.isBlank()){
            throw new PedidoIdException("El id del pedido no es un numero entero");
        }
        Pedido pedido = pedidoService.obtenerPedido(Long.valueOf(idPedido));
        if (estadoDevolucion) {
            if (pedido.getEstado().equals(EstadoPedido.FINALIZADO) && pedido.getMedioPago().equals(MedioPago.EFECTIVO)){
                pedidoService.cambiarEstadoPedido(pedido.getId(),EstadoPedido.DEVUELTO);

            } else if ((pedido.getEstado().equals(EstadoPedido.FINALIZADO) && pedido.getMedioPago().equals(MedioPago.PAYPAL))){

            }
        } else {
            if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)) {

            } else if (pedido.getMedioPago().equals(MedioPago.PAYPAL)){
                Context context = new Context();
                context.setVariable("user", pedido.getCliente().getNombre());
                context.setVariable("contenido","Su reclamo al pedido # ");
                String htmlContent = templateEngine.process("bloquear-desbloquear-eliminar", context);
            }
        }
        JsonObject response = new JsonObject();
        response.addProperty("Mensaje", "Devolucion prueba");
        return response;
    }
}

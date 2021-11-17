package org.foodmonks.backend.Restaurante;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Exceptions.ClienteDireccionException;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Menu.Exceptions.MenuIdException;
import org.foodmonks.backend.Menu.Exceptions.MenuMultiplicadorException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import org.foodmonks.backend.Menu.Exceptions.MenuPrecioException;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Pedido.Exceptions.PedidoDevolucionException;
import org.foodmonks.backend.Pedido.Exceptions.PedidoDistintoRestauranteException;
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
import org.foodmonks.backend.paypal.PayPalService;
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

import java.io.IOException;
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
    private final EmailService emailService;
    private final PayPalService payPalService;

    @Autowired

    public RestauranteService(
        RestauranteRepository restauranteRepository,
        PasswordEncoder passwordEncoder,
        UsuarioRepository usuarioRepository,
        MenuService menuService,
        RestauranteConverter restauranteConverter,
        PedidoService pedidoService,
        TemplateEngine templateEngine,
        EmailService emailService,
        PayPalService payPalService
        ) {
            this.restauranteRepository = restauranteRepository;
            this.passwordEncoder = passwordEncoder;
            this.usuarioRepository = usuarioRepository; 
            this.menuService = menuService; 
            this.restauranteConverter = restauranteConverter;
            this.pedidoService = pedidoService;
            this.templateEngine = templateEngine;
            this.emailService = emailService;
            this.payPalService = payPalService;
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

    public JsonObject realizarDevolucion(String correoRestaurante, String idPedido, String montoDevolucion, String motivoDevolucion, Boolean estadoDevolucion) throws PedidoNoExisteException, PedidoIdException, RestauranteNoEncontradoException, EmailNoEnviadoException, IOException, PedidoDevolucionException, PedidoDistintoRestauranteException {
        Restaurante restaurante = obtenerRestaurante(correoRestaurante);
        JsonObject response = new JsonObject();
        response.addProperty("Mensaje", "Devolucion");
        if (!idPedido.matches("[0-9]*") || idPedido.isBlank()){
            throw new PedidoIdException("El id del pedido no es un numero entero");
        }
        Pedido pedido = pedidoService.obtenerPedido(Long.valueOf(idPedido));
        if (!pedido.getEstado().equals(EstadoPedido.FINALIZADO)){
            throw new PedidoDevolucionException("El pedido no esta FINALIZADO, no se puede aplicar una devolucion");
        }
        if (!pedido.getRestaurante().getCorreo().equals(restaurante.getCorreo())){
            throw new PedidoDistintoRestauranteException("El pedido id "+ idPedido + " no pertenece al restaurante " + correoRestaurante);
        }
        Context context = new Context();
        context.setVariable("user", pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido());
        String[] cc = new String[1];
        cc[0] = pedido.getRestaurante().getCorreo();
        String htmlContent = "";
        if (estadoDevolucion) {
            if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)){
                pedidoService.cambiarEstadoPedido(pedido.getId(),EstadoPedido.DEVUELTO);
                response.addProperty("status","completado");
                context.setVariable("contenido", "Su reclamo al restaurante " + restaurante.getNombreRestaurante() + " ha sido aceptado.");
                context.setVariable("pedido", "Identificador pedido: #" + pedido.getId());
                context.setVariable("monto", "Monto: " + pedido.getTotal().toString());
                context.setVariable("mensaje", "Para la devolución deberá contactar con el restaurante por alguno de los siguientes medios:");
                context.setVariable("direccion", "Direccion: " + restaurante.getDireccion().getCalle() + " " +
                        restaurante.getDireccion().getNumero() + ", esquina " + restaurante.getDireccion().getEsquina());
                context.setVariable("telefono", "Telefono: " + restaurante.getTelefono().toString());
                context.setVariable("email", "Email: " + restaurante.getCorreo());
                htmlContent = templateEngine.process("reclamo-aceptado-efectivo", context);
            } else if (pedido.getMedioPago().equals(MedioPago.PAYPAL)){
                response.addProperty("status", payPalService.refundOrder(payPalService.getOrder(pedido.getOrdenPaypal().getOrdenId())));
                context.setVariable("contenido", "Su reclamo al restaurante " + restaurante.getNombreRestaurante() + " ha sido aceptado.");
                context.setVariable("pedido", "Identificador pedido: #" + pedido.getId());
                context.setVariable("paypal", "Identificador Paypal: order#" + pedido.getOrdenPaypal().getOrdenId());
                context.setVariable("mensaje", "Te hemos realizado una devolucion de $" + pedido.getTotal() + " en tu cuenta de Paypal.");
                htmlContent = templateEngine.process("reclamo-aceptado-paypal", context);
            }
            emailService.enviarMail(pedido.getCliente().getCorreo(), "Reclamo aceptado", htmlContent,cc);
            pedidoService.cambiarEstadoPedido(pedido.getId(),EstadoPedido.DEVUELTO);
        } else {
            if (pedido.getMedioPago().equals(MedioPago.PAYPAL)) {
                context.setVariable("contenido","Su reclamo al restaurante " + restaurante.getNombreRestaurante() + ", ha sido rechazado.");
                context.setVariable("pedido","Identificador pedido: #" + pedido.getId());
                context.setVariable("paypal", "Identificador Paypal order#" + pedido.getOrdenPaypal().getOrdenId());
                context.setVariable("motivo",motivoDevolucion);
                htmlContent = templateEngine.process("reclamo-rechazado-paypal", context);
            } else if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)){
                context.setVariable("contenido","Su reclamo al restaurante " + restaurante.getNombreRestaurante() + ", ha sido rechazado.");
                context.setVariable("pedido","Identificador pedido: #" + pedido.getId());
                context.setVariable("motivo",motivoDevolucion);
                htmlContent = templateEngine.process("reclamo-rechazado-efectivo", context);
            }
            emailService.enviarMail(pedido.getCliente().getCorreo(), "Reclamo rechazado", htmlContent,cc);
            response.addProperty("status","Mail de rechazo enviado");
            pedidoService.cambiarEstadoPedido(pedido.getId(),EstadoPedido.RECLAMORECHAZADO);
        }
        return response;
    }
}

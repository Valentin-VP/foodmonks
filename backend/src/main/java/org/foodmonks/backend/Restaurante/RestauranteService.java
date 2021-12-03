package org.foodmonks.backend.Restaurante;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.jav.exposerversdk.PushClientException;
import org.foodmonks.backend.Cliente.Exceptions.ClienteDireccionException;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Menu.Exceptions.MenuMultiplicadorException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import org.foodmonks.backend.Menu.Exceptions.MenuPrecioException;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.MenuCompra.MenuCompra;
import org.foodmonks.backend.Pedido.Exceptions.PedidoFechaProcesadoException;
import org.foodmonks.backend.Pedido.Exceptions.PedidoNoExisteException;
import org.foodmonks.backend.Pedido.Exceptions.*;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Reclamo.Reclamo;
import org.foodmonks.backend.Reclamo.ReclamoConverter;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteFaltaMenuException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioExisteException;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoRestaurante;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.foodmonks.backend.datatypes.*;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.foodmonks.backend.datatypes.MedioPago;
import org.foodmonks.backend.notificacion.NotificacionExpoService;
import org.foodmonks.backend.paypal.PayPalService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Comparator;
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
    private final ReclamoConverter reclamoConverter;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;
    private final PayPalService payPalService;
    private final NotificacionExpoService notificacionExpoService;

    @Autowired
    public RestauranteService(RestauranteRepository restauranteRepository, PasswordEncoder passwordEncoder,
            UsuarioRepository usuarioRepository, MenuService menuService, RestauranteConverter restauranteConverter,
            PedidoService pedidoService, ReclamoConverter reclamoConverter, TemplateEngine templateEngine,
            EmailService emailService, PayPalService payPalService, NotificacionExpoService notificacionExpoService) {
        this.restauranteRepository = restauranteRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.menuService = menuService;
        this.restauranteConverter = restauranteConverter;
        this.pedidoService = pedidoService;
        this.reclamoConverter = reclamoConverter;
        this.templateEngine = templateEngine;
        this.emailService = emailService;
        this.payPalService = payPalService;
        this.notificacionExpoService = notificacionExpoService;
    }

    public JsonArray listarRestaurante(){
        List<Restaurante> result = new ArrayList<>();
        result.addAll(restauranteRepository.findRestaurantesIgnoreCaseByEstado(EstadoRestaurante.ABIERTO));
        result.addAll(restauranteRepository.findRestaurantesIgnoreCaseByEstado(EstadoRestaurante.CERRADO));
        return restauranteConverter.arrayJsonRestaurantes(result);
    }

    public Restaurante buscarRestaurante(String correo) {
        return restauranteRepository.findByCorreoIgnoreCase(correo);
    }

    public void editarRestaurante(Restaurante restaurante) {
        restauranteRepository.save(restaurante);
    }

    public void modificarEstado(String correo, String estado) {
        Restaurante restauranteAux = restauranteRepository.findByCorreoIgnoreCase(correo);
        restauranteAux.setEstado(EstadoRestaurante.valueOf(estado));
        restauranteRepository.save(restauranteAux);
    }

    public void createSolicitudAltaRestaurante(String nombre, String apellido, String correo, String password,
            LocalDate now, float calificacion, String nombreRestaurante, String rut, Direccion direccion,
            String pendiente, String telefono, String descripcion, String cuentaPaypal, String url,
            ArrayList<JsonObject> jsonMenus)
            throws UsuarioExisteException, ClienteDireccionException, RestauranteFaltaMenuException,
            UsuarioNoRestaurante, MenuNombreExistente, MenuPrecioException, MenuMultiplicadorException, MenuNombreException {
        if (usuarioRepository.findByCorreoIgnoreCase(correo) != null) {
            throw new UsuarioExisteException("Ya existe un Usuario registrado con el correo " + correo);
        }
        if (direccion == null) {
            throw new ClienteDireccionException("Debe ingresar una direccion");
        }
        if (jsonMenus.size() < 3) {
            throw new RestauranteFaltaMenuException("Debe ingresar al menos 3 menus");
        }
        Restaurante restaurante = new Restaurante(nombre, apellido, correo, passwordEncoder.encode(password), now,
                calificacion, 0, nombreRestaurante, Long.valueOf(rut), direccion, EstadoRestaurante.valueOf(pendiente), Integer.valueOf(telefono),
                descripcion, cuentaPaypal, url);
        restauranteRepository.save(restaurante);
        for (JsonObject menu : jsonMenus) {
            menuService.altaMenu(menu, restaurante.getCorreo());
        }
    }

    public EstadoRestaurante restauranteEstado(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreoIgnoreCase(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return restaurante.getEstado();
    }

    public JsonObject obtenerJsonRestaurante(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreoIgnoreCase(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return restauranteConverter.jsonRestaurante(restaurante);

    }

    public void registrarPagoEfectivo(String correo, Long idPedido)
            throws RestauranteNoEncontradoException, PedidoNoExisteException {
        Restaurante restaurante = restauranteRepository.findByCorreoIgnoreCase(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        if (!pedidoService.existePedido(idPedido)) {
            throw new PedidoNoExisteException("No existe el pedido con id " + idPedido);
        }
        if (!pedidoService.existePedidoRestaurante(idPedido, restaurante)) {
            throw new RestauranteNoEncontradoException(
                    "No existe el pedido con id " + idPedido + " para el Restaurante " + correo);
        }
        pedidoService.cambiarEstadoPedido(idPedido, EstadoPedido.FINALIZADO);
    }

    public List<JsonObject> listarPedidosEfectivoConfirmados(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreoIgnoreCase(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return pedidoService.listaPedidosEfectivoConfirmados(restaurante);
    }

    public List<JsonObject> listaRestaurantesAbiertos(String nombreRestaurante, String categoriaMenu,
            boolean ordenCalificacion) {

        if (ordenCalificacion) {
            if (!nombreRestaurante.isBlank()) {
                return restauranteConverter.listaRestaurantes(restauranteRepository
                        .findRestaurantesByNombreRestauranteIgnoreCaseContainsAndEstadoOrderByCalificacionDesc(nombreRestaurante,
                                EstadoRestaurante.ABIERTO));
            }
            if (!categoriaMenu.isBlank()) {
                List<Restaurante> restaurantes = restauranteRepository
                        .findRestaurantesIgnoreCaseByEstadoOrderByCalificacionDesc(EstadoRestaurante.ABIERTO);
                CategoriaMenu categoria = CategoriaMenu.valueOf(categoriaMenu);
                return obtenerRestauranteMenuConCategoria(restaurantes, categoria);
            }
            return restauranteConverter.listaRestaurantes(
                    restauranteRepository.findRestaurantesIgnoreCaseByEstadoOrderByCalificacionDesc(EstadoRestaurante.ABIERTO));
        } else {
            if (!nombreRestaurante.isBlank()) {
                return restauranteConverter.listaRestaurantes(
                        restauranteRepository.findRestaurantesByNombreRestauranteIgnoreCaseContainsAndEstado(nombreRestaurante,
                                EstadoRestaurante.ABIERTO));
            }
            if (!categoriaMenu.isBlank()) {
                List<Restaurante> restaurantes = restauranteRepository
                        .findRestaurantesIgnoreCaseByEstado(EstadoRestaurante.ABIERTO);
                CategoriaMenu categoria = CategoriaMenu.valueOf(categoriaMenu);
                return obtenerRestauranteMenuConCategoria(restaurantes, categoria);
            }
            return restauranteConverter
                    .listaRestaurantes(restauranteRepository.findRestaurantesIgnoreCaseByEstado(EstadoRestaurante.ABIERTO));
        }
    }

    public List<JsonObject> obtenerRestauranteMenuConCategoria(List<Restaurante> restaurantes,
            CategoriaMenu categoriaMenu) {
        List<Restaurante> result = new ArrayList<>();
        for (Restaurante restaurante : restaurantes) {
            if (menuService.existeCategoriaMenu(restaurante, categoriaMenu)) {
                result.add(restaurante);
            }
        }
        return restauranteConverter.listaRestaurantes(result);
    }

    // Si el estado es "CONFIRMADO" se agregan el tiempo a fechaHoraEntrega (a
    // partir de fechaHoraProcesado)
    public void actualizarEstadoPedido(String correo, Long idPedido, String estado, Integer minutos)
            throws RestauranteNoEncontradoException, PedidoNoExisteException, PedidoIdException, PedidoDevolucionException, PedidoDistintoRestauranteException, IOException, EmailNoEnviadoException, PushClientException, InterruptedException {
        Restaurante restaurante = restauranteRepository.findByCorreoIgnoreCase(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        if (!pedidoService.existePedidoRestaurante(idPedido, restaurante)) {
            throw new RestauranteNoEncontradoException(
                    "No existe el pedido con id " + idPedido + " para el Restaurante " + correo);
        }
        Pedido pedido = pedidoService.obtenerPedido(idPedido);
        if (estado.equals("CONFIRMADO")) {
            pedidoService.cambiarFechasEntregaProcesado(idPedido, minutos);
            if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)) {
                pedidoService.cambiarEstadoPedido(idPedido, EstadoPedido.CONFIRMADO);
            } else {
                pedidoService.cambiarEstadoPedido(idPedido, EstadoPedido.FINALIZADO);
            }
            //MAIL
            Context context = new Context();
            context.setVariable("user", "Gracias " + pedido.getCliente().getNombre() + " " +  pedido.getCliente().getApellido() + "!");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            context.setVariable("contenido", restaurante.getNombreRestaurante() + " ya esta preparando tu pedido. La hora estimada de entrega es " + formatter.format(LocalTime.now().plusMinutes(minutos)) + " y " + formatter.format(LocalTime.now().plusMinutes(minutos+15)));
            String htmlContent = templateEngine.process("aprobar-rechazar", context);
            emailService.enviarMail(pedido.getCliente().getCorreo(),"Confirmado Pedido #" + pedido.getId(),htmlContent,null);
            //PUSH NOTIFICATION
            if (pedido.getCliente().getMobileToken() != null && !pedido.getCliente().getMobileToken().isBlank()){
                notificacionExpoService.crearNotifacion(pedido.getCliente().getMobileToken(),"Confirmado Pedido #" + pedido.getId(),restaurante.getNombreRestaurante() + " ya esta preparando tu pedido. La hora estimada de entrega es " + formatter.format(LocalTime.now().plusMinutes(minutos)) + " y " + formatter.format(LocalTime.now().plusMinutes(minutos+15)));
            }
        } else if (estado.equals("RECHAZADO")) {
            pedidoService.cambiarEstadoPedido(idPedido, EstadoPedido.RECHAZADO);
            //ENVIO DE MAIL DE RECHAZO
            Context context = new Context();
            context.setVariable("user", "Estimado " + pedido.getCliente().getNombre() + " " +  pedido.getCliente().getApellido() + ".");
            context.setVariable("contenido", restaurante.getNombreRestaurante() + " ha rechazado el pedido que realizaste.");
            String htmlContent = templateEngine.process("aprobar-rechazar", context);
            emailService.enviarMail(pedido.getCliente().getCorreo(),"Rechazado Pedido #" + pedido.getId(),htmlContent,null);
            if (pedido.getMedioPago().equals(MedioPago.PAYPAL)) {
                realizarDevolucion(correo,String.valueOf(idPedido),"rechazado",true);
            }
            // PUSH NOTIFICATION
            if (pedido.getCliente().getMobileToken() != null && !pedido.getCliente().getMobileToken().isBlank()){
                notificacionExpoService.crearNotifacion(pedido.getCliente().getMobileToken(),"Rechazado Pedido #" + pedido.getId(),restaurante.getNombreRestaurante() + " ha rechazado el pedido que realizaste");
            }
        }
    }

    public Restaurante obtenerRestaurante(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreoIgnoreCase(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return restaurante;
    }

    public List<JsonObject> listarPedidosPendientes(String correo) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreoIgnoreCase(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }
        return pedidoService.listaPedidosPendientes(restaurante);
    }

    public JsonObject listarHistoricoPedidos(String correo, String estadoPedido, String medioPago, String orden,
            String fecha, String total, String page, String size) throws RestauranteNoEncontradoException {
        Restaurante restaurante = restauranteRepository.findByCorreoIgnoreCase(correo);
        if (restaurante == null) {
            throw new RestauranteNoEncontradoException("No existe el Restaurante " + correo);
        }

        String[] _total = (total!=null && total.contains(",")) ? total.split(",") : null;
        String[] _fecha = (fecha!=null && fecha.contains(",")) ? fecha.split(",") : null;

        String[] _order = (!orden.isEmpty() && orden.contains(",")) ? orden.split(",") : null;

        Float[] totalFinal = new Float[2];
        LocalDateTime[] fechaFinal = new LocalDateTime[2];

        EstadoPedido estado = null;
        MedioPago pago = null;
        int pageFinal;
        int sizeFinal;
        if (estadoPedido != null && !estadoPedido.equals("")) {
            estado = EstadoPedido.valueOf(estadoPedido.trim().toUpperCase(Locale.ROOT));
        }

        if (medioPago != null && !medioPago.equals("")) {
            pago = MedioPago.valueOf(medioPago.trim().toUpperCase(Locale.ROOT));
        }

        if (_total != null && _total[0] != null && _total[1] != null) {
            totalFinal[0] = Math.abs(Float.valueOf(_total[0]));
            totalFinal[1] = Math.abs(Float.valueOf(_total[1]));
        } else {
            totalFinal = null;
        }

        if (_fecha != null && _fecha[0] != null && _fecha[1] != null) {
            try {
                fechaFinal[0] = LocalDateTime.of(LocalDate.parse(_fecha[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        LocalTime.MIDNIGHT);
                fechaFinal[1] = LocalDateTime.of(LocalDate.parse(_fecha[1], DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        LocalTime.MIDNIGHT);
            } catch (DateTimeException e) {
                fechaFinal = null;
            }
        } else {
            fechaFinal = null;
        }

        pageFinal = Integer.parseInt(page);
        sizeFinal = Integer.parseInt(size);
        return pedidoService.listaPedidosHistorico(restaurante, estado, pago, orden, fechaFinal, totalFinal, pageFinal,
                sizeFinal);
    }

    public void calificarRestaurante(String correoCliente, JsonObject jsonCalificacion)
            throws PedidoNoExisteException, PedidoIdException, PedidoPuntajeException, PedidoClienteException,
            PedidoEstadoException, PedidoCalificacionRestauranteException {
        verificarJsonCalificacion(jsonCalificacion);
        Pedido pedido = pedidoService.obtenerPedido(jsonCalificacion.get("idPedido").getAsLong());
        if (!pedido.getCliente().getCorreo().equals(correoCliente)) {
            throw new PedidoClienteException("El cliente " + correoCliente + " no es cliente del pedido id "
                    + jsonCalificacion.get("idPedido").getAsString());
        }
        if (!pedido.getEstado().equals(EstadoPedido.FINALIZADO) && !pedido.getEstado().equals(EstadoPedido.DEVUELTO)
                && !pedido.getEstado().equals(EstadoPedido.RECLAMORECHAZADO)) {
            throw new PedidoEstadoException("El pedido id " + jsonCalificacion.get("idPedido").getAsString()
                    + "no esta en EstadoPedido para calificar");
        }
        if (pedido.getCalificacionRestaurante() != null) {
            throw new PedidoCalificacionRestauranteException("El pedido con id "
                    + jsonCalificacion.get("idPedido").getAsString() + " ya tiene calificacion Restaurante");
        }
        DtCalificacion calificacion = new DtCalificacion(jsonCalificacion.get("puntaje").getAsFloat(),
                jsonCalificacion.get("comentario").getAsString());
        pedidoService.modificarCalificacionRestaurantePedido(jsonCalificacion.get("idPedido").getAsLong(),
                calificacion);
        Restaurante restaurante = pedido.getRestaurante();
        restaurante.setCalificacion(
                (restaurante.getCalificacion() * restaurante.getCantidadCalificaciones() + calificacion.getPuntaje())
                        / (restaurante.getCantidadCalificaciones() + 1));
        restaurante.setCantidadCalificaciones(restaurante.getCantidadCalificaciones() + 1);
        restauranteRepository.save(restaurante);
    }

    public void modificarCalificacionRestaurante(String correoCliente, JsonObject jsonCalificacion)
            throws PedidoNoExisteException, PedidoIdException, PedidoPuntajeException, PedidoClienteException,
            PedidoCalificacionRestauranteException {
        verificarJsonCalificacion(jsonCalificacion);
        Pedido pedido = pedidoService.obtenerPedido(jsonCalificacion.get("idPedido").getAsLong());
        if (!pedido.getCliente().getCorreo().equals(correoCliente)) {
            throw new PedidoClienteException("El cliente " + correoCliente + " no es cliente del pedido id "
                    + jsonCalificacion.get("idPedido").getAsString());
        }
        if (pedido.getCalificacionRestaurante() == null) {
            throw new PedidoCalificacionRestauranteException("El pedido con id "
                    + jsonCalificacion.get("idPedido").getAsString() + " no tiene calificacion Restaurante");
        }
        DtCalificacion calificacion = pedido.getCalificacionRestaurante();
        DtCalificacion calificacionNueva = new DtCalificacion(jsonCalificacion.get("puntaje").getAsFloat(),
                jsonCalificacion.get("comentario").getAsString());
        pedidoService.modificarCalificacionRestaurantePedido(jsonCalificacion.get("idPedido").getAsLong(),
                calificacionNueva);
        Restaurante restaurante = pedido.getRestaurante();
        restaurante.setCalificacion(
                (restaurante.getCalificacion() * restaurante.getCantidadCalificaciones() - calificacion.getPuntaje()
                        + calificacionNueva.getPuntaje()) / restaurante.getCantidadCalificaciones());
        restauranteRepository.save(restaurante);
    }

    public void eliminarCalificacionRestaurante(String correoCliente, String idPedido) throws PedidoNoExisteException,
            PedidoIdException, PedidoClienteException, PedidoCalificacionRestauranteException {
        if (!idPedido.matches("[0-9]*") || idPedido.isBlank()) {
            throw new PedidoIdException("El id del pedido no es un numero entero");
        }
        Pedido pedido = pedidoService.obtenerPedido(Long.valueOf(idPedido));
        if (!pedido.getCliente().getCorreo().equals(correoCliente)) {
            throw new PedidoClienteException(
                    "El cliente " + correoCliente + " no es cliente del pedido id " + idPedido);
        }
        if (pedido.getCalificacionRestaurante() == null) {
            throw new PedidoCalificacionRestauranteException(
                    "El pedido con id " + idPedido + " no tiene calificacion Restaurante");
        }
        DtCalificacion calificacion = pedido.getCalificacionRestaurante();
        pedidoService.modificarCalificacionRestaurantePedido(Long.valueOf(idPedido), null);
        Restaurante restaurante = pedido.getRestaurante();
        if (restaurante.getCantidadCalificaciones() == 1) {
            restaurante.setCalificacion(5f);
            restaurante.setCantidadCalificaciones(0);
        } else {
            restaurante.setCalificacion((restaurante.getCalificacion() * restaurante.getCantidadCalificaciones()
                    - calificacion.getPuntaje()) / (restaurante.getCantidadCalificaciones() - 1));
            restaurante.setCantidadCalificaciones(restaurante.getCantidadCalificaciones() - 1);
        }
        restauranteRepository.save(restaurante);
    }

    public void verificarJsonCalificacion(JsonObject jsonCalificacion)
            throws PedidoIdException, PedidoPuntajeException {
        if (!jsonCalificacion.get("idPedido").getAsString().matches("[0-9]*")
                || jsonCalificacion.get("idPedido").getAsString().isBlank()) {
            throw new PedidoIdException("El id del pedido no es un numero entero");
        }
        if (!jsonCalificacion.get("puntaje").getAsString().matches("[1-5]")
                || jsonCalificacion.get("puntaje").getAsString().isBlank()) {
            throw new PedidoPuntajeException("El puntaje del pedido no es un numero entero o no es un valor posible");
        }
    }

    public void agregarReclamoRestaurante(Restaurante restaurante, Reclamo reclamo) {
        List<Reclamo> reclamos = restaurante.getReclamos();
        reclamos.add(reclamo);
        restauranteRepository.save(restaurante);
    }

    public JsonArray listarReclamos(String correoRestaurante, boolean orden, String correoCliente, String razon)
            throws RestauranteNoEncontradoException {
        Restaurante restaurante = obtenerRestaurante(correoRestaurante);
        List<Reclamo> reclamos;
        if (!correoCliente.isBlank() && !correoCliente.isBlank()) {
            reclamos = obtenerReclamoClienteRazon(restaurante, correoCliente, razon);
        } else if (!correoCliente.isBlank()) {
            reclamos = obtenerReclamoCliente(restaurante, correoCliente);
        } else if (!razon.isBlank()) {
            reclamos = obtenerReclamoRazon(restaurante, razon);
        } else {
            reclamos = restaurante.getReclamos();
        }
        if (orden) {
            reclamos.sort(Comparator.comparing(Reclamo::getFecha).reversed());
        }
        return reclamoConverter.arrayJsonReclamo(reclamos);
    }

    public List<Reclamo> obtenerReclamoClienteRazon(Restaurante restaurante, String correoCliente, String razon) {
        List<Reclamo> reclamos = new ArrayList<>();
        for (Reclamo reclamo : restaurante.getReclamos()) {
            if (reclamo.getPedido() != null && reclamo.getPedido().getCliente() != null
                    && !reclamo.getPedido().getCliente().getCorreo().isBlank()) {
                if (reclamo.getPedido().getCliente().getCorreo().contains(correoCliente)
                        && !reclamo.getRazon().isBlank() && reclamo.getRazon().contains(razon)) {
                    reclamos.add(reclamo);
                }
            }
        }
        return reclamos;
    }

    public List<Reclamo> obtenerReclamoCliente(Restaurante restaurante, String correoCliente) {
        List<Reclamo> reclamos = new ArrayList<>();
        for (Reclamo reclamo : restaurante.getReclamos()) {
            if (reclamo.getPedido() != null && reclamo.getPedido().getCliente() != null
                    && !reclamo.getPedido().getCliente().getCorreo().isBlank()) {
                if (reclamo.getPedido().getCliente().getCorreo().contains(correoCliente)) {
                    reclamos.add(reclamo);
                }
            }
        }
        return reclamos;
    }

    public List<Reclamo> obtenerReclamoRazon(Restaurante restaurante, String razon) {
        List<Reclamo> reclamos = new ArrayList<>();
        for (Reclamo reclamo : restaurante.getReclamos()) {
            if (!reclamo.getRazon().isBlank() && reclamo.getRazon().contains(razon)) {
                reclamos.add(reclamo);
            }
        }
        return reclamos;
    }

    public JsonObject obtenerBalance(String correoRestaurante,String medioPago, String fechaIni, String fechaFin,String categoriaMenu) throws Exception {

        Restaurante restaurante = obtenerRestaurante(correoRestaurante);

        JsonObject balance = new JsonObject();
        JsonArray meses = new JsonArray();
        JsonArray totales = new JsonArray();

        float totalVentasEfectivo = 0;
        int totalCantidadVentasEfectivo = 0;
        float totalVentasPaypal = 0;
        int totalCantidadVentasPayPal = 0;
        float totalDevolucionesEfectivo = 0;
        int totalCantidadDevolucionesEfectivo = 0;
        float totalDevolucionesPayPal = 0;
        int totalCantidadDevolucionesPayPal = 0;
        String mes = "";
        // Preparo

        // Categoria Menu
        CategoriaMenu categoria = null;
        if (categoriaMenu!= null && !categoriaMenu.isBlank()) {
            categoria = CategoriaMenu.valueOf(categoriaMenu.trim().toUpperCase(Locale.ROOT));
        }

        // Medio Pago
        MedioPago pago = null;

        if (medioPago!= null && !medioPago.isBlank()) {
            pago = MedioPago.valueOf(medioPago.trim().toUpperCase(Locale.ROOT));
        }

        // Fecha
        LocalDateTime fechaInicial = null;
        LocalDateTime fechaFinal = null;

        if (fechaIni != null && !fechaIni.isBlank()){
            fechaInicial = LocalDateTime.of(LocalDate.parse(fechaIni),LocalTime.MIDNIGHT);
        }

        if (fechaFin != null && !fechaFin.isBlank()){
            fechaFinal = LocalDateTime.of(LocalDate.parse(fechaFin),LocalTime.MAX); // plusDays(1) para que sea inclusivo
        }

        if (fechaInicial != null && fechaFinal != null) {
            if ((fechaInicial.getYear() > fechaFinal.getYear()) ||
                    (fechaInicial.getYear() == fechaFinal.getYear() && (fechaInicial.getMonthValue() > fechaFinal.getMonthValue())) ||
                    (fechaInicial.getYear() == fechaFinal.getYear() && (fechaInicial.getMonthValue() == fechaFinal.getMonthValue()) && (fechaInicial.getDayOfMonth() > fechaFinal.getDayOfMonth()))){
                throw new Exception("Rango de fechas invalido");
            }
        }

        // Diferencia entre mes de fecha inicio y mes de fecha final
       //  int cantidadMeses = 12;
       //  ver si funciona cantidadMeses
        List<Pedido> listaPedidos;
        if (pago != null && fechaInicial != null && fechaFinal != null){
            listaPedidos = pedidoService.pedidosRestauranteMedioPagoFechaHoraProcesado(restaurante,pago,fechaInicial,fechaFinal);
        } else if (pago != null){
            listaPedidos = pedidoService.pedidosRestauranteMedioPago(restaurante,pago);
        } else if (fechaInicial != null && fechaFinal != null){
            listaPedidos = pedidoService.pedidosRestauranteFechaHoraProcesado(restaurante,fechaInicial,fechaFinal);
        } else {
            listaPedidos = pedidoService.pedidosRestaurante(restaurante);
        }

        if (fechaInicial == null || fechaFinal == null){
            fechaInicial = LocalDateTime.of(LocalDateTime.now().getYear(),LocalDateTime.now().getMonthValue(),1,0,0,0);
            fechaFinal = LocalDateTime.now();
        }

        List<Pedido> aux;
        List<Pedido> pedidosCategoria = new ArrayList<>();

        if (categoria != null) {
            for (Pedido pedido : listaPedidos) {
                for (MenuCompra menuCompra : pedido.getMenusCompra()){
                    if (menuCompra.getCategoria().equals(categoria)){
                        if (!pedidosCategoria.contains(pedido)){
                            pedidosCategoria.add(pedido);
                        }
                    }
                }
            }
            listaPedidos = pedidosCategoria;
        }

        int mesfinal;
        if (fechaFinal.getYear() - fechaInicial.getYear() > 0){
            mesfinal = fechaFinal.getMonthValue() + ((fechaFinal.getYear() - fechaInicial.getYear()) * 12);
        } else {
            mesfinal = fechaFinal.getMonthValue();
        }

        int anio = fechaInicial.getYear();

        for(int i =fechaInicial.getMonthValue(); i <= mesfinal; i++){

            float subTotal = 0;
            aux = listaPedidos;
            float ventasEfectivo = 0;
            int cantidadVentasEfectivo = 0;
            float ventasPaypal = 0;
            int cantidadVentasPayPal = 0;
            float devolucionesEfectivo = 0;
            int cantidadDevolucionesEfectivo = 0;
            float devolucionesPayPal = 0;
            int cantidadDevolucionesPayPal = 0;

            mes = obtenerMes(i % 12);

            for (Pedido pedido : aux){
                LocalDateTime fechapedido = pedido.getFechaHoraProcesado();
                if (fechapedido == null) {
                    throw new PedidoFechaProcesadoException("Existe un pedido sin fecha de procesado en estado Finalizado-Devuelto-ReclamoRechazado");
                }
                if (pedido.getEstado().equals(EstadoPedido.FINALIZADO) || (pedido.getEstado().equals(EstadoPedido.RECLAMORECHAZADO))) {
                    if ((fechapedido.isEqual(fechaInicial) || fechapedido.isAfter(fechaInicial)) && (fechapedido.getMonthValue() % 12) == (i % 12) &&
                            ( fechapedido.isBefore(fechaFinal) || fechapedido.isEqual(fechaFinal)) && (fechapedido.getYear() == anio)) {
                        if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)) {
                            ventasEfectivo += pedido.getTotal();
                            cantidadVentasEfectivo++;
                            subTotal += pedido.getTotal();
                        } else if (pedido.getMedioPago().equals(MedioPago.PAYPAL)) {
                            ventasPaypal += pedido.getTotal();
                            cantidadVentasPayPal++;
                            subTotal += pedido.getTotal();
                        }
                        //anio = pedido.getFechaHoraProcesado().getYear();
                    }
                } else if (pedido.getEstado().equals(EstadoPedido.DEVUELTO)){
                    if ((fechapedido.isEqual(fechaInicial) || fechapedido.isAfter(fechaInicial)) && (fechapedido.getMonthValue() % 12) == (i % 12) &&
                            ( fechapedido.isBefore(fechaFinal) || fechapedido.isEqual(fechaFinal)) && (fechapedido.getYear() == anio)) {
                        if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)) {
                            devolucionesEfectivo += pedido.getTotal();
                            cantidadDevolucionesEfectivo++;
                            subTotal -= pedido.getTotal();
                        } else if (pedido.getMedioPago().equals(MedioPago.PAYPAL)) {
                            devolucionesPayPal += pedido.getTotal();
                            cantidadDevolucionesPayPal++;
                            subTotal -= pedido.getTotal();
                        }
                    }
                }
            }

            totalVentasEfectivo += ventasEfectivo;
            totalCantidadVentasEfectivo += cantidadVentasEfectivo;
            totalVentasPaypal += ventasPaypal;
            totalCantidadVentasPayPal += cantidadVentasPayPal;
            totalDevolucionesEfectivo += devolucionesEfectivo;
            totalCantidadDevolucionesEfectivo += cantidadDevolucionesEfectivo;
            totalDevolucionesPayPal += devolucionesPayPal;
            totalCantidadDevolucionesPayPal += cantidadDevolucionesPayPal;

            JsonObject jsonMes = new JsonObject();
            jsonMes.addProperty("mes", mes);
            jsonMes.addProperty("anio", anio);
            JsonArray indicadores = new JsonArray();

            // Cargar los 4 json de indicadores
            JsonObject jsonIndicadorVE = new JsonObject();
            jsonIndicadorVE.addProperty("ventasEfectivo", ventasEfectivo);
            jsonIndicadorVE.addProperty("cantidad", cantidadVentasEfectivo);

            JsonObject jsonIndicadorVPP = new JsonObject();
            jsonIndicadorVPP.addProperty("ventasPaypal", ventasPaypal);
            jsonIndicadorVPP.addProperty("cantidad", cantidadVentasPayPal);

            JsonObject jsonIndicadorDE = new JsonObject();
            jsonIndicadorDE.addProperty("devolucionesEfectivo", devolucionesEfectivo);
            jsonIndicadorDE.addProperty("cantidad", cantidadDevolucionesEfectivo);

            JsonObject jsonIndicadorDPP = new JsonObject();
            jsonIndicadorDPP.addProperty("devolucionesPaypal", devolucionesPayPal);
            jsonIndicadorDPP.addProperty("cantidad", cantidadDevolucionesPayPal);

            indicadores.add(jsonIndicadorVE);
            indicadores.add(jsonIndicadorVPP);
            indicadores.add(jsonIndicadorDE);
            indicadores.add(jsonIndicadorDPP);
            jsonMes.add("indicadores", indicadores);
            jsonMes.addProperty("subtotal", subTotal);

            // Termina iteracion de todo lo relacionado a un mes
            meses.add(jsonMes);

            if ((i % 12) == 0) {
                anio ++;
            }
        }

        // Cargo totales

        JsonObject ventasEfectivoTotal = new JsonObject();
        ventasEfectivoTotal.addProperty("ventasEfectivo", totalVentasEfectivo);
        ventasEfectivoTotal.addProperty("cantidad", totalCantidadVentasEfectivo);

        JsonObject ventasPayPalTotal = new JsonObject();
        ventasPayPalTotal.addProperty("ventasPayPal", totalVentasPaypal);
        ventasPayPalTotal.addProperty("cantidad", totalCantidadVentasPayPal);

        JsonObject devolucionesEfectivoTotal = new JsonObject();
        devolucionesEfectivoTotal.addProperty("devolucionesEfectivo", totalDevolucionesEfectivo);
        devolucionesEfectivoTotal.addProperty("cantidad", totalCantidadDevolucionesEfectivo);

        JsonObject devolucionesPayPalTotal = new JsonObject();
        devolucionesPayPalTotal.addProperty("devolucionesPayPal", totalDevolucionesPayPal);
        devolucionesPayPalTotal.addProperty("cantidad", totalCantidadDevolucionesPayPal);

        JsonObject total = new JsonObject(); // Para agregar los totales finales al front
        total.addProperty("total", totalVentasEfectivo + totalVentasPaypal - totalDevolucionesEfectivo - totalDevolucionesPayPal);
        total.addProperty("cantidadVentas", totalCantidadVentasEfectivo + totalCantidadVentasPayPal);
        total.addProperty("cantidadDevoluciones", totalCantidadDevolucionesEfectivo + totalCantidadDevolucionesPayPal);

        // Termina todo, retorno balance
        totales.add(ventasEfectivoTotal);
        totales.add(ventasPayPalTotal);
        totales.add(devolucionesEfectivoTotal);
        totales.add(devolucionesPayPalTotal);
        totales.add(total); // Para agregar los totales finales al front
        balance.add("meses", meses);
        balance.add("totales", totales);
        return balance;
    }

    public JsonObject realizarDevolucion(String correoRestaurante, String idPedido, String motivoDevolucion,
            Boolean estadoDevolucion)
            throws PedidoNoExisteException, PedidoIdException, RestauranteNoEncontradoException,
            EmailNoEnviadoException, IOException, PedidoDevolucionException, PedidoDistintoRestauranteException {
        Restaurante restaurante = obtenerRestaurante(correoRestaurante);
        JsonObject response = new JsonObject();
        response.addProperty("Mensaje", "Devolucion");
        if (!idPedido.matches("[0-9]*") || idPedido.isBlank()) {
            throw new PedidoIdException("El id del pedido no es un numero entero");
        }
        Pedido pedido = pedidoService.obtenerPedido(Long.valueOf(idPedido));
        if (!pedido.getEstado().equals(EstadoPedido.FINALIZADO) && !pedido.getEstado().equals(EstadoPedido.RECHAZADO)) {
            throw new PedidoDevolucionException("El pedido no esta FINALIZADO, no se puede aplicar una devolucion");
        }
        if (!pedido.getRestaurante().getCorreo().equals(restaurante.getCorreo())) {
            throw new PedidoDistintoRestauranteException(
                    "El pedido id " + idPedido + " no pertenece al restaurante " + correoRestaurante);
        }
        Context context = new Context();
        context.setVariable("user", pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido());
        String htmlContent = "";
        if (estadoDevolucion) {
            String asunto = "Reclamo aceptado: " + pedido.getReclamo().getRazon();
            if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)) {
                pedidoService.cambiarEstadoPedido(pedido.getId(), EstadoPedido.DEVUELTO);
                response.addProperty("status", "completado");
                context.setVariable("contenido",
                        "Su reclamo al restaurante " + restaurante.getNombreRestaurante() + " ha sido aceptado.");
                context.setVariable("pedido", "Identificador pedido: #" + pedido.getId());
                context.setVariable("monto", "Monto: " + pedido.getTotal().toString());
                context.setVariable("mensaje",
                        "Para la devolución deberá contactar con el restaurante por alguno de los siguientes medios:");
                context.setVariable("direccion",
                        "Direccion: " + restaurante.getDireccion().getCalle() + " "
                                + restaurante.getDireccion().getNumero() + ", esquina "
                                + restaurante.getDireccion().getEsquina());
                context.setVariable("telefono", "Telefono: " + restaurante.getTelefono().toString());
                context.setVariable("email", "Email: " + restaurante.getCorreo());
                htmlContent = templateEngine.process("reclamo-aceptado-efectivo", context);
            } else if (pedido.getMedioPago().equals(MedioPago.PAYPAL)) {
                response.addProperty("status",
                        payPalService.refundOrder(payPalService.getOrder(pedido.getOrdenPaypal().getOrdenId())));
                if (motivoDevolucion.equals("rechazado")){
                    asunto = "Devolucion por rechazo de pedido";
                    context.setVariable("contenido",
                            "Su pedido al restaurante " + restaurante.getNombreRestaurante() + " ha sido rechazado.");
                } else {
                    context.setVariable("contenido",
                            "Su reclamo al restaurante " + restaurante.getNombreRestaurante() + " ha sido aceptado.");
                }
                context.setVariable("pedido", "Identificador pedido: #" + pedido.getId());
                context.setVariable("paypal", "Identificador Paypal: order#" + pedido.getOrdenPaypal().getOrdenId());
                context.setVariable("mensaje",
                        "Te hemos realizado una devolucion de $" + pedido.getTotal() + " en tu cuenta de Paypal.");
                htmlContent = templateEngine.process("reclamo-aceptado-paypal", context);
            }
            emailService.enviarMail(pedido.getCliente().getCorreo(), asunto, htmlContent, null);
            pedidoService.cambiarEstadoPedido(pedido.getId(), EstadoPedido.DEVUELTO);
        } else {
            if (pedido.getMedioPago().equals(MedioPago.PAYPAL)) {
                context.setVariable("contenido",
                        "Su reclamo al restaurante " + restaurante.getNombreRestaurante() + ", ha sido rechazado.");
                context.setVariable("pedido", "Identificador pedido: #" + pedido.getId());
                context.setVariable("paypal", "Identificador Paypal order#" + pedido.getOrdenPaypal().getOrdenId());
                context.setVariable("motivo", motivoDevolucion);
                htmlContent = templateEngine.process("reclamo-rechazado-paypal", context);
            } else if (pedido.getMedioPago().equals(MedioPago.EFECTIVO)) {
                context.setVariable("contenido",
                        "Su reclamo al restaurante " + restaurante.getNombreRestaurante() + ", ha sido rechazado.");
                context.setVariable("pedido", "Identificador pedido: #" + pedido.getId());
                context.setVariable("motivo", motivoDevolucion);
                htmlContent = templateEngine.process("reclamo-rechazado-efectivo", context);
            }
            emailService.enviarMail(pedido.getCliente().getCorreo(),
                    "Reclamo rechazo: " + pedido.getReclamo().getRazon(), htmlContent, null);
            response.addProperty("status", "Mail de rechazo enviado");
            pedidoService.cambiarEstadoPedido(pedido.getId(), EstadoPedido.RECLAMORECHAZADO);
        }
        return response;
    }

    public JsonObject pedidosRegistrados(int anio){
        JsonObject result = new JsonObject();
        List<Restaurante> restaurantes = restauranteRepository.findAllByRolesOrderByCalificacion("ROLE_RESTAURANTE");
        JsonArray mes = new JsonArray();
        for (int i=1; i <= 12 ; i++) {
            LocalDateTime fechaIni = LocalDateTime.of(LocalDate.of(anio, i, 1),LocalTime.MIDNIGHT);
            LocalDateTime fechaFin;
            if (i == 12) {
                fechaFin = LocalDateTime.of(LocalDate.of(anio + 1, 1, 1).minusDays(1),LocalTime.MAX);
            } else {
                fechaFin = LocalDateTime.of(LocalDate.of(anio, i + 1, 1).minusDays(1),LocalTime.MAX);
            }
            int cantidadRegistrados = 0;
            for (Restaurante restaurante : restaurantes){
                cantidadRegistrados += pedidoService.cantPedidosRestaurante(restaurante,fechaIni,fechaFin);
            }
            JsonObject pedidosRestaurante = new JsonObject();
            pedidosRestaurante.addProperty("mes", obtenerMes(i));
            pedidosRestaurante.addProperty("cantidad",cantidadRegistrados);
            mes.add(pedidosRestaurante);
        }
        result.add("pedidosRegistrados",mes);
        return result;
    }

    public String obtenerMes (int mes){
        String result;
        switch (mes) {
            case 1:
                result = "Enero";
                break;
            case 2:
                result = "Febrero";
                break;
            case 3:
                result = "Marzo";
                break;
            case 4:
                result = "Abril";
                break;
            case 5:
                result = "Mayo";
                break;
            case 6:
                result = "Junio";
                break;
            case 7:
                result = "Julio";
                break;
            case 8:
                result = "Agosto";
                break;
            case 9:
                result = "Septiembre";
                break;
            case 10:
                result = "Octubre";
                break;
            case 11:
                result = "Noviembre";
                break;
            default:
                result = "Diciembre";
                break;
        }
        return result;
    }

    public Long restaurantesActivos(){
        return restauranteRepository.countRestaurantesByEstado(EstadoRestaurante.ABIERTO) +
                restauranteRepository.countRestaurantesByEstado(EstadoRestaurante.CERRADO);
    }

    public JsonObject ventasRestaurantes(String correo, int anio) throws RestauranteNoEncontradoException {
        Restaurante restaurante = obtenerRestaurante(correo);
        JsonObject ventasRestaurante = new JsonObject();
        JsonArray meses = new JsonArray();
        for (int i=1; i<=12; i++){
            LocalDateTime fechaIni = LocalDateTime.of(LocalDate.of(anio, i, 1),LocalTime.MIDNIGHT);
            LocalDateTime fechaFin;
            if (i == 12) {
                fechaFin = LocalDateTime.of(LocalDate.of(anio + 1, 1, 1).minusDays(1),LocalTime.MAX);
            } else {
                fechaFin = LocalDateTime.of(LocalDate.of(anio, i + 1, 1).minusDays(1),LocalTime.MAX);
            }
            JsonObject mes = new JsonObject();
            Long cantidad = pedidoService.cantVentasRestauranteAnio(restaurante,fechaIni,fechaFin);
            mes.addProperty("mes",obtenerMes(i));
            mes.addProperty("cantidad", cantidad);
            meses.add(mes);
        }
        ventasRestaurante.addProperty("restaurante",restaurante.getNombreRestaurante());
        ventasRestaurante.addProperty("anio",anio);
        ventasRestaurante.add("ventas", meses);
        return ventasRestaurante;
    }

}


package org.foodmonks.backend.Restaurante;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.foodmonks.backend.Cliente.Cliente;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.Direccion.DireccionConverter;
import org.foodmonks.backend.EmailService.EmailNoEnviadoException;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Pedido.Exceptions.*;
import org.foodmonks.backend.Pedido.Pedido;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Reclamo.Reclamo;
import org.foodmonks.backend.Reclamo.ReclamoConverter;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.foodmonks.backend.datatypes.*;
import org.foodmonks.backend.paypal.PayPalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestauranteServiceTest {
    @InjectMocks
    RestauranteService restauranteService;

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    RestauranteRepository restauranteRepository;
    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    MenuService menuService;
    @Mock
    RestauranteConverter restauranteConverter;
    @Mock
    PedidoService pedidoService;
    @Spy
    ReclamoConverter reclamoConverter;
    @Mock
    TemplateEngine templateEngine;
    @Mock
    EmailService emailService;
    @Mock
    PayPalService payPalService;

    @Mock
    DireccionConverter direccionConverter;

    @BeforeEach
    void setUp() {
        restauranteConverter = new RestauranteConverter(direccionConverter);
        restauranteService = new RestauranteService(restauranteRepository, passwordEncoder,
                usuarioRepository, menuService, restauranteConverter,
                pedidoService, reclamoConverter, templateEngine,
                emailService, payPalService);
    }

    @Test // COVERAGE (it's useless!);
    void listarHistoricoPedidos() throws RestauranteNoEncontradoException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante1);
        restauranteService.listarHistoricoPedidos("","","","","","","","");
        restauranteService.listarHistoricoPedidos("a","","","","","","","");
        restauranteService.listarHistoricoPedidos("","FINALIZADO","","","","","","");
        restauranteService.listarHistoricoPedidos("","asdasd","","","","","","");
        restauranteService.listarHistoricoPedidos("","","PAYPAL","","","","","");
        restauranteService.listarHistoricoPedidos("","","asdasd","","","","","");
        restauranteService.listarHistoricoPedidos("","","","","2020-01-01,2021-01-01","","","");
        restauranteService.listarHistoricoPedidos("","","","","2020-01-01T18:05:05,2021-01-01T19:05:05","","","");
        restauranteService.listarHistoricoPedidos("","","","","","1,2","","");
        restauranteService.listarHistoricoPedidos("","","","","","1a,b2","","");
        restauranteService.listarHistoricoPedidos("","","","","","","0","");
        restauranteService.listarHistoricoPedidos("","","","","","","O","");
        restauranteService.listarHistoricoPedidos("","","","","","","","10");
        restauranteService.listarHistoricoPedidos("","","","","","","","1O");
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(null);
        assertThatThrownBy(()->restauranteService.listarHistoricoPedidos("dummy","","",
                "","","","",""))
                .isInstanceOf(RestauranteNoEncontradoException.class)
                .hasMessageContaining("No existe el Restaurante dummy");

//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("currentPage", 0);
//        jsonObject.addProperty("totalItems", 5);
//        jsonObject.addProperty("totalPages", 1);
//        jsonObject.addProperty("pedidos", );
//        when(pedidoService.listaPedidosHistorico(restaurante1, EstadoPedido.FINALIZADO,
//                MedioPago.PAYPAL, "", new LocalDateTime[2], new Float[2], 0, 5)).thenReturn(null);
    }

    @Test
    void registrarPagoEfectivo() throws RestauranteNoEncontradoException, PedidoNoExisteException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante1);
        when(pedidoService.existePedido(anyLong())).thenReturn(true);
        when(pedidoService.existePedidoRestaurante(anyLong(), any(Restaurante.class))).thenReturn(true);
        restauranteService.registrarPagoEfectivo("dummy",1L);
        verify(pedidoService).cambiarEstadoPedido(anyLong(), any(EstadoPedido.class));
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(null);
        assertThatThrownBy(()->restauranteService.registrarPagoEfectivo("dummy", 1L))
                .isInstanceOf(RestauranteNoEncontradoException.class)
                .hasMessageContaining("No existe el Restaurante dummy");
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante1);
        when(pedidoService.existePedido(anyLong())).thenReturn(false);
        assertThatThrownBy(()->restauranteService.registrarPagoEfectivo("dummy", 1L))
                .isInstanceOf(PedidoNoExisteException.class)
                .hasMessageContaining("No existe el pedido con id 1");
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante1);
        when(pedidoService.existePedido(anyLong())).thenReturn(true);
        when(pedidoService.existePedidoRestaurante(anyLong(), any(Restaurante.class))).thenReturn(false);
        assertThatThrownBy(()->restauranteService.registrarPagoEfectivo("dummy", 1L))
                .isInstanceOf(RestauranteNoEncontradoException.class)
                .hasMessageContaining("No existe el pedido con id 1 para el Restaurante dummy");
    }

    @Test
    void listaRestaurantesAbiertos(){
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.5F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante3 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante3@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                5.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.BLOQUEADO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        when(restauranteRepository.findRestaurantesByEstadoOrderByCalificacionDesc(any(EstadoRestaurante.class))).thenReturn(
                        List.of(restaurante2, restaurante1));
        List<JsonObject> expectedRestaurantes = restauranteConverter.listaRestaurantes(List.of(restaurante2, restaurante1));
        List<JsonObject> result = restauranteService.listaRestaurantesAbiertos("", "", true);
        assertThat(result).isEqualTo(expectedRestaurantes);
        // -------------------------------------------------------
        when(restauranteRepository.findRestaurantesByNombreRestauranteContainsAndEstadoOrderByCalificacionDesc(anyString(), any(EstadoRestaurante.class))).thenReturn(
                List.of(restaurante2, restaurante1));
        expectedRestaurantes = restauranteConverter.listaRestaurantes(List.of(restaurante2, restaurante1));
        result = restauranteService.listaRestaurantesAbiertos("nombreDelRestaurante", "", true);
        assertThat(result).isEqualTo(expectedRestaurantes);
        // -------------------------------------------------------
        when(menuService.existeCategoriaMenu(any(Restaurante.class), any(CategoriaMenu.class))).thenReturn(true);
        expectedRestaurantes = restauranteConverter.listaRestaurantes(List.of(restaurante2, restaurante1));
        result = restauranteService.listaRestaurantesAbiertos("", "OTROS", true);
        assertThat(result).isEqualTo(expectedRestaurantes);
        // -------------------------------------------------------
        when(restauranteRepository.findRestaurantesByEstado(any(EstadoRestaurante.class))).thenReturn(
                List.of(restaurante1, restaurante2));
        expectedRestaurantes = restauranteConverter.listaRestaurantes(List.of(restaurante1, restaurante2));
        result = restauranteService.listaRestaurantesAbiertos("", "", false);
        assertThat(result).isEqualTo(expectedRestaurantes);
        // -------------------------------------------------------
        when(restauranteRepository.findRestaurantesByNombreRestauranteContainsAndEstado(anyString(), any(EstadoRestaurante.class))).thenReturn(
                List.of(restaurante1, restaurante2));
        expectedRestaurantes = restauranteConverter.listaRestaurantes(List.of(restaurante1, restaurante2));
        result = restauranteService.listaRestaurantesAbiertos("nombreDelRestaurante", "", false);
        assertThat(result).isEqualTo(expectedRestaurantes);
        // -------------------------------------------------------
        when(menuService.existeCategoriaMenu(any(Restaurante.class), any(CategoriaMenu.class))).thenReturn(true);
        expectedRestaurantes = restauranteConverter.listaRestaurantes(List.of(restaurante1, restaurante2));
        result = restauranteService.listaRestaurantesAbiertos("", "OTROS", false);
        assertThat(result).isEqualTo(expectedRestaurantes);
    }

    @Test
    void actualizarEstadoPedido() throws PedidoNoExisteException, RestauranteNoEncontradoException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.0F, 10, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Pedido pedido1 = new Pedido(EstadoPedido.PENDIENTE, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);

        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante1);
        when(pedidoService.existePedidoRestaurante(anyLong(), any(Restaurante.class))).thenReturn(true);
        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);
        restauranteService.actualizarEstadoPedido("dummy", 1L,"CONFIRMADO",30);
        verify(pedidoService).cambiarEstadoPedido(1L, EstadoPedido.CONFIRMADO);
        // -------------------------------------------------------
        restauranteService.actualizarEstadoPedido("dummy", 1L,"RECHAZADO",30);
        //verify(pedidoService).cambiarEstadoPedido(1L, EstadoPedido.RECHAZADO);
        // -------------------------------------------------------
        pedido1.setMedioPago(MedioPago.PAYPAL);
        restauranteService.actualizarEstadoPedido("dummy", 1L,"CONFIRMADO",30);
        verify(pedidoService).cambiarEstadoPedido(1L, EstadoPedido.FINALIZADO);
        // -------------------------------------------------------
        restauranteService.actualizarEstadoPedido("dummy", 1L,"RECHAZADO",30);
        verify(pedidoService, times(2)).cambiarEstadoPedido(1L, EstadoPedido.RECHAZADO);
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(null);
        assertThatThrownBy(()->restauranteService.actualizarEstadoPedido("dummy", 1L, "dummy", 0))
                .isInstanceOf(RestauranteNoEncontradoException.class)
                .hasMessageContaining("No existe el Restaurante dummy");
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante1);
        when(pedidoService.existePedidoRestaurante(anyLong(), any(Restaurante.class))).thenReturn(false);
        assertThatThrownBy(()->restauranteService.actualizarEstadoPedido("dummy", 1L, "dummy", 0))
                .isInstanceOf(RestauranteNoEncontradoException.class)
                .hasMessageContaining("No existe el pedido con id 1 para el Restaurante dummy");
    }

    @Test
    void calificarRestaurante() throws PedidoNoExisteException, PedidoIdException, PedidoClienteException, PedidoEstadoException, PedidoPuntajeException, PedidoCalificacionRestauranteException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                3.0F, 1, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);
        JsonObject calificacion = new JsonObject();
        calificacion.addProperty("puntaje", "4");
        calificacion.addProperty("comentario", "dummy");
        calificacion.addProperty("idPedido", "1");

        restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion);

        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);
        verify(restauranteRepository).save(restauranteArgumentCaptor.capture());
        assertThat(restauranteArgumentCaptor.getValue().getCalificacion()).isEqualTo(3.5f);
        // -------------------------------------------------------
        assertThatThrownBy(()->restauranteService.calificarRestaurante("fakeEmail", calificacion))
                .isInstanceOf(PedidoClienteException.class)
                .hasMessageContaining("El cliente fakeEmail no es cliente del pedido id 1");
        // -------------------------------------------------------
        pedido1.setEstado(EstadoPedido.CONFIRMADO);
        assertThatThrownBy(()->restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoEstadoException.class)
                .hasMessageContaining("El pedido id 1no esta en EstadoPedido para calificar");
        // -------------------------------------------------------
        pedido1.setEstado(EstadoPedido.FINALIZADO);
        pedido1.setCalificacionRestaurante(new DtCalificacion(5.0f,"dummy"));
        assertThatThrownBy(()->restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoCalificacionRestauranteException.class)
                .hasMessageContaining("El pedido con id 1 ya tiene calificacion Restaurante");
        // -------------------------------------------------------
        // ------------------------ verificarJsonCalificacion -------------------------------
        // -------------------------------------------------------
        pedido1.setCalificacionRestaurante(null);
        calificacion.addProperty("puntaje", "6");
        assertThatThrownBy(()->restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoPuntajeException.class)
                .hasMessageContaining("El puntaje del pedido no es un numero entero o no es un valor posible");
        calificacion.addProperty("puntaje", "5.1");
        assertThatThrownBy(()->restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoPuntajeException.class)
                .hasMessageContaining("El puntaje del pedido no es un numero entero o no es un valor posible");
        calificacion.addProperty("puntaje", "a");
        assertThatThrownBy(()->restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoPuntajeException.class)
                .hasMessageContaining("El puntaje del pedido no es un numero entero o no es un valor posible");
        // -------------------------------------------------------
        calificacion.addProperty("idPedido", "1.");
        assertThatThrownBy(()->restauranteService.calificarRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoIdException.class)
                .hasMessageContaining("El id del pedido no es un numero entero");

    }

    @Test
    void modificarCalificacionRestaurante() throws PedidoNoExisteException, PedidoIdException, PedidoClienteException, PedidoPuntajeException, PedidoCalificacionRestauranteException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                3.0F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        pedido1.setCalificacionRestaurante(new DtCalificacion(5.0f,"dummy"));

        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);
        JsonObject calificacion = new JsonObject();
        calificacion.addProperty("puntaje", "4");
        calificacion.addProperty("comentario", "dummy");
        calificacion.addProperty("idPedido", "1");

        restauranteService.modificarCalificacionRestaurante("cliente1@gmail.com", calificacion);

        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);
        verify(restauranteRepository).save(restauranteArgumentCaptor.capture());
        assertThat(restauranteArgumentCaptor.getValue().getCalificacion()).isEqualTo(2.8f);
        // -------------------------------------------------------
        assertThatThrownBy(()->restauranteService.modificarCalificacionRestaurante("fakeEmail", calificacion))
                .isInstanceOf(PedidoClienteException.class)
                .hasMessageContaining("El cliente fakeEmail no es cliente del pedido id 1");
        // -------------------------------------------------------
        pedido1.setCalificacionRestaurante(null);
        assertThatThrownBy(()->restauranteService.modificarCalificacionRestaurante("cliente1@gmail.com", calificacion))
                .isInstanceOf(PedidoCalificacionRestauranteException.class)
                .hasMessageContaining("El pedido con id 1 no tiene calificacion Restaurante");
    }

    @Test
    void eliminarCalificacionRestaurante() throws PedidoNoExisteException, PedidoIdException, PedidoClienteException, PedidoCalificacionRestauranteException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.2F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        pedido1.setCalificacionRestaurante(new DtCalificacion(5.0f,"dummy"));

        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);

        restauranteService.eliminarCalificacionRestaurante("cliente1@gmail.com", "1");

        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);
        verify(restauranteRepository).save(restauranteArgumentCaptor.capture());
        assertThat(restauranteArgumentCaptor.getValue().getCalificacion()).isEqualTo(4.0f);
        assertThat(restauranteArgumentCaptor.getValue().getCantidadCalificaciones()).isEqualTo(4);
        // -------------------------------------------------------
        restaurante1.setCalificacion(3.0f);
        restaurante1.setCantidadCalificaciones(1);
        restauranteService.eliminarCalificacionRestaurante("cliente1@gmail.com", "1");
        verify(restauranteRepository, times(2)).save(restauranteArgumentCaptor.capture());
        assertThat(restauranteArgumentCaptor.getValue().getCalificacion()).isEqualTo(5.0f);
        assertThat(restauranteArgumentCaptor.getValue().getCantidadCalificaciones()).isEqualTo(0);
        // -------------------------------------------------------
        assertThatThrownBy(()->restauranteService.eliminarCalificacionRestaurante("cliente1@gmail.com", "     "))
                .isInstanceOf(PedidoIdException.class)
                .hasMessageContaining("El id del pedido no es un numero entero");
        assertThatThrownBy(()->restauranteService.eliminarCalificacionRestaurante("cliente1@gmail.com", "asd"))
                .isInstanceOf(PedidoIdException.class)
                .hasMessageContaining("El id del pedido no es un numero entero");
        // -------------------------------------------------------
        assertThatThrownBy(()->restauranteService.eliminarCalificacionRestaurante("fakeEmail", "1"))
                .isInstanceOf(PedidoClienteException.class)
                .hasMessageContaining("El cliente fakeEmail no es cliente del pedido id 1");
        // -------------------------------------------------------
        pedido1.setCalificacionRestaurante(null);
        assertThatThrownBy(()->restauranteService.eliminarCalificacionRestaurante("cliente1@gmail.com", "1"))
                .isInstanceOf(PedidoCalificacionRestauranteException.class)
                .hasMessageContaining("El pedido con id 1 no tiene calificacion Restaurante");
    }

    @Test
    void listarReclamos() throws RestauranteNoEncontradoException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.2F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        Reclamo reclamo1 = new Reclamo("razon1","comentario1",LocalDateTime.of(2020,1,1,0,0,0), pedido1);
        pedido1.setReclamo(reclamo1);
        Pedido pedido2 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido2.setCliente(cliente1);
        pedido2.setRestaurante(restaurante1);
        Reclamo reclamo2 = new Reclamo("razon2","comentario2",LocalDateTime.of(2020,2,1,0,0,0), pedido2);
        pedido2.setReclamo(reclamo2);
        cliente1.setPedidos(List.of(pedido1, pedido2));
        restaurante1.setPedidos(List.of(pedido1, pedido2));

        ArrayList<Reclamo> reclamoArrayList = new ArrayList<>();
        reclamoArrayList.addAll(List.of(reclamo1, reclamo2));
        restaurante1.setReclamos(reclamoArrayList);


        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante1);
        JsonArray expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of(reclamo1, reclamo2));

        JsonArray result = restauranteService.listarReclamos("dummy", false, "", "");

        assertThat(result).isEqualTo(expectedReclamos);
        // -------------------------------------------------------
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of(reclamo2, reclamo1));
        result = restauranteService.listarReclamos("dummy", true, "", "");
        assertThat(result).isEqualTo(expectedReclamos);
        // -------------------------------------------------------
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of());
        result = restauranteService.listarReclamos("dummy", false, "fail", "razon1");
        assertThat(result).isEqualTo(expectedReclamos);
        result = restauranteService.listarReclamos("dummy", false, "cliente", "fail");
        assertThat(result).isEqualTo(expectedReclamos);
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of(reclamo1));
        result = restauranteService.listarReclamos("dummy", false, "cliente", "razon1");
        assertThat(result).isEqualTo(expectedReclamos);
        // -------------------------------------------------------
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of(reclamo2, reclamo1));
        result = restauranteService.listarReclamos("dummy", false, "cliente1@gmail.com", "");
        assertThat(result).isEqualTo(expectedReclamos);
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of());
        result = restauranteService.listarReclamos("dummy", false, "fail", "");
        assertThat(result).isEqualTo(expectedReclamos);
        // -------------------------------------------------------
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of(reclamo2));
        result = restauranteService.listarReclamos("dummy", false, "", "razon2");
        assertThat(result).isEqualTo(expectedReclamos);
        expectedReclamos = reclamoConverter.arrayJsonReclamo(List.of());
        result = restauranteService.listarReclamos("dummy", false, "", "fail");
        assertThat(result).isEqualTo(expectedReclamos);

    }

    @Test
    void realizarDevolucion() throws PedidoNoExisteException, PedidoIdException, PedidoDevolucionException, PedidoDistintoRestauranteException, IOException, RestauranteNoEncontradoException, EmailNoEnviadoException {
        Direccion dir1 = new Direccion(1234, "calle", "esquina", "detalles", "latitud", "longitud");
        Restaurante restaurante1 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante1@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.2F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Restaurante restaurante2 = new Restaurante("nombreDelRestaurante",
                "apellidoDelRestaurante", "restaurante2@gmail.com",
                passwordEncoder.encode("a"), LocalDate.of(2020, 01, 01),
                4.2F, 5, "NombreRestaurante", 123456L,
                dir1, EstadoRestaurante.ABIERTO, 23487123,
                "DescripcionRestaurante", "CuentaDePaypal", null);
        Cliente cliente1 =  new Cliente("nombreDelCliente",
                "apellidoDelCliente",
                "cliente1@gmail.com", passwordEncoder.encode("a"),
                LocalDate.of(2020, 01, 01), 4.0f,10,
                List.of(dir1), EstadoCliente.ACTIVO, null, null);
        Pedido pedido1 = new Pedido(EstadoPedido.FINALIZADO, LocalDateTime.of(2020,1,1,0,0,0),
                500.0F,MedioPago.EFECTIVO, LocalDateTime.of(2020,1,1,0,0,0),dir1,null);
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(restaurante1);
        cliente1.setPedidos(List.of(pedido1));
        restaurante1.setPedidos(List.of(pedido1));
        Reclamo reclamo1 = new Reclamo("razon1","comentario1",LocalDateTime.of(2020,1,1,0,0,0), pedido1);
        pedido1.setReclamo(reclamo1);
        restaurante1.setReclamos(List.of(reclamo1));

        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante1);
        when(pedidoService.obtenerPedido(anyLong())).thenReturn(pedido1);
        when(payPalService.refundOrder(anyString())).thenReturn("dummy");
        when(payPalService.getOrder(anyString())).thenReturn("dummy");
        JsonObject expectedResponse = new JsonObject();
        expectedResponse.addProperty("Mensaje","Devolucion");
        expectedResponse.addProperty("status", "Mail de rechazo enviado");

        JsonObject result = restauranteService.realizarDevolucion("dummy", "1","dummy",false);

        assertThat(result).isEqualTo(expectedResponse);
        // -------------------------------------------------------
        pedido1.setMedioPago(MedioPago.PAYPAL);
        pedido1.setOrdenPaypal(new DtOrdenPaypal("","",""));
        result = restauranteService.realizarDevolucion("dummy", "1","dummy",false);

        assertThat(result).isEqualTo(expectedResponse);
        // -------------------------------------------------------
        expectedResponse.addProperty("status", "dummy");
        result = restauranteService.realizarDevolucion("dummy", "1","dummy",true);

        assertThat(result).isEqualTo(expectedResponse);
        // -------------------------------------------------------
        pedido1.setMedioPago(MedioPago.EFECTIVO);
        expectedResponse.addProperty("status", "completado");
        result = restauranteService.realizarDevolucion("dummy", "1","dummy",true);

        assertThat(result).isEqualTo(expectedResponse);
        // -------------------------------------------------------
        assertThatThrownBy(()->restauranteService.realizarDevolucion("dummy", "1.0","dummy",false))
                .isInstanceOf(PedidoIdException.class)
                .hasMessageContaining("El id del pedido no es un numero entero");
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante2);
        assertThatThrownBy(()->restauranteService.realizarDevolucion("restaurante2@gmail.com", "1","dummy",false))
                .isInstanceOf(PedidoDistintoRestauranteException.class)
                .hasMessageContaining("El pedido id 1 no pertenece al restaurante restaurante2@gmail.com");
        // -------------------------------------------------------
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante1);
        pedido1.setEstado(EstadoPedido.DEVUELTO);
        assertThatThrownBy(()->restauranteService.realizarDevolucion("dummy", "1","dummy",false))
                .isInstanceOf(PedidoDevolucionException.class)
                .hasMessageContaining("El pedido no esta FINALIZADO, no se puede aplicar una devolucion");

    }
}
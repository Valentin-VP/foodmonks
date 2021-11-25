package org.foodmonks.backend.Restaurante;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Direccion.Direccion;
import org.foodmonks.backend.EmailService.EmailService;
import org.foodmonks.backend.Menu.MenuService;
import org.foodmonks.backend.Pedido.PedidoService;
import org.foodmonks.backend.Reclamo.ReclamoConverter;
import org.foodmonks.backend.Restaurante.Exceptions.RestauranteNoEncontradoException;
import org.foodmonks.backend.Usuario.UsuarioRepository;
import org.foodmonks.backend.datatypes.EstadoPedido;
import org.foodmonks.backend.datatypes.EstadoRestaurante;
import org.foodmonks.backend.datatypes.MedioPago;
import org.foodmonks.backend.paypal.PayPalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

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
    @Mock
    ReclamoConverter reclamoConverter;
    @Mock
    TemplateEngine templateEngine;
    @Mock
    EmailService emailService;
    @Mock
    PayPalService payPalService;

    @BeforeEach
    void setUp() {
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
}
package org.foodmonks.backend.Direccion;

import com.google.gson.JsonObject;
import org.foodmonks.backend.Direccion.Exceptions.DireccionNumeroException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DireccionServiceTest {

    @InjectMocks
    DireccionService direccionService;

    @Mock
    DireccionRepository direccionRepository;

    @BeforeEach
    void setUp() {
        direccionService = new DireccionService(direccionRepository);
    }

    @Test
    void crearDireccion() throws DireccionNumeroException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("numero", "10");
        jsonObject.addProperty("calle", "no");
        jsonObject.addProperty("esquina", "atropmi");
        jsonObject.addProperty("detalles", "dummy");
        jsonObject.addProperty("latitud", "1.0");
        jsonObject.addProperty("longitud", "1.0");
        Direccion result = direccionService.crearDireccion(jsonObject);
        assertThat(result.getNumero()).isEqualTo(10);
    }

    @Test
    void crearDireccion_NoVerify() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("numero", "a");
        assertThatThrownBy(()->direccionService.crearDireccion(jsonObject))
                .isInstanceOf(DireccionNumeroException.class)
                .hasMessageContaining("El numero de puerta debe ser un numero real");
    }

    @Test
    void modificarDireccion() {
        Direccion vieja = new Direccion(1,"calle","esquina","detalles","1.0","1.0");
        Direccion nueva = new Direccion(2,"calle","esquina","detalles","2.0","2.0");

        direccionService.modificarDireccion(vieja, nueva);

        ArgumentCaptor<Direccion> direccionArgumentCaptor = ArgumentCaptor.forClass(Direccion.class);
        verify(direccionRepository).save(direccionArgumentCaptor.capture());

        assertThat(direccionArgumentCaptor.getValue().getNumero()).isEqualTo(nueva.getNumero());
    }
}
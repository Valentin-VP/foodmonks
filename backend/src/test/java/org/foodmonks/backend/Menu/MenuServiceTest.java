package org.foodmonks.backend.Menu;

import org.foodmonks.backend.Menu.Exceptions.MenuNoEncontradoException;
import org.foodmonks.backend.Menu.Exceptions.MenuNombreExistente;
import org.foodmonks.backend.Restaurante.Restaurante;
import org.foodmonks.backend.Restaurante.RestauranteRepository;
import org.foodmonks.backend.Usuario.Exceptions.UsuarioNoRestaurante;
import org.foodmonks.backend.datatypes.CategoriaMenu;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    /* Este es el componente o Service a testear. Va con @InjectMocks para que lo que está con @Mock sea "inyectado" automáticamente...
    * ...gracias al @ExtendWith(MockitoExtension.class).
    * */
    @InjectMocks
    MenuService menuService;
    // (t-o-d-o queda marcado en IntelliJ, por eso Everything)
    /* Everything lo que tiene @Mock es llamado un "stub", es una imitación del objeto original.
     * Con Mockito no se quiere que haya conexión a BD real al testear un Service, por lo que se utiliza el 'esqueleto' del repository original.
     * Nunca se guarda o elimina nada, pero tiene todos los métodos correspondientes al repositorio. Estos no hacen nada, pero se pueden simular.
     * Sin embargo, también se pueden simular otros componentes, como el MenuConvertidor, para no depender de su implementación real.
     * */
    @Mock
    MenuRepository menuRepository;
    @Mock
    RestauranteRepository restauranteRepository;
    @Mock
    MenuConvertidor menuConvertidor;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, restauranteRepository, menuConvertidor);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void altaMenu() throws UsuarioNoRestaurante, MenuNombreExistente {
        /* Se debe crear un [Restaurante] (vacío porque no se utiliza ningún atributo del mismo en la función que está bajo test)
        * Se debe crear un [Menu] que tenga datos para poder comparar que los dos tengan el mismo contenido luego del .save
        * NOTA: Como no existe un 'equals' y un 'hashcode' en la clase [Menu], no se pueden comparar dos objetos de este tipo porque...
        * ...se estarían comparando las referencias y no los datos (atributos): la comparación daría false sí o sí...
        * ...lo que indica que crear la variable 'menu' es innecesario dada la situación remarcada de la clase [Menu].
        * Entonces se compara algún atributo del [Menu] que se presupone que se guarda al llamar a altaMenu, con lo que sabemos...
        * ...que tiene que equivaler dicho atributo luego de guardado. Acá se eligió la 'categoria', pero pudo haber sido cualquiera.
        * */
        //dado
        Restaurante restaurante = new Restaurante();
        Menu menu = new Menu("nombre", 100.0F, "descripcion", true, 1.0F,
                "imagen", CategoriaMenu.OTROS, restaurante);
        /* 'when' se utiliza para simular un comportamiento de un método que en realidad no sucede (por estar el componente anotado como @Mock)
        * En estos casos ni el repository del [Restaurante] o el [Menu] tienen ninguna comunicación con la BD. Son 'falsos'...
        * ...por lo que '.findByCorreo' o '.existsByNombreAndRestaurante' darán siempre el mismo valor (null y false respectivamente).
        * Se le dice (concepto de premisas) que cuando (when) se llama a la función '.findByCorreo' con 'anyString()' (que es como un wildcard)...
        * ...se retorne la variable 'restaurante' creada arriba. Si no se hace esto se retornaría [null] y se cortaría en el 'if (restaurante==null).
        * En el caso de '.existsByNombreAndRestaurante', se le pasan 2 'wildcards' y se simula que ese método del repositorio...
        * ...retorna que no existe el menú repetido en la BD.
        * Este es el concepto de 'stubbing'. Imitar un objeto y sus funcionalidades reales, pero sin depender de si estas funcionan bien.
        * Dato: No importan los strings o clases que se le pasen porque lo necesario es el valor de retorno, siempre y cuando coincidan con la firma.
        * */
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante);
        when(menuRepository.existsByNombreAndRestaurante(anyString(), any(Restaurante.class))).thenReturn(false);

        //cuando
        /* Acá se llama al método que está siendo probado 'altaMenu' con datos relativamente reales. Luego se preguntará por alguno de estos.*/
        menuService.altaMenu("nombre", 100.0F, "descripcion", true, 1.0F,
                "imagen", CategoriaMenu.OTROS, "correoRestaurante");
        //entonces
        /* Si se quiere comparar los atributos del objeto que llega al método .save con lo que se le envió al método...
        * ...(o si fuese el caso el objeto entero), se necesita utilizar un 'ArgumentCaptor' del tipo correcto, que es [Menu]...
        * ...dado que ese es el tipo de objeto que recibe el '.save' del 'menuRepository':
        * */
        ArgumentCaptor<Menu> menuArgumentCaptor = ArgumentCaptor.forClass(Menu.class);
        /* El 'menuArgumentCaptor.capture()' sirve para inspeccionar el objeto o argumento que se le pasa al método '.save' en 'altaMenu'.
        * Sin él, no habría forma de consultar por el objeto [Menu] que estaría realmente llegándole al '.save' (y sus atributos).
        * 'verify' es para comprobar que '.save' fue ejecutado (o sea que se llegó a la línea donde está y que al menos se intentó ejecutar).
        * */
        verify(menuRepository).save(menuArgumentCaptor.capture());

        /* Acá se obtiene el objeto "capturado" por el ArgumentCaptor anterior, para luego preguntar por algún atributo...
        * ...y compararlo con lo que se le pasó al 'altaMenu' más arriba.
        * En este caso se utilizó la categoría:
        * */
        Menu capturedMenu = menuArgumentCaptor.getValue();
        assertThat(capturedMenu.getCategoria()).isEqualTo(CategoriaMenu.OTROS);
        // assertThat(capturedMenu).isEqualTo(menu);
    }

    @Test
    void altaMenu_MenuRepetido() throws UsuarioNoRestaurante, MenuNombreExistente {
        //dado
        Restaurante restaurante = new Restaurante();

        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante);

        /* Se "fuerza" o simula que un menú exista para comprobar que el resto de la función corta donde tiene que cortar.
         * Con esto, donde en 'altaTest' se pregunta por si existe ese menú (y que en la realidad se utilizaría la BD real para la consulta)...
         * ...directamente se le dictamina que existe (.thenReturn(true)).
         * Se puede hacer de las 2 formas.
         * */
        when(menuRepository.existsByNombreAndRestaurante(anyString(), any(Restaurante.class))).thenReturn(true);
        //BDDMockito.given(menuRepository.existsByNombreAndRestaurante(anyString(), any(Restaurante.class))).willReturn(true);

        //cuando
        //entonces
        /* Se le verifica que, como cortó antes ('if (menuRepository.existsByNombreAndRestaurante(nombre, restaurante))')...
        * ...gracias a la 'mentira' que se le tiró con el .when, el .save no puede ser llamado nunca.
        * Como se espera que de veras no sea llamado se le pasa un wildcard mediante any().
        *
        * Al necesitar comprobar que se lanza de veras la excepción correspondiente, se hace directamente el assert junto a la llamada...
        * ...utilizando el 'assertThatThrownBy' junto al método que debería lanzar la excepción.
        * Obviamente la excepción tiene que coincidir y si se quiere especificar que el mensaje coincida, también este último.
        * */
        assertThatThrownBy(()->menuService.altaMenu("nombre", 100.0F, "descripcion", true, 1.0F,
                "imagen", CategoriaMenu.OTROS, "correoRestaurante"))
                .isInstanceOf(MenuNombreExistente.class).hasMessageContaining("Ya existe un menu con el nombre nombre para el restaurante correoRestaurante");

        verify(menuRepository, never()).save(any());

        /* Como esa función retornaba False si el menú es repetido, se esperaba que el retorno sea igual.
        * Se puede colocar una descripción al assert para que aparezca cuando falla el mismo...
        * ...por lo que si se quiere ser más verbose se puede. Solo aparece cuando falla.
        * Esto obviamente ya no corre para altaMenu.
        * */
       // assertThat(result).as("Debería retornar false porque el menu se asume como existente").isEqualTo(false);
    }

    @Test
    void altaMenu_RestauranteInexistente() throws UsuarioNoRestaurante, MenuNombreExistente {
        //dado
        /* Acá se quiere ver que la función falla si el restaurante es null, pero obviamente no existe conexión con la BD...
        * ...entonces se le simula un retorno para que 'if (restaurante == null)' corte por lo sano.
        * */
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(null);

        //cuando
        //entonces
        /* Igual que antes, solo que con la excepción de cuando el restaurante es null */
        assertThatThrownBy(()->menuService.altaMenu("nombre", 100.0F, "descripcion", true, 1.0F,
                "imagen", CategoriaMenu.OTROS, "correoRestaurante"))
                .isInstanceOf(UsuarioNoRestaurante.class)
                .hasMessageContaining("El correo correoRestaurante no pertenece a un restaurante");
        verify(menuRepository, never()).save(any());

        /* Igual que antes */
        //assertThat(result).as("Debería retornar false porque el restaurante se asume como null").isEqualTo(false);
    }

    //@Test //Test innecesario
    //void altaMenu_throwsException() throws UsuarioNoRestaurante, MenuNombreExistente {
        /* Debido a que no hay una conexión real con la BD, aparentemente (y APARENTEMENTE), repito: APARENTEMENTE...
        * ...no se ejecutan las queries de JPA que sí lo hacen en uso normal, entonces el IllegalStateException es imposible de simular.
        * Lo que significa que excepciones por Hibernate no pueden ser invocadas forzosamente.
        * Entonces este tipo de test es forzado para que se simule una excepción que nunca sucede.
        * No es de mucha utilidad salvo para comprobar que se cubren los caminos o branches de código apropiados.
        * De cualquier forma sí que se quiere que se llegue al .save, por lo que hay que simular los retornos de los repositories igualmente...
        * ...como si se esperase un alta de menú completo y correcto, entonces el [Restaurante] es creado y los .when se utilizan.
        * */
        //dado
    //    Restaurante restaurante = new Restaurante();
    //    Menu menu = new Menu("nombre", 100.0F, "descripcion", true, 1.0F,
    //            "imagen", CategoriaMenu.OTROS, restaurante);
    //    when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante);
    //    when(menuRepository.existsByNombreAndRestaurante(anyString(), any(Restaurante.class))).thenReturn(false);
        /* Se le fuerza a que cuando se llama al .save se lanze la excepción que lanzaría en operación normal (en caso de entidad nula o repetida)
        * El comentario de arriba detalla por qué esto se hace.
        * Por lo que es irrelevante lo que se le pase al .save porque se necesita un fallo que es imposible de simular con este @Mock.
        * */
    //    doThrow(IllegalStateException.class).when(menuRepository).save(any());
        //cuando
        /* Se llama al método...*/
    //    menuService.altaMenu("nombre", 100.0F, "descripcion", true, 1.0F,
    //            "imagen", CategoriaMenu.OTROS, "correoRestaurante");
        //entonces
        /* Igual que antes...
        * ...pero hay que tener cuidado porque si hubiese un 'throws AlgunaExcepcion' en la firma del 'altaMenu'...
        * ...y un 'throw new AlgunaExcepcion' en el código, habría que comprobar que la excepción se lanzase:
        * assertThatThrownBy(()->altaMenu(...parámetros...)).isInstanceOf(TipoDeExcepcion.class).hasMessageContaining("El mensaje que lanzaría");
        * No es el caso aquí, entonces no se hace.
        * */
    //    verify(menuRepository).save(any());
        //assertThat(result).isFalse();
    //}

    @Test
    void eliminarMenu() throws MenuNoEncontradoException {
        //dado
        /* Concepto similar al de 'altaMenu', pero al [Menu] creado se le setea un Id para luego preguntar si coincide...
        * ...pero igual que antes, no es útil o necesario por la falta de 'hashCode' y 'equals' en la clase [Menu]...
        * ...entonces directamente se le pregunta por el Id.
        * */
        Restaurante restaurante = new Restaurante();
        Menu menu = new Menu("nombre", 100.0F, "descripcion", true, 1.0F,
                "imagen", CategoriaMenu.OTROS, restaurante);
        menu.setId(1L);
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante);
        when(menuRepository.findByIdAndRestaurante(anyLong(), any(Restaurante.class))).thenReturn(menu);
        //cuando
        /* Se llama al método, y el correoRestaurante en realidad es irrelevante porque no hay BD real (están los .when para simular)
        * */
        menuService.eliminarMenu(1L, "correoRestaurante");
        //entonces
        /* Igual idea que el alta, solo que con 'menuRepository.delete' en vez de '.save'
        * */
        ArgumentCaptor<Menu> menuArgumentCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).delete(menuArgumentCaptor.capture());
        // Otra forma de hacer lo mismo
        //then(menuRepository).should().delete(menuArgumentCaptor.capture());

        /* Igual que el alta, pero se pregunta por el ID porque es el único atributo que se le pasa a 'eliminarMenu'...
        *...(ok, correoRestaurante también y técnicamente el [Menu] es nuestro 'menu' del test pero 'hashCode' y 'equals'...)
        * */
        assertThat(menuArgumentCaptor.getValue().getId()).isEqualTo(1L);
    }

    @Test
    void eliminarMenu_MenuInexistente() throws MenuNoEncontradoException {
        //IGNORAR: //doThrow(IllegalStateException.class).when(menuRepository).delete(any());

        /* Mismo concepto que los tests que esperan arrojar una excepción...
        * */
        //dado
        Restaurante restaurante = new Restaurante();
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante);
        //cuando
        //entonces
        assertThatThrownBy(()->menuService.eliminarMenu(1L, "correoRestaurante"))
                .isInstanceOf(MenuNoEncontradoException.class)
                .hasMessageContaining("No se encontro el Menu con id 1 para el Restuarante correoRestaurante");
        verify(menuRepository, never()).delete(any());
    }

    @Test
    void modificarMenu() throws MenuNombreExistente, MenuNoEncontradoException, UsuarioNoRestaurante {
        //dado
        /* Concepto similar a los anteriores
        * */
        Restaurante restaurante = new Restaurante();
        Menu menu = new Menu("nombre", 100.0F, "descripcion", true, 1.0F,
                "imagen", CategoriaMenu.OTROS, restaurante);
        menu.setId(1L);
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante);
        when(menuRepository.existsByNombreAndRestaurante(anyString(), any(Restaurante.class))).thenReturn(false);
        /*Aca podría utilizar 'new Menu()' u otro Menu en vez de 'menu', para evitar que 'nombre' se modifique también en 'menu' (la variable local de este test).
        * Sucede porque en al momento de hacer el 'when...thenReturn' se le pasa la variable 'menu' (que está en este mismo test) como referencia y no como copia.
        * Eso lo hace Mockito automáticamente.
        * No es importante acá.
        */
        when(menuRepository.findByIdAndRestaurante(anyLong(), any(Restaurante.class))).thenReturn(menu);
        //cuando
        menuService.modificarMenu(1L,"MODIFICADO_nombre", 100.0F, "descripcion", true, 1.0F,
                "imagen", CategoriaMenu.OTROS, "correoRestaurante");
        //entonces
        ArgumentCaptor<Menu> menuArgumentCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(menuRepository).save(menuArgumentCaptor.capture());

        /* 'menu.getNombre()' debería retornar "nombre" y sin embargo retorna "MODIFICADO_nombre".
        * Como indicado antes, esto no tiene sentido si no fuese porque 'menu' (al momento del '.when') se pasa (se stubbea) como referencia y no como copia.
        * Por lo que lo "guardado" o capturado por el ArgumentCaptor en el 'save' es la misma variable que 'menu', y los nombres lógicamente son iguales.
        * De hecho, una comparación de referencias a memoria entre 'menu' y 'menuArgumentCaptor.getValue()' da el mismo Nº Hexadecimal.
         */
        assertThat(menuArgumentCaptor.getValue()).isEqualTo(menu);
        assertThat(menuArgumentCaptor.getValue().getNombre()).isEqualTo("MODIFICADO_nombre");
    }

    @Test
    void modificarMenu_MenuInexistente() throws MenuNombreExistente, MenuNoEncontradoException, UsuarioNoRestaurante {
        /* Concepto similar al de los anteriores testeos de inexistentes.
        * */
        //dado
        Restaurante restaurante = new Restaurante();
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante);
        when(menuRepository.findByIdAndRestaurante(anyLong(), any(Restaurante.class))).thenReturn(null);
        //cuando
        //entonces
        /* Mismo concepto que anteriormente */
        assertThatThrownBy(()->menuService.modificarMenu(1L,"MODIFICADO_nombre", 100.0F, "descripcion", true, 1.0F,
                "imagen", CategoriaMenu.OTROS, "correoRestaurante"))
                .isInstanceOf(MenuNoEncontradoException.class)
                .hasMessageContaining("No existe el Menu con id 1 para el Restaurante correoRestaurante");
        verify(menuRepository, never()).save(any());
    }

    @Test
    void modificarMenu_NombreRepetido() throws MenuNombreExistente, MenuNoEncontradoException, UsuarioNoRestaurante {
        /* Concepto similar...
        * ...pero siempre hay que saber qué comportamientos y de qué componentes hay que simular, de lo contrario Mockito dará error.
        * Si falta un 'when', no se llegará al resultado esperado (que corte porque el nombre "está repetido"), y si sobra ERROR de TEST...
        * ...por "stubbing o mocking innecesario" según Mockito.
        * Entonces se debe tener claro los retornos que se necesitan de los métodos de los repositorios o los componentes que estén "@Mockeados"...
        * ...para llegar al output necesario.
        * */
        //dado
        Restaurante restaurante = new Restaurante();
        Menu menu = new Menu("nombre", 100.0F, "descripcion", true, 1.0F,
                "imagen", CategoriaMenu.OTROS, restaurante);
        menu.setId(1L);
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante);
        when(menuRepository.findByIdAndRestaurante(anyLong(), any(Restaurante.class))).thenReturn(menu);
        when(menuRepository.existsByNombreAndRestaurante(anyString(), any(Restaurante.class))).thenReturn(true);
        //cuando
        //entonces
        /* Igual que antes */
        assertThatThrownBy(()->menuService.modificarMenu(1L,"MODIFICADO_nombre", 100.0F, "descripcion", true, 1.0F,
                "imagen", CategoriaMenu.OTROS, "correoRestaurante"))
                .isInstanceOf(MenuNombreExistente.class)
                .hasMessageContaining("Ya existe un menu con el nombre nombre para el Restaurante correoRestaurante");
        verify(menuRepository, never()).save(any());
        //assertThat(result).isFalse();
    }

    @Test
    void modificarMenu_RestauranteInexistente() throws MenuNombreExistente, MenuNoEncontradoException, UsuarioNoRestaurante {
        /* Concepto similar...
        * ...y no vale la pena crear nada, ya que para esta excepción solo se necesita llegar hasta el 'if (restaurante == null)' con un True.
        * */
        //dado
        when(restauranteRepository.findByCorreo(anyString())).thenReturn(null);
        //cuando
        //entonces
        /* Igual que antes */
        assertThatThrownBy(()->menuService.modificarMenu(1L,"MODIFICADO_nombre", 100.0F, "descripcion", true, 1.0F,
                "imagen", CategoriaMenu.OTROS, "correoRestaurante"))
                .isInstanceOf(UsuarioNoRestaurante.class)
                .hasMessageContaining("El correo correoRestaurante no pertenece a un restaurante");
        verify(menuRepository, never()).save(any());
        //assertThat(result).isFalse();
    }

    //@Test //Test innecesario
    //void modificarMenu_throwsException() throws MenuNombreExistente, MenuNoEncontradoException, UsuarioNoRestaurante {
        //dado
        /* La misma historia que antes con las excepciones... hay que llegar hasta el .save y forzar la excepción ahí */
    //    Restaurante restaurante = new Restaurante();
    //    Menu menu = new Menu("nombre", 100.0F, "descripcion", true, 1.0F,
    //            "imagen", CategoriaMenu.OTROS, restaurante);
    //    menu.setId(1L);
    //    when(restauranteRepository.findByCorreo(anyString())).thenReturn(restaurante);
    //    when(menuRepository.existsByNombreAndRestaurante(anyString(), any(Restaurante.class))).thenReturn(false);
    //    when(menuRepository.findByIdAndRestaurante(anyLong(), any(Restaurante.class))).thenReturn(new Menu());
    //    doThrow(IllegalStateException.class).when(menuRepository).save(any());
        //cuando
    //    menuService.modificarMenu(1L,"MODIFICADO_nombre", 100.0F, "descripcion", true, 1.0F,
    //            "imagen", CategoriaMenu.OTROS, "correoRestaurante");
        //entonces
        //assertThat(result).isFalse();
    //}

    @Test
    void infoMenu() {
    }

    @Test
    void listarMenu() {
    }
}
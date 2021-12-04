import axios from "axios";
import { Base64 } from "js-base64";

// Crear base de Axios "instance"
const instance = axios.create({
  baseURL: `${process.env.REACT_APP_BACKEND_URL_BASE}`,
  headers: {
    "Content-Type": "application/json",
  },
});

// Agregar Token de Acceso y Refresh Token a todos los requests de Axios
instance.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers["Authorization"] = "Bearer " + token;
      const refreshToken = getRefreshToken();
      if (refreshToken) {
        config.headers["RefreshAuthentication"] = "Bearer " + refreshToken;
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

instance.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalConfig = error.config;

    if (error.response) {
      if (error.response.data.status === 401 && !originalConfig._retry) {
        originalConfig._retry = true;
        try {
          const rs = await renovarTokens();
          const token = rs.headers.authorization;
          const refreshToken = rs.headers.refreshauthentication;
          //agregar el seteo del refreshToken
          setTokens(token, refreshToken);
          instance.defaults.headers.common["Authorization"] = getToken();
          instance.defaults.headers.common["RefreshAuthentication"] =
            getRefreshToken();
          return instance(originalConfig);
        } catch (_error) {
          if (_error.response && _error.response.data) {
            return Promise.reject(_error.response.data);
          }

          return Promise.reject(_error);
        }
      }

      if (error.response.data.status === 403 && error.response.data) {
        return Promise.reject(error.response.data);
      }
    }

    return Promise.reject(error);
  }
);

export const renovarTokens = () => {
  return instance.get("api/v1/auth/refresh");
};

//esta funcion es para cerrar sesion
export const clearState = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("react-use-cart");
  sessionStorage.clear();
  window.location.replace("/");
};

//retorna la id del menu para el modificarMenu
export const getMenuId = () => {
  return sessionStorage.getItem("menuId");
};

//retorna la id del restaurante para el listado de menus y promociones
export const getRestauranteId = () => {
  return sessionStorage.getItem("restauranteId");
};

//---------------------------------------LOGIN--------------------------------------------

export const getToken = () => {
  return localStorage.getItem("token");
};

export const getRefreshToken = () => {
  return localStorage.getItem("refreshToken");
};

const setTokens = (auth, refreshAuth) => {
  // const newAuth = auth.substring(7);
  // const newRefreshAuth = refreshAuth.substring(7);
  localStorage.setItem("token", auth);
  localStorage.setItem("refreshToken", refreshAuth);
};

export const userLogin = (authRequest) => {
  return instance.post("api/v1/auth/login", authRequest);
};

export const fetchUserData = () => {
  return instance.get("api/v1/auth/userinfo");
};

export const registrarCliente = (cliente) => {
  return instance.post("api/v1/cliente/altaCliente", cliente);
};

export const registrarRestaurante = (restaurante) => {
  return instance.post(
    "api/v1/restaurante/crearSolicitudAltaRestaurante",
    restaurante
  );
};

export const eliminarMenu = (menuId) => {
  return instance.delete(`api/v1/restaurante/eliminarMenu/${menuId}`);
};

export const modMenu = (menuInfo, id) => {
  return instance.put(`api/v1/restaurante/modificarMenu/${id}`, menuInfo);
};

export const fetchMenus = () => {
  return instance.get("api/v1/restaurante/listarMenu");
};

export const fetchPromos = () => {
  return instance.get("api/v1/restaurante/listarPromocion");
};

export const fetchMenusPromos = (datos) => {
  const restauranteId = getRestauranteId();
  const correoRestaurante = Base64.encode(restauranteId);
  return instance.get(
    `api/v1/cliente/listarProductosRestaurante?id=${correoRestaurante}&categoria=${datos.categoria}&precioInicial=${datos.precioInicial}&precioFinal=${datos.precioFinal}`
  );
};

export const getMenuInfo = () => {
  const menuId = getMenuId();
  return instance.get(`api/v1/restaurante/getInfoMenu/${menuId}`);
};

export const altaMenu = (menu) => {
  return instance.post("api/v1/restaurante/agregarMenu", menu);
};

export const cambiarEstado = (estado) => {
  return instance.put(`api/v1/restaurante/modificarEstado/${estado}`);
};

export const actualizarEstadoPedido = (estado, id) => {
  return instance.put(`api/v1/restaurante/actualizarEstadoPedido/${id}`, {
    estado: estado,
  });
};

export const actualizarEstadoPedidoPendientes = (estado, id, minutos) => {
  return instance.put(`api/v1/restaurante/actualizarEstadoPedido/${id}`, {
    estado: estado,
    minutos: minutos,
  });
};

export const altaAdmin = (datos) => {
  return instance.post("api/v1/admin/altaAdmin", datos);
};

export const eliminarCuentaClientePropia = () => {
  return instance.delete("api/v1/cliente/eliminarCuenta");
};

export const obtenerPedidosSinFinalizarEfectivo = () => {
  return instance.get("api/v1/restaurante/listarPedidosEfectivoCompletado");
};

export const obtenerPedidosSinConfirmar = () => {
  return instance.get("api/v1/restaurante/listarPedidosPendientes");
};

export const obtenerPedidosHistorico = (datos, fechaIni, fechaFin, page) => {
  const fIni = fechaIni ? fechaIni.toISOString().slice(0, 10) : ""; // Para sacarle la basura del final (resulta en yy-MM-dddd)
  const fFin = fechaFin ? fechaFin.toISOString().slice(0, 10) : fIni;
  const fecha = fIni !== "" && fFin !== "" ? fIni + "," + fFin : "";
  const total =
    datos.minTotal !== "" && datos.maxTotal !== ""
      ? datos.minTotal + "," + datos.maxTotal
      : "";

  return instance.get(
    `api/v1/restaurante/listarHistoricoPedidos?estadoPedido=${datos.estadoPedido}&medioPago=${datos.medioPago}&orden=${datos.ordenamiento}&fecha=${fecha}&total=${total}&page=${page}`,
    datos
  );
};

export const obtenerBalance = (datos, fechaIni, fechaFin) => {
  const fIni = fechaIni ? fechaIni.toISOString().slice(0, 10) : ""; // Para sacarle la basura del final (resulta en yy-MM-dddd)
  const fFin = fechaFin ? fechaFin.toISOString().slice(0, 10) : fIni;
  //const fecha = fIni !== "" && fFin !== "" ? fIni + "," + fFin : "";
  return instance.get(
    `api/v1/restaurante/obtenerBalance?categoriaMenu=${datos.categoria}&medioPago=${datos.medioPago}&fechaIni=${fIni}&fechaFin=${fFin}`
  );
};

//----------------------------------USUARIOS---------------------------------------------------

export const fetchUsuarios = () => {
  return instance.get("api/v1/admin/listarUsuarios");
};

export const eliminarUsuario = (correoUsuario) => {
  return instance.delete(`api/v1/restaurante/eliminarMenu/${correoUsuario}`);
};

export const fetchUsuariosBusqueda = (datos, fechaIni, fechaFin, p) => {
  const fIni = fechaIni ? fechaIni.toISOString().slice(0, 10) : ""; // Para sacarle la basura del final (resulta en yy-MM-dddd)
  const fFin = fechaFin ? fechaFin.toISOString().slice(0, 10) : fIni;
  const correo = Base64.encode(datos.correo);
  return instance.get(
    `api/v1/admin/listarUsuarios?correo=${correo}&tipoUser=${datos.tipoUser}&estado=${datos.estado}&orden=${datos.ordenar}&fechaReg=${fIni}&fechafin=${fFin}&page=${p}`,
    datos
  );
};

export const fetchRestauranteInfo = () => {
  return instance.get("api/v1/restaurante/getInfoRestaurante");
};

export const actualizarEstadoUsuario = (estado, id) => {
  return instance.put(`api/v1/admin/cambiarEstado/${id}`, { estado: estado });
};

export const recuperarPassword = (recoverRequest) => {
  return instance.post(
    "api/v1/password/recuperacion/solicitud",
    recoverRequest
  );
};

export const cambiarPassword = (email, pass, ptoken) => {
  const datos = { correo: email, password: pass, token: ptoken ? ptoken : "" };
  return instance.post("api/v1/password/recuperacion/cambio", datos);
};
export const checkPwdRecoveryToken = (email, ptoken) => {
  const correo = Base64.encode(email);
  const datos = { email: correo ? correo : "", token: ptoken ? ptoken : "" };
  return instance.post("api/v1/password/recuperacion/check", datos);
};

export const paypalEnviarCART = (datos) => {
  return instance.post("api/v1/cliente/realizarPedido", datos);
};

export const agregarDireccion = (direccion) => {
  return instance.post("api/v1/cliente/agregarDireccion", direccion);
};

export const modificarDireccion = (direccion, id) => {
  return instance.put(`api/v1/cliente/modificarDireccion?id=${id}`, direccion);
};

export const eliminarDireccion = (id) => {
  return instance.delete(`api/v1/cliente/eliminarDireccion?id=${id}`);
};

export const editNombre = (nombre, apellido) => {
  return instance.put(
    `api/v1/cliente/modificarCliente?nombre=${nombre}&apellido=${apellido}`
  );
};

export const fetchRestaurantesBusqueda = (datos) => {
  return instance.get(
    `api/v1/cliente/listarAbiertos?nombre=${datos.nombre}&categoria=${datos.categoria}&orden=${datos.calificacion}&direccion=${datos.idDireccion}`
  );
  // const response = axios({
  //   method: "GET",
  //   url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/cliente/listarAbiertos?nombre=${datos.nombre}&categoria=${datos.categoria}&orden=${datos.calificacion}`,
  //   data: datos,
  //   headers: {
  //     Authorization: "Bearer " + getToken(),
  //     RefreshAuthentication: "Bearer " + getRefreshToken(),
  //   },
  // });
  // response.then((res) => {});
  // return response;
};

export const obtenerPedidosRealizados = (datos, fechaIni, fechaFin, page) => {
  const fIni = fechaIni ? fechaIni.toISOString().slice(0, 10) : ""; // Para sacarle la basura del final (resulta en yy-MM-dddd)
  const fFin = fechaFin ? fechaFin.toISOString().slice(0, 10) : fIni;
  const fecha = fIni !== "" && fFin !== "" ? fIni + "," + fFin : "";
  const total =
    datos.minTotal !== "" && datos.maxTotal !== ""
      ? datos.minTotal + "," + datos.maxTotal
      : "";
  return instance.get(
    `api/v1/cliente/listarPedidosRealizados?estadoPedido=${datos.estadoPedido}&nombreMenu=${datos.nombreMenu}&nombreRestaurante=${datos.nombreRestaurante}&medioPago=${datos.medioPago}&orden=${datos.ordenamiento}&fecha=${fecha}&total=${total}&page=${page}&size=5`,
    datos
  );
};

export const hacerPedidoEfectivo = (datos) => {
  return instance.post("api/v1/cliente/realizarPedido", datos);
};

export const listarRestaurantesPorEstado = (estadoRestaurante) => {
  return instance.get(
    `api/v1/admin/listarRestaurantesPorEstado?estadoRestaurante=${estadoRestaurante}`
  );
};

export const cambiarEstadoRestaurante = (
  restaurante,
  estadoRestaurante,
  mensaje
) => {
  const correoRestaurante = Base64.encode(restaurante);
  return instance.put(
    `api/v1/admin/cambiarEstadoRestaurante?correoRestaurante=${correoRestaurante}&estadoRestaurante=${estadoRestaurante}`,
    mensaje
  );
};

export const fetchReclamos = (values) => {
  const correoCliente = Base64.encode(values.cliente);
  return instance.get(
    `api/v1/restaurante/listarReclamos?orden=${values.ordenar}&cliente=${correoCliente}&razon=${values.razon}`
  );
};

export const getMenusFromRestaurante = (restaurante) => {
  var vacia = "";
  var correoRestaurante = Base64.encode(restaurante);
  return instance.get(
    `api/v1/cliente/listarProductosRestaurante?id=${correoRestaurante}&categoria=${vacia}&precioInicial=${vacia}&precioFinal=${vacia}`
  );
};

export const fetchPedidoFromReclamo = (pedidoId) => {
  return instance.get(`api/v1/restaurante/obtenerPedido?id=${pedidoId}`);
};

export const calificarRestaurante = (data) => {
  return instance.post("api/v1/cliente/calificarRestaurante", data);
};

export const modificarCalificacionRestaurante = (data) => {
  return instance.put("api/v1/cliente/modificarCalificacionRestaurante", data);
};

export const eliminarCalificacionRestaurante = (idPedido) => {
  return instance.delete(
    `api/v1/cliente/eliminarCalificacionRestaurante?idPedido=${idPedido}`
  );
};

export const calificarCliente = (data) => {
  return instance.post("api/v1/restaurante/calificarCliente", data);
};

export const modificarCalificacionCliente = (data) => {
  return instance.put("api/v1/restaurante/modificarCalificacionCliente", data);
};

export const eliminarCalificacionCliente = (idPedido) => {
  return instance.delete(
    `api/v1/restaurante/eliminarCalificacionCliente?idPedido=${idPedido}`
  );
};

export const realizarReclamo = (reclamo) => {
  return instance.post("api/v1/cliente/agregarReclamo", reclamo);
};

export const realizarDevolucion = (pedido, estado, motivo) => {
  var id = "";
  id = pedido;
  return instance.post(
    `api/v1/restaurante/realizarDevolucion?idPedido=${id}&estadoDevolucion=${estado}`,
    motivo
  );
};

export const estadisticasUsuariosTotales = () => {
  return axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/admin/obtenerEstadisticasUsuarios`,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
};

export const estadisticasUsuariosRegistrados = () => {
  return axios({
    method: "GET",
    url: `${
      process.env.REACT_APP_BACKEND_URL_BASE
    }api/v1/admin/obtenerEstadisticasRegistros?anioPedidos=${new Date().getFullYear()}`,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
};

export const estadisticasPedidosRegistrados = () => {
  return axios({
    method: "GET",
    url: `${
      process.env.REACT_APP_BACKEND_URL_BASE
    }api/v1/admin/obtenerEstadisticasPedidos?anioPedidos=${new Date().getFullYear()}`,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
};
export const estadisticasVentasRestaurante = (restaurante) => {
  return axios({
    method: "GET",
    url: `${
      process.env.REACT_APP_BACKEND_URL_BASE
    }api/v1/admin/obtenerEstadisticasVentas?correoRestaurante=${restaurante}&anioVentas=${new Date().getFullYear()}`,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
};

export const obtenerRestaurantes = () => {
  return axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/admin/obtenerRestaurantes`,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
};

import axios from "axios";

//esta funcion es para cerrar sesion
export const clearState = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("refreshToken");
  window.location.replace("/");
};

//retorna la id del menu para el modificarMenu
export const getMenuId = () => {
  return sessionStorage.getItem("menuId");
};

//---------------------------------------LOGIN--------------------------------------------

export const getToken = () => {
  return localStorage.getItem("token");
};

export const getRefreshToken = () => {
  return localStorage.getItem("refreshToken");
};

export const checkTokens = (auth, refreshAuth) => {
  const newAuth = auth.substring(7);
  const newRefreshAuth = refreshAuth.substring(7);
  if (getToken() != null && getRefreshToken() != null) {
    if (newAuth !== getToken() || newRefreshAuth !== getRefreshToken()) {
      localStorage.setItem("token", auth);
      localStorage.setItem("refreshToken", refreshAuth);
    }
  }
};

export const userLogin = (authRequest) => {
  return axios({
    method: "POST",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/auth/login`,
    data: authRequest,
  });
};

export const fetchUserData = () => {
  const response = axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/auth/userinfo`,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {
    checkTokens(
      res.config.headers.Authorization,
      res.config.headers.RefreshAuthentication
    );
  });
  return response;
};

export const registrarCliente = (cliente) => {
  return axios({
    method: "POST",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/cliente/altaCliente`,
    data: cliente,
  });
};

export const registrarRestaurante = (restaurante) => {
  return axios({
    method: "POST",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/crearSolicitudAltaRestaurante`,
    data: restaurante,
  });
};
export const eliminarMenu = (menuId) => {
  const response = axios({
    method: "DELETE",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/eliminarMenu/${menuId}`,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {
    checkTokens(
      res.config.headers.Authorization,
      res.config.headers.RefreshAuthentication
    );
  });
  return response;
};

export const modMenu = (menuInfo, id) => {
  const response = axios({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/modificarMenu/${id}`,
    data: menuInfo,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {
    checkTokens(
      res.config.headers.Authorization,
      res.config.headers.RefreshAuthentication
    );
  });
  return response;
};

export const fetchMenus = () => {
  const response = axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/listarMenu`,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {
    checkTokens(
      res.config.headers.Authorization,
      res.config.headers.RefreshAuthentication
    );
  });
  return response;
};

export const fetchPromos = () => {
  const response = axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/listarPromocion`,
    headers: {
      Authorization: "Bearer " + getToken(),
      'RefreshAuthentication': "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {checkTokens(res.config.headers.Authorization, res.config.headers.RefreshAuthentication)});
  return response;
};

export const getMenuInfo = () => {
  const menuId = getMenuId();
  const response = axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/getInfoMenu/${menuId}`,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {
    checkTokens(
      res.config.headers.Authorization,
      res.config.headers.RefreshAuthentication
    );
  });
  return response;
};

export const altaMenu = (menu) => {
  const response = axios({
    method: "POST",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/agregarMenu`,
    data: menu,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {
    checkTokens(
      res.config.headers.Authorization,
      res.config.headers.RefreshAuthentication
    );
  });
  return response;
};

export const cambiarEstado = (estado) => {
  const response = axios({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/modificarEstado/${estado}`,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {
    checkTokens(
      res.config.headers.Authorization,
      res.config.headers.RefreshAuthentication
    );
  });
  return response;
};

export const actualizarEstadoPedido = (estado, id) => {
  const response = axios({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/actualizarEstadoPedido/${id}`,
    data: {estado: estado},
    headers: {
      Authorization: "Bearer " + getToken(),
      'RefreshAuthentication': "Bearer " + getRefreshToken(),
    }
  });
  response.then((res) => {checkTokens(res.config.headers.Authorization, res.config.headers.RefreshAuthentication)})
    .catch((error)=>{checkTokens(error.config.headers.Authorization, error.config.headers.RefreshAuthentication)});
  return response;
};

export const altaAdmin = (datos) => {
  return axios({
    method: "POST",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/admin/altaAdmin`,
    data: datos,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const eliminarCuentaClientePropia = () => {
  return axios({
    method: "DELETE",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/cliente/eliminarCuenta`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const obtenerPedidosSinFinalizarEfectivo = () => {
  const response = axios ({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/listarPedidosEfectivoCompletado`,
    headers: {
      Authorization: "Bearer " + getToken(),
      'RefreshAuthentication': "Bearer " + getRefreshToken(),
    }
  });
  response.then((res) => {checkTokens(res.config.headers.Authorization, res.config.headers.RefreshAuthentication)})
    .catch((error)=>{checkTokens(error.config.headers.Authorization, error.config.headers.RefreshAuthentication)});
  return response;
};

//----------------------------------USUARIOS---------------------------------------------------

export const fetchUsuarios = () => {
  return axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}/api/v1/admin/listarUsuarios`,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
};

export const eliminarUsuario = (correoUsuario) => {
  return axios({
    method: "DELETE",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/eliminarMenu/${correoUsuario}`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const fetchUsuariosBusqueda = (datos, fechaIni, fechaFin) => {
  const fIni = fechaIni ? fechaIni.toISOString().slice(0, 10) : ""; // Para sacarle la basura del final (resulta en yy-MM-dddd)
  const fFin = fechaFin ? fechaFin.toISOString().slice(0, 10) : fIni;
  return axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/admin/listarUsuarios?correo=${datos.correo}&tipoUser=${datos.tipoUser}&estado=${datos.estado}&orden=${datos.ordenar}&fechaReg=${fIni}&fechafin=${fFin}`,
    data: datos,
    headers: {
      Authorization: "Bearer " + getToken(),
      RefreshAuthentication: "Bearer " + getRefreshToken(),
    },
  });
};

export const fetchRestauranteInfo = () => {
  return axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/getInfoRestaurante`,
    headers: {
      Authorization: "Bearer " + getToken(),
      'RefreshAuthentication': "Bearer " + getRefreshToken(),
    },
  });
}

export const actualizarEstadoUsuario = (estado, id) => {
  console.log(estado);
  return axios({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/admin/cambiarEstado/${id}`,
    data: { estado: estado },
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};
/*export const setEstadoUsuarioEliminado = (correo) => {
  return axios({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/admin/eliminarUsuario/${correo}`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};*/

export const recuperarPassword = (recoverRequest) => {
  console.log(recoverRequest);
  return axios({
    method: "POST",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/password/recuperacion/solicitud`,
    data: recoverRequest,
  });
};

export const cambiarPassword = (email, pass, ptoken) => {
  const datos = { correo: email, password: pass, token: ptoken ? ptoken : "" };
  console.log(datos);
  console.log(datos);
  return axios({
    method: "POST",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/password/recuperacion/cambio`,
    data: datos,
  });
};
export const checkPwdRecoveryToken = (email, ptoken) => {
  const datos = { email: email ? email : "", token: ptoken ? ptoken : "" };
  console.log(datos);
  return axios({
    method: "POST",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/password/recuperacion/check`,
    data: datos,
  });
};

export const agregarDireccion = (direccion) => {
  return axios({
    method: "POST",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/cliente/agregarDireccion`,
    data: direccion,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const modificarDireccion = (direccion, id) => {
  return axios({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/cliente/modificarDireccion?id=${id}`,
    data: direccion,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const eliminarDireccion = (id) => {
  return axios({
    method: "DELETE",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/cliente/eliminarDireccion?id=${id}`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const editNombre = (nombre, apellido) => {
  return axios({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/cliente/modificarCliente?nombre=${nombre}&apellido=${apellido}`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const fetchRestaurantesBusqueda = (datos) => {
  const response = axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/cliente/listarAbiertos?nombre=${datos.nombre}&categoria=${datos.categoria}&orden=${datos.calificacion}`,
    data: datos,
    headers: {
      Authorization: "Bearer " + getToken(),
      'RefreshAuthentication': "Bearer " + getRefreshToken(),
    }
  });
  response.then((res) => {checkTokens(res.config.headers.Authorization, res.config.headers.RefreshAuthentication)});
  return response;
};

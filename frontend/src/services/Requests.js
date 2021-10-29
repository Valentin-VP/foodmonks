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

//----------------------------------------------------------------------------------

export const getToken = () => {
  return localStorage.getItem("token");
};

export const getRefreshToken = () => {
  return localStorage.getItem("refreshToken");
}

export const checkTokens = (auth, refreshAuth) => {
  const newAuth = auth.substring(7);
  const newRefreshAuth = refreshAuth.substring(7);
  if(getToken() != null && getRefreshToken() != null) {
    if(newAuth !== getToken() || newRefreshAuth !== getRefreshToken()) {
      localStorage.setItem("token", auth);
      localStorage.setItem("refreshToken", refreshAuth);
    }
  }
}

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
      'RefreshAuthentication': "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {checkTokens(res.config.headers.Authorization, res.config.headers.RefreshAuthentication)});
  return response;
};

export const eliminarMenu = (menuId) => {
  const response = axios({
    method: "DELETE",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/eliminarMenu/${menuId}`,
    headers: {
      Authorization: "Bearer " + getToken(),
      'RefreshAuthentication': "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {checkTokens(res.config.headers.Authorization, res.config.headers.RefreshAuthentication)});
  return response;
};

export const modMenu = (menuInfo, id) => {
  const response = axios({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/modificarMenu/${id}`,
    data: menuInfo,
    headers: {
      Authorization: "Bearer " + getToken(),
      'RefreshAuthentication': "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {checkTokens(res.config.headers.Authorization, res.config.headers.RefreshAuthentication)});
  return response;
};

export const fetchMenus = () => {
  const response = axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/listarMenu`,
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
      'RefreshAuthentication': "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {checkTokens(res.config.headers.Authorization, res.config.headers.RefreshAuthentication)});
  return response;
};

export const altaMenu = (menu) => {
  const response = axios({
    method: "POST",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/agregarMenu`,
    data: menu,
    headers: {
      Authorization: "Bearer " + getToken(),
      'RefreshAuthentication': "Bearer " + getRefreshToken(),
    },
  });
  response.then((res) => {checkTokens(res.config.headers.Authorization, res.config.headers.RefreshAuthentication)});
  return response;
};

export const cambiarEstado = (estado) => {
  const response = axios ({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/modificarEstado/${estado}`,
    headers: {
      Authorization: "Bearer " + getToken(),
      'RefreshAuthentication': "Bearer " + getRefreshToken(),
    }
  });
  response.then((res) => {checkTokens(res.config.headers.Authorization, res.config.headers.RefreshAuthentication)});
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
  return axios ({
    method: "DELETE",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/cliente/eliminarCuenta`,
    headers: {
      Authorization: "Bearer " + getToken(),
    }
  });
};

export const recuperarPassword=(recoverRequest)=>{
  console.log(recoverRequest);
  return axios({
      method:"POST",
      url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/password/recuperacion/solicitud`,
      data : recoverRequest
  })
}

export const cambiarPassword=(email, pass, ptoken)=>{
  const datos = {correo: email,
    password: pass,
    token: ptoken ? ptoken : ""}
    console.log(datos);
  console.log(datos);
  return axios({
      method:"POST",
      url:`${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/password/recuperacion/cambio`,
      data:datos
  })
}
export const checkPwdRecoveryToken=(email, ptoken)=>{
  const datos = {email: email ? email : "",
    token: ptoken ? ptoken : ""}
    console.log(datos);
  return axios({
      method:"POST",
      url:`${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/password/recuperacion/check`,
      data:datos
  })
}

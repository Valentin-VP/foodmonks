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
  if(getToken() != null && getRefreshToken() != null) {
    if(auth !== getToken() || refreshAuth !== getRefreshToken()) {
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
      Authorization: getToken(),
      'RefreshAuthentication': "Bearer " + getRefreshToken(),
    }
  });
  response.then((res) => {checkTokens(res.config.headers.Authorization, res.config.headers.RefreshAuthentication)});
  return response;
};

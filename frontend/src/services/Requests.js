import axios from "axios";

//esta funcion es para cerrar sesion
export const clearState = () => {
  localStorage.removeItem("token");
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

export const userLogin = (authRequest) => {
  return axios({
    method: "POST",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/auth/login`,
    data: authRequest,
  });
};

export const fetchUserData = () => {
  return axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/auth/userinfo`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const eliminarMenu = (menuId) => {
  return axios({
    method: "DELETE",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/eliminarMenu/${menuId}`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const modMenu = (menuInfo, id) => {
  return axios({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/modificarMenu/${id}`,
    data: menuInfo,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const fetchMenus = () => {
  return axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/listarMenu`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const getMenuInfo = () => {
  const menuId = getMenuId();
  return axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/getInfoMenu/${menuId}`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const altaMenu = (menu) => {
  return axios({
    method: "POST",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/agregarMenu`,
    data: menu,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const cambiarEstado = (estado) => {
  return axios ({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/modificarEstado/${estado}`,
    headers: {
      Authorization: "Bearer " + getToken(),
    }
  });
};

export const fetchUsuarios = () => {
  return axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}/api/v1/admin/listarUsuarios`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
}

export const eliminarUsuario = (correoUsuario) => {
  return axios({
    method: "DELETE",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/restaurante/eliminarMenu/${correoUsuario}`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
}

export const fetchUsuariosBusqueda = (datos) => {
  return axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}/api/v1/admin/filtroUsuarios`,
    data: datos,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const actualizarEstadoUsuario = (estado, id) => {
  return axios({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}/api/v1/admin/actualizarEstado/${id}`,
    data: estado,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};
/*export const setEstadoUsuarioEliminado = (correo) => {
  return axios({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}/api/v1/admin/eliminarUsuario/${correo}`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};*/

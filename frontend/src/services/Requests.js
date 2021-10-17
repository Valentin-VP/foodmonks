import axios from "axios";

//esta funcion es para cerrar sesion
export const clearState = () => {
  localStorage.removeItem("token");
  window.location.replace("/");
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
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}/api/v1/restaurante/eliminarMenu/${menuId}`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const modMenu = (menuInfo) => {
  return axios({
    method: "PUT",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}/api/v1/restaurante/modificarMenu`,
    data: menuInfo,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const fetchMenus = () => {
  return axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}/api/v1/restaurante/listaMenu`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

export const getMenuInfo = (menuId) => {//falta
  return axios({
    method: "GET",
    url: `${process.env.REACT_APP_BACKEND_URL_BASE}/api/v1/restaurante/getInfoMenu/${menuId}`,
    headers: {
      Authorization: "Bearer " + getToken(),
    },
  });
};

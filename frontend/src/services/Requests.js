import axios from "axios";

//esta funcion es para cerrar sesion
export const clearState = () => {
  localStorage.removeItem("token");
  window.location.replace("/");
};

//esta funcion es para luego de recuperar contraseña
export const clearRecoverEmail = () => {
  localStorage.removeItem("recover.mail");
};

//retorna la id del menu para el modificarMenu
export const getMenuId = () => {
  return sessionStorage.getItem("menuId");
};

//retorna el mail que uso para resetear la password
export const getRecoverEmail = () => {
  return localStorage.getItem("recover.mail");
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

export const recuperarPassword=(recoverRequest)=>{
  return axios({
      method:"POST",
      url: `${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/password/recuperacion/solicitud`,
      data : recoverRequest
  })
}

export const cambiarPassword=(pass, ptoken)=>{
  const datos = {correo: getRecoverEmail(),
    password: pass,
    token: ptoken ? ptoken : ""}
  console.log(datos);
  return axios({
      method:"POST",
      url:`${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/password/recuperacion/cambio`,
      data:datos
  })
}
export const checkPwdRecoveryToken=(recoverRequest)=>{
  return axios({
      method:"POST",
      url:`${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/auth/check`,
      data:recoverRequest
  })
}
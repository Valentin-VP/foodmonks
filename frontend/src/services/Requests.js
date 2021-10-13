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

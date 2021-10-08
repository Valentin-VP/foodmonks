import React from 'react';
import axios from 'axios';


const getToken=()=>{
    return localStorage.getItem('USER_KEY');
}

//const {REACT_APP_BACKEND_URL_BASE} = process.env;

export const userLogin=(authRequest)=>{
    return axios({
        'method':'POST',
        'url':`${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/auth/login`,
        'data':authRequest
    })
}

export const fetchUserData=()=>{
    return axios({
        method:'GET',
        url:`${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/auth/userinfo`,
        headers:{
            'Authorization':'Bearer '+getToken()
        }
    })
}

//export const fetchUsers=()=>{
//    return axios({
//        method:'GET',
//        url:`${process.env.REACT_APP_BACKEND_URL_BASE}api/v1/users/showUsers`,
//        headers:{
//            'Authorization':'Bearer '+getToken()
//        }
//    })
//}
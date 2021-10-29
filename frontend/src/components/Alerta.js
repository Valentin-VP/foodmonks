import React from "react";
import { Alert } from "react-bootstrap";

export const Alerta = ({msg, tipo}) => <Alert variant={tipo}>{msg}</Alert>;

//tipo puede ser
//'primary',
//'secondary',
//'success',
//'danger',
//'warning',
//'info',
//'light',
//'dark'

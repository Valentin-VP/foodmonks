import "./App.css";
import { React, useState } from "react";
import { BrowserRouter, Switch, Route } from "react-router-dom";
import Login from "./pages/Login";
import { toast } from "react-toastify";
import Admin from "./pages/admin/Admin";
import Cliente from "./pages/cliente/Cliente";
import  RegistroCliente from "./pages/cliente/RegistroCliente";
import Restaurante from "./pages/restaurante/Restaurante";
import { getToken, fetchUserData } from "./services/Requests";
import { Spinner } from "react-bootstrap";

toast.configure(); //esto esta para poder enviar las notificaciones
function App() {
  const [tipoUser, setTipoUser] = useState();
  if (getToken() != null && tipoUser == null) {
    fetchUserData().then((response) => {
      setTipoUser(response.data.roles[0].role);
    });
  }
  if ( tipoUser == null) {
    setTipoUser("NO_ROLE");
  }

  switch (tipoUser) {
    case "ROLE_CLIENTE":
      return <Cliente />;
    case "ROLE_RESTAURANTE":
      return <Restaurante />;
    case "ROLE_ADMIN":
      return <Admin />;
    case "NO_ROLE":
      return (
        <BrowserRouter>
          <Switch>
            <Route exact path="/" component={Login} />
            <Route exact path="/register" component={RegistroCliente} />
          </Switch>
        </BrowserRouter>
      );
    default:
      return <Spinner className="text-justify" animation="border" />
  }
}

export default App;


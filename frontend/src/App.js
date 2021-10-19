import "./App.css";
import { React, useState } from "react";
import { BrowserRouter, Switch, Route } from "react-router-dom";
import Login from "./pages/Login";
import { toast } from "react-toastify";
import Admin from "./pages/admin/Admin";
import Cliente from "./pages/cliente/Cliente";
import Register from "./pages/Login";
import Restaurante from "./pages/restaurante/Restaurante";
import { getToken, fetchUserData } from "./services/Requests";

toast.configure(); //esto esta para poder enviar las notificaciones
function App() {
  const [tipoUser, setTipoUser] = useState();
   if (getToken() != null && tipoUser == null) {
     fetchUserData().then((response) => {
       setTipoUser(response.data.roles[0].role);
     });
   }


  switch (tipoUser) {
    case "ROLE_CLIENTE":
      return <Cliente />;
    case "ROLE_RESTAURANTE":
      return <Restaurante />;
    case "ROLE_ADMIN":
      return <Admin />;
    default:
      return (
        <BrowserRouter>
          <Switch>
            <Route exact path="/" component={Login} />
            <Route exact path="/register" component={Register} />
          </Switch>
        </BrowserRouter>
      );
  }
}

export default App;

// function PrivateRoute({ component, ...rest }) {
//   const [tipoUser, setTipoUser] = useState("CLIENTE");
//   // if (getToken() != null) {
//   //   fetchUserData().then((response) => {
//   //     setTipoUser(response.data.roles);
//   //   });
//   // }

//   return (
//     <Route
//       {...rest}
//       render={() => {
//         switch (tipoUser) {
//           case "CLIENTE":
//             return <Cliente />;
//           case "RESTAURANTE":
//             return <Restaurante />;
//           case "ADMIN":
//             return <Admin />;
//           default:
//             return <Redirect to="/login" />;
//         }
//       }}
//     />
//   );
// }

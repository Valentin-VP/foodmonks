import { React, Fragment } from "react";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import Home from "./Home";
import { Cart } from "./Cart";
import BuscarMenusPromociones from "./BuscarMenusPromociones";
import { NavigationBar } from "../cliente/NavBar";
import { Footer } from "../../components/Footer";
import PerfilCliente from "./PerfilCliente";
import ModificarDireccion from "./ModificarDireccion";
import { CartProvider } from "react-use-cart";
import styled from "styled-components";
import BuscarPedidosRealizados from "./BuscarPedidosRealizados";
import Reclamo from "./Reclamo";

const Styles = styled.div`
  #page-container {
    position: relative;
    min-height: calc(100vh - 3.5rem);
    padding-bottom: 7rem; //doble footer
  }
`;

function Cliente() {
  return (
    <Styles>
      <CartProvider>
        <div id="page-container">
          <NavigationBar />
          <Router>
            <Switch>
              <Fragment>
                {/* el home tiene su propio layout*/}
                <Route exact path="/" component={Home} />
                <Route exact path="/cart" component={Cart} />
                <Route exact path="/perfil" component={PerfilCliente} />
                <Route
                  exact
                  path="/modificarDireccion"
                  component={ModificarDireccion}
                />
                <Route
                  exact
                  path="/perfilRestaurante"
                  component={BuscarMenusPromociones}
                />
                <Route
                  path="/listadoPedidos"
                  component={BuscarPedidosRealizados}
                />
                <Route exact path="/reclamo" component={Reclamo} />
                {/* <Route path="no-match" component={NoMatch} /> */}
              </Fragment>
            </Switch>
          </Router>
          <Footer />
        </div>
      </CartProvider>
    </Styles>
  );
}

export default Cliente;

// function NoMatch() {
//   let location = useLocation();

//   return (
//     <div>
//       <h3 className="text-center">
//         No se encontro nada para <code>{location.pathname}</code>
//       </h3>
//     </div>
//   );
// }

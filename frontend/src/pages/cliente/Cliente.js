import { React, Fragment } from "react";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import Home from "./Home";
import { Cart } from "./Cart";
import { Grafico } from "../Grafico";
import BuscarMenusPromociones from "./BuscarMenusPromociones";
import { NavigationBar } from "../cliente/NavBar";
import { Footer } from "../../components/Footer";
import PerfilCliente from "./PerfilCliente";
import ModificarDireccion from "./ModificarDireccion";
import { CartProvider } from "react-use-cart";
import styled from "styled-components";

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
      <div id="page-container">
        <NavigationBar />
        <Router>
          <Switch>
            <Fragment>
              <CartProvider>
                {/* el home tiene su propio layout*/}
                <Route exact path="/" component={Home} />
                <Route path="/cart" component={Cart} />
                <Route path="/listarProductos" component={BuscarMenusPromociones}/>
              </CartProvider>
              <Route exact path="/perfil" component={PerfilCliente} />
              <Route exact path="/modificarDireccion" component={ModificarDireccion} />
              <Route exact path="/grafica" component={Grafico} />
              {/* <Route path="no-match" component={NoMatch} /> */}
            </Fragment>
          </Switch>
        </Router>
        <Footer />
      </div>
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

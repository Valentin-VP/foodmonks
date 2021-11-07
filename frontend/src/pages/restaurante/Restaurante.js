import React from "react";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import styled from "styled-components";
import Home from "./Home";
import Menu from "./Menu";
import AltaPromocion from "./AltaPromocion";
import ModificarMenu from "./ModificarMenu";
import AltaMenu from "./AltaMenu";
import ListadoPedidosEfectivo from "./ListarPedidosEfectivo";
import { Footer } from "../../components/Footer";
import { NavigationBar } from "./NavBar";
import Promocion from "./Promocion";

const Styles = styled.div`
  #page-container {
    position: relative;
    min-height: calc(100vh - 3.5rem);
    padding-bottom: 3.5rem; //doble footer
  }
`;

function Restaurante() {
  return (
    <Styles>
      <div id="page-container">
        <NavigationBar />
        <Router>
          <Switch>
            {/* el home tiene su propio layout*/}
            <Route exact path="/" component={Home} />
            <Route exact path="/menu" component={Menu} />
            <Route exact path="/modificarMenu" component={ModificarMenu} />
            <Route exact path="/altaMenu" component={AltaMenu} />
            <Route exact path="/promocionar" component={AltaPromocion}/>
            <Route exact path="/promocion" component={Promocion} />
            <Route exact path="/listadoPedidosEfectivo" component={ListadoPedidosEfectivo} />
            {/* <Route path="no-match" component={NoMatch} /> */}
          </Switch>
        </Router>
        <Footer />
      </div>
    </Styles>
  );
}

export default Restaurante;

import React from "react";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import styled from "styled-components";
import { Home } from "./Home";
import Usuario from "./Usuario";
import { Footer } from "../../components/Footer";
import { NavigationBar } from "../admin/NavBar";
import BuscarRegistrados from "../admin/BuscarRegistrados"

const Styles = styled.div`
  #page-container {
    position: relative;
    min-height: calc(100vh - 3.5rem);
    padding-bottom: 7rem; //doble footer
  }
`;

function Admin() {
  return (
    <Styles>
      <div id="page-container">
        <NavigationBar />
        <Router>
          <Switch>
            {/* el home tiene su propio layout*/}
            <Route exact path="/" component={Home} />
            <Route exact path="/buscarUsuarios" component={BuscarRegistrados} />
            <Route exact path="/usuarios" component={Usuario}/>
            {/* <Route path="no-match" component={NoMatch} /> */}
          </Switch>
        </Router>
        <Footer />
      </div>
    </Styles>
  );
}

export default Admin;

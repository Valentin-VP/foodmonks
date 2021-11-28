import React from "react";
import { Navbar, Nav, NavDropdown } from "react-bootstrap";
import styled from "styled-components";
import foodlogo from "../../assets/foodLogo.png"; // Tell webpack this JS file uses this image
import { clearState } from "../../services/Requests";
import { FiLogOut } from "react-icons/fi";

console.log(foodlogo);

const Styles = styled.div`
  #container {
    margin-bottom: 56px; //tamaño del navbar
    // para separar del navbar
  }

  .navbar {
    background-color: #e87121;
    .logo {
      margin-left: 25px;
    }
    .items {
      margin-left: auto;
      font-weight: bold;
    }
  }

  .navbar-collapse {
    margin-left: 25px;
  }

  .navbar-light .navbar-toggler-icon {
    background-image: url("https://icons.veryicon.com/png/o/food--drinks/food-series-1/hamburger-46.png");
  }

  .navbar-toggler {
    margin-right: 20px;
  }

  .navbar-brand,
  .navbar-nav .nav-link {
    color: white;

    &hover: {
      color: black;
    }
  }

  .carrito {
    margin-top: 5px;
    #span {
      position: relative;
      left: -12px;
      top: 5px;
      visibility: shown;
      color: white;
      background: #e60000;
    }
  }

  .dropdown-menu-color {
    background-color: rgb(231, 107, 24);
    a {
      &:hover {
        //background-color: rgba(231, 107, 24, 0.7);
        background-color: #d06016;
      }
    }
  }
`;

export const NavigationBar = () => (
  <Styles>
    <div id="container">
      <Navbar expand="lg" className="navbar fixed-top">
        <Navbar.Brand className="logo" href="/">
          <img src={foodlogo} alt="logo" width="150px" />
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="items">
            <NavDropdown
              align="end"
              title={sessionStorage.getItem("nombreUsuario")}
              menuVariant="color"
            >
              <NavDropdown.Item href="/historico">Histórico</NavDropdown.Item>
              <NavDropdown.Item href="/reclamos">Reclamos</NavDropdown.Item>
              <NavDropdown.Item href="/promocion">Promociones</NavDropdown.Item>
              <NavDropdown.Item href="/menu">Menús</NavDropdown.Item>
              <NavDropdown.Item href="/listadoPedidosEfectivo">
                Cobrar Pedidos Efectivo
              </NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item onClick={clearState}>
                Cerrar Sesion <FiLogOut color="black" />
              </NavDropdown.Item>
            </NavDropdown>
          </Nav>
        </Navbar.Collapse>
      </Navbar>
    </div>
  </Styles>
);

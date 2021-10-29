import React from "react";
import { Navbar, Nav, NavDropdown } from "react-bootstrap";
import styled from "styled-components";
import foodlogo from "../../assets/foodLogo.png"; // Tell webpack this JS file uses this image
import cartIcon from "../../assets/cartIcon.png";
import { Noti } from "../../components/Notification";
import { clearState, eliminarCuentaClientePropia } from "../../services/Requests";

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
            <Nav.Item>
              <Nav.Link onClick={() => {Noti("esto es un mensaje de prueba")}}>Notificacion</Nav.Link>
            </Nav.Item>
            <NavDropdown title="Cliente" menuVariant="color">
              <NavDropdown.Item href="#action/3.3">Prueba</NavDropdown.Item>
              {/* Esto no va acá sino en el perfil del usuario */}
              <NavDropdown.Item onClick={()=>{
                eliminarCuentaClientePropia().then((response)=>{
                  console.log(response.status);
                  clearState();
                }).catch((error)=>{
                  console.log(error);
                })}
                }>Eliminar Cuenta</NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item onClick={clearState}>
                Cerrar Sesion
              </NavDropdown.Item>
            </NavDropdown>
            <Nav.Item>
              <div className="carrito">
                <a href="/cart">
                  <img src={cartIcon} alt="carrito" width="35px" />
                  <span id="span" className="badge rounded-pill ">
                    1
                  </span>
                </a>
              </div>
            </Nav.Item>
          </Nav>
        </Navbar.Collapse>
      </Navbar>
    </div>
  </Styles>
);

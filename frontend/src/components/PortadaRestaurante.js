import React from "react";
import { Container } from "react-bootstrap";
import styled from "styled-components";
import imagenPortada from "../assets/resPort.jpg";

const Styles = styled.div`
  .portada {
    display: flex;
    background-image: url(${imagenPortada});
    background-size: cover;
    background-color: #e5e5e5;
    background-position: center;
    background-repeat: no-repeat;
    box-shadow: inset 0 -120px 150px -10px black;
    height: 300px;
    width: 100%;
    margin-top: 56px;
  }

  .contenido {
    margin-top: 4rem;
  }

  .logo {
    width: 8rem;
  }

  .nombreRestaurante {
    margin-top: 1rem;
    color: white;
  }
  .calificacion {
    position: relative;
    color: white;
    position: relative;
    text-align: right;
  }

  img {
    height: 8rem;
    width: 8rem;
    object-fit: cover;
  }
`;

export const PortadaRestaurante = ({ props }) => (
  <Styles>
    <Container fluid className="portada">
      <Container className="contenido">
        <img src={props.logo} className="logo" alt="Logo de Restaurante" />
        <h3 className="nombreRestaurante">{props.nombre}</h3>
        <div className="calificacion">
          {props.cantCal < 10 ? <h3>-⭐</h3> : <h3>{props.calificacion}⭐</h3>}
        </div>
      </Container>
    </Container>
  </Styles>
);

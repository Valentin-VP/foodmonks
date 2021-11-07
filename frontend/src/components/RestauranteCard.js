import React from "react";
import styled from "styled-components";
import { Button } from "react-bootstrap";

const Styles = styled.div`
  .card {
    margin-left: 20px;
    margin-right: 20px;
  }

  img {
    object-fit: cover;
    border-radius: 3px 3px 0px 0px;
  }

  .btn-primary {
    color: white;
    background-color: #e87121;
    border: none;
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
    &:hover {
      background-color: #da6416;
    }
    $:active {
      background-color: black !important;
    }
  }
`;

const RestauranteCard = (props) => {
    const onClick = (correo, imagen, calificacion, nombre) => {
        sessionStorage.setItem("restauranteId", correo);
        sessionStorage.setItem("restauranteImagen", imagen);
        sessionStorage.setItem("restauranteCalif", calificacion);
        sessionStorage.setItem("restauranteNombre", nombre);
      }

  return (
    <Styles>
      <div className="card">
        <img src={props.imagen} alt="productimg" height="200" />
        <div className="card-body">
          <h5 className="card-title">{props.nombre}</h5>
          <h5 className="card-subtitle">Tel: {props.telefono}</h5>
          <p className="card-text">{props.calificacion}⭐</p>
          <Button
            href="/listarProductos"
            className="btn-primary margin-auto"
            onClick={() => onClick(props.correo, props.imagen, props.calificacion, props.nombre)}
          >
            Acceder
          </Button>
        </div>
      </div>
    </Styles>
  );
};

export default RestauranteCard;
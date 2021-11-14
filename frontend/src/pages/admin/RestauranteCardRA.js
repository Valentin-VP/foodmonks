import React from "react";
import styled from "styled-components";

const Styles = styled.div`
  .card {
    &:active {
      transform: scale(0.95);
    }
  }

  img {
    object-fit: cover;
    border-radius: 3px 3px 0px 0px;
    height: 12rem;
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
  const onClick = () => {
    sessionStorage.setItem("restauranteId", props.correo);
    sessionStorage.setItem("restauranteImagen", props.imagen);
    sessionStorage.setItem("restauranteCalif", props.calificacion);
    sessionStorage.setItem("restauranteNombre", props.nombre);
    window.location.replace("/listarProductos");
  };

  return (
    <Styles>
      <div className="card" onClick={() => onClick()}>
        <img src={props.imagen} alt="restauranteimg" />
        <div className="card-body">
          <h5 className="card-title">{props.nombre}</h5>
          <h5 className="card-subtitle">Teléfono: {props.telefono}</h5>
          <p className="card-text">{props.calificacion}⭐</p>
        </div>
      </div>
    </Styles>
  );
};

export default RestauranteCard;

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
    height: 17rem;
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

  return (
    <Styles>
      <div className="card">
        <img src={props.imagen} alt="restauranteimg" />
        <div className="card-body">
          <h5 className="card-title">{props.nombre}</h5>
          <h5 className="card-subtitle">Teléfono: {props.telefono}</h5>
        </div>
      </div>
    </Styles>
  );
};

export default RestauranteCard;

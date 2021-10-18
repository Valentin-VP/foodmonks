import React from "react";
import styled from "styled-components";
import { Button, ButtonGroup } from "react-bootstrap";
import { eliminarMenu } from "../../services/Requests";

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

const MenuCard = (props) => {
  return (
    <Styles>
      <div className="card">
        <img src={props.img} alt="productimg" height="200" />
        <div className="card-body">
          <h5 className="card-title">{props.nombre}</h5>
          <h5 className="card-subtitle">
            $ {props.price} - {props.multiplicador}%
          </h5>
          <h6 className="card-subtitle">{props.categoria}</h6>
          <p className="card-text">{props.descripcion}</p>
          <div className="grupoBotones">
            <ButtonGroup aria-label="Basic example">
              <Button id="eliminar" className="btn-primary margin-auto">
                Eliminar
              </Button>
              {/*tengo redireccionar a modificarMenu */}
              <Button id="modificar" className="btn-primary margin-auto">
                Modificar
              </Button>
              <Button id="promocion" className="btn-primary margin-auto">
                Promocion
              </Button>
            </ButtonGroup>
          </div>
        </div>
      </div>
    </Styles>
  );
};

export default MenuCard;

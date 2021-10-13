import React from "react";
import styled from "styled-components";
import { Button } from "react-bootstrap";
import { useCart } from "react-use-cart";

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

const ItemCard = (props) => {
  const { addItem } = useCart();

  return (
    <Styles>
      <div className="card">
        <img src={props.img} alt="productimg" height="200" />
        <div className="card-body">
          <h5 className="card-title">{props.title}</h5>
          <h5 className="card-subtitle">$ {props.price}</h5>
          <p className="card-text">{props.desc}</p>
          <Button
            className="btn-primary margin-auto"
            onClick={() => addItem(props.item)}
          >
            Agregar al carrito
          </Button>
        </div>
      </div>
    </Styles>
  );
};

export default ItemCard;

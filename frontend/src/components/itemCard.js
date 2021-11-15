import React, { useState } from "react";
import styled from "styled-components";
import { Button } from "react-bootstrap";
import { useCart } from "react-use-cart";
import { NotiError } from "./Notification";

const Styles = styled.div`
  .card {
    margin-left: 20px;
    margin-right: 20px;
  }

  itemImg {
    object-fit: cover;
    border-radius: 3px 3px 0px 0px;
    height: 200px;
    position: relative;
    top: 0;
    left: 0;
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

  #descuento {
    color: #59eb59;
  }

  #promo {
    position: absolute;
    height: 6rem;
    text-align: right;
  }

  p {
    margin-bottom: 0;
  }
`;

const ItemCard = (props) => {
  const { isEmpty, addItem, items, removeItem } = useCart();

  const agregarItem = (item) => {
    if (isEmpty) {
      sessionStorage.setItem("restauranteCart", item.restaurante);
      console.log(item.price);
      item.price =
        item.price - item.price * (item.multiplicadorPromocion / 100);
      console.log(item.price);
      addItem(item);
    } else if (sessionStorage.getItem("restauranteCart") === item.restaurante) {
      item.price =
        item.price - item.price * (item.multiplicadorPromocion / 100);
      addItem(item);
    } else {
      NotiError("Ya estas comprando en otro restaurante");
    }
  };

  // const agregarItem = (item) => {
  //   if (isEmpty) {
  //     sessionStorage.setItem("restauranteCart", item.restaurante);
  //     if (item.multiplicadorPromocion === 0) {
  //       addItem(item);
  //     } else {
  //       item.price = item.price - item.multiplicadorPromocion / 100;
  //       addItem(item);
  //     }
  //   } else if (sessionStorage.getItem("restauranteCart") === item.restaurante) {
  //     if (item.multiplicadorPromocion === 0) {
  //       addItem(item);
  //     } else {
  //       item.price = item.price - item.multiplicadorPromocion / 100;
  //       addItem(item);
  //     }
  //   } else {
  //     NotiError("Ya estas comprando en otro restaurante");
  //   }

  return (
    <Styles>
      <div className="card">
        <img src={props.img} id="itemImg" alt="productimg"></img>
        {props.item.multiplicadorPromocion !== 0 ? (
          <img
            id="promo"
            src="https://www.moteur.ma/media/images/other/promo_icon_fr.png"
          ></img>
        ) : null}
        <div className="card-body">
          <h5 className="card-title">{props.title}</h5>
          <h5 className="card-subtitle">
            $
            {props.price -
              props.price * (props.item.multiplicadorPromocion / 100)}
          </h5>
          {props.item.multiplicadorPromocion !== 0 ? (
            <p id="descuento">-{props.item.multiplicadorPromocion} %</p>
          ) : (
            <p>ㅤ</p>
          )}
          {items.find((item) => item.id === props.item.id) === undefined ? (
            <Button
              className="btn-primary margin-auto"
              onClick={() => agregarItem(props.item)}
            >
              Agregar al carrito
            </Button>
          ) : (
            <Button
              variant="danger"
              className="margin-auto"
              onClick={() => removeItem(props.item.id)}
            >
              Eliminar
            </Button>
          )}
        </div>
      </div>
    </Styles>
  );
};

export default ItemCard;

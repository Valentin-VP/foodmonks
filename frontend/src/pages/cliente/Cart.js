import React, { useState, useEffect } from "react";
import { Form, Button } from "react-bootstrap";
import { useCart } from "react-use-cart";
import { fetchUserData } from "../../services/Requests";
import styled from "styled-components";
import { Layout } from "../../components/Layout";
import { Loading } from "../../components/Loading";
import { IoBagAddSharp, IoBagRemoveSharp } from "react-icons/io5";
import { MdDeleteForever, MdOutlinePayments } from "react-icons/md";
import { ImPaypal } from "react-icons/im";
import PaypalCheckoutButton from "../../components/PaypalCheckoutButton";

const Styles = styled.div`
  .card {
    border-radius: 5px;
    padding-left: 2.5%;
    padding-right: 2.5%;
    padding-top: 1rem;
  }
  .items {
    width: 70%;
  }
  .compra {
    text-align: center;
    width: 25%;
  }

  h1 {
    margin-top: 20px;
  }
  img {
    height: 6rem;
    border-radius: 5px;
  }
  .btn-info {
    color: white;
    background-color: #e87121;
    border: none;
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
    &:hover {
      background-color: #da6416;
    }
    &:active {
      background-color: black !important;
    }
  }
  .ultima {
    text-align: right;
    button {
      max-width: 8rem;
      margin-left: calc(100% - 8rem);
    }
  }

  tr {
    border-radius: 10px;
  }
  table,
  tbody,
  tr,
  td {
    background-color: white;
  }

  #direcciones {
    background-color: white;
    color: black;
    border-color: #e87121;
    width: 80%;
    margin: auto;
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
  }

  .dir {
    width: 15.3rem;
  }

  .cb {
    max-width: 50%;
  }

  .bPagoE {
    margin: auto;
    margin-bottom: 5px;
    width: 250px;
  }

  .bPagoP {
    margin: auto;
  }

  .eButton {
    font-weight: bold;
  }
`;

export const Cart = () => {
  const [perfil, setPerfil] = useState();
  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    fetchUserData().then((response) => {
      setPerfil(response.data);
      setLoading(false);
    });
  }, []);

  const {
    isEmpty,
    totalUniqueItems,
    items,
    totalItems,
    cartTotal,
    updateItemQuantity,
    removeItem,
    emptyCart,
  } = useCart();
  if (isEmpty)
    return (
      <Styles>
        <h1 className="text-center mt-5">El carrito esta vacio</h1>
      </Styles>
    );

  if (isLoading) {
    return <Loading />;
  }

  const onEfectivo = (e) => {
    e.preventDefault();
    console.log("efectivo");
  };

  const onPaypal = (e) => {
    e.preventDefault();
    console.log("paypal");
  };

  return (
    <React.Fragment>
      <Layout>
        <Styles>
          {/* esto es la lista de items */}
          <div className="row">
            <div className="column items">
              <table className="table table-light table-hover m-1">
                <tbody>
                  {items.map((item, index) => {
                    return (
                      <tr key={index}>
                        <td>
                          <img src={item.img} alt="productimg" width="100" />
                        </td>
                        <td>{item.title}</td>
                        <td>Cantidad: {item.quantity}</td>
                        <td>Precio Total: {item.price * item.quantity}</td>
                        <td>
                          <button
                            className="btn btn-info ms-2"
                            onClick={() =>
                              updateItemQuantity(item.id, item.quantity - 1)
                            }
                          >
                            <IoBagRemoveSharp color="white" size="1.5rem" />
                          </button>
                          <button
                            className="btn btn-info ms-2"
                            onClick={() =>
                              updateItemQuantity(item.id, item.quantity + 1)
                            }
                          >
                            <IoBagAddSharp color="white" size="1.5rem" />
                          </button>
                          <button
                            className="btn btn-danger ms-2"
                            onClick={() => removeItem(item.id)}
                          >
                            <MdDeleteForever color="white" size="1.5rem" />
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
              <div className="row ultima">
                <button className="btn btn-danger" onClick={() => emptyCart()}>
                  Limpiar carrito
                </button>
              </div>
            </div>
            <div className="column compra m-3">
              <div className="card">
                <h2>Realizar pedido</h2>
                <br />
                <h5 className="mb-2">Dirección del envio</h5>
                <Form.Select
                  aria-label="Default select example"
                  id="direcciones"
                  required
                >
                  {perfil.direcciones.map((direccion, index) => {
                    return (
                      <option className="dir" value={direccion}>
                        {direccion.calle + " " + direccion.numero}
                      </option>
                    );
                  })}
                </Form.Select>
                <br />

                <label className="mb-2">Total de items: {totalItems}</label>
                <h4>Finalizar Compra</h4>
                <h5 className="mb-4">Precio final: $ {cartTotal}</h5>
                <div className="row bPagoE">
                  <Button
                    className="eButton"
                    variant="success"
                    onClick={onEfectivo}
                    value="efectivo"
                  >
                    Efectivo <MdOutlinePayments size="1.5rem" color="white" />
                  </Button>
                </div>
                <div className="row bPagoP">
                  <PaypalCheckoutButton
                    order={{
                      customer: perfil.nombre,
                      total: cartTotal,
                      items: items,
                    }}
                  />
                  {/* <Button variant="primary" className="ppb" onClick={onPaypal} value="paypal">
                    </Button> */}
                </div>
              </div>
            </div>
          </div>
        </Styles>
      </Layout>
    </React.Fragment>
  );
};

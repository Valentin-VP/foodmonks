import React, { useState, useEffect } from "react";
import { Form, Button } from "react-bootstrap";
import { useCart } from "react-use-cart";
import { fetchUserData, paypalEnviarCART } from "../../services/Requests";
import styled from "styled-components";
import { Layout } from "../../components/Layout";
import { Loading } from "../../components/Loading";
import { IoBagAddSharp, IoBagRemoveSharp } from "react-icons/io5";
import { MdDeleteForever, MdOutlinePayments } from "react-icons/md";
import PaypalCheckoutButton from "../../components/PaypalCheckoutButton";
import { hacerPedidoEfectivo } from "../../services/Requests";
import { NotiError, Noti } from "../../components/Notification";

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
    border-radius: 5px;
    object-fit: cover;
    height: 6rem;
    width: 7rem;
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
    var menus = [];
    items.forEach(menu => {
      const aux = {
        id: menu.id,
        cantidad: menu.quantity, 
      }
      menus.push(aux);
    });
    const jsonPedido = {
      restaurante: sessionStorage.getItem("restauranteCart"), //falta arreglar el restaurante
      direccionId: document.getElementById("direcciones").value, // Long: direccion seleccionada del cliente, ya se cuenta con las direcciones del cliente en el front, entiendo se podría enviar solamente el ID
      medioPago: "EFECTIVO", //String: vale 'PayPal' o 'Efectivo'
      total: Math.round((cartTotal + Number.EPSILON) * 100) / 100,
      ordenId: "", // Lo que la API de PayPal nuestra responde al front desde el CU PAgar con PayPal. vacío si el pago fue en efectivo: ''
      linkAprobacion: "", // URL que la API de PayPal nuestra responde al front desde el CU PAgar con PayPal. vacío si el pago fue en efectivo: ''
      menus: menus
    };
    console.log(jsonPedido);
    hacerPedidoEfectivo(jsonPedido).then((response) =>{
      console.log(response);
      Noti("Pedido realizado con exito");
      emptyCart();
    }).catch((error) => {
      NotiError(error.response.data);
    });
  };

  const onPaypal = (cartId) => { //Copiado del efectivo + lo que corresponde a paypal
    //e.preventDefault();
    var menus = [];
    items.forEach(menu => {
      const aux = {
        id: menu.id,
        cantidad: menu.quantity, 
      }
      menus.push(aux);
    });
    const jsonPedido = {
      restaurante: sessionStorage.getItem("restauranteCart"), // Seria el correo del restaurante (esto es del cliente)
      direccionId: document.getElementById("direcciones").value,
      medioPago: "PAYPAL",
      total: cartTotal,
      ordenId: cartId,
      linkAprobacion: "",
      menus: menus
    };
    console.log(jsonPedido);
    paypalEnviarCART(jsonPedido).then((response) =>{
      console.log(response);
      Noti("Pedido realizado con exito");
      emptyCart();
    }).catch((error) => {
      NotiError(error.response.data);
    });
  };

  const getOrder = () => {
    let order = { customer: perfil.nombre, total: Math.round((cartTotal + Number.EPSILON) * 100) / 100
    };
    const orderItems = items.map((item) => {
      return {
        name: `${item.nombre + " (subtotal $" + item.price * item.quantity + ")"}`,
        price: Math.round((item.price + Number.EPSILON) * 100) / 100
        ,
        quantity: item.quantity,
        currency: "USD",
      };
    });
    order = { ...order, items: orderItems };
    //console.log(order);
    return order;
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
                          <img src={item.imagen} alt="productimg" />
                        </td>
                        <td className="font-weight-bold">{item.nombre}</td>
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
                      <option key={index} className="dir" value={direccion.id}>
                        {direccion.calle + " " + direccion.numero}
                      </option>
                    );
                  })}
                </Form.Select>
                <br />

                <label className="mb-2">Total de items: {totalItems}</label>
                <h4>Finalizar Compra</h4>
                <h5 className="mb-4">Precio final: $ {Math.round((cartTotal + Number.EPSILON) * 100) / 100}</h5>
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
                  <PaypalCheckoutButton order={getOrder()} onAuthorizeCallback={onPaypal} />
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

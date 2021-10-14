import React from "react";
import { useCart } from "react-use-cart";
import styled from "styled-components";
import { Layout } from "../../components/Layout";

const Styles = styled.div`
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
    $:active {
      background-color: black !important;
    }
  }
  .btn-danger {
  }
  table,
  tbody,
  tr,
  td {
    background-color: white;
  }
`;

export const Cart = () => {
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
        <h1 className="text-center">El carrito esta vacio</h1>
      </Styles>
    );

  return (
    <React.Fragment>
      <Layout>
        <Styles>
          <div className="row justify-content-center">
            <div className="col-12">
              <h3>
                Menús: {totalUniqueItems} Total de items: {totalItems}
              </h3>
              <table className="table table-light table-hover m-0">
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
                            -
                          </button>
                          <button
                            className="btn btn-info ms-2"
                            onClick={() =>
                              updateItemQuantity(item.id, item.quantity + 1)
                            }
                          >
                            +
                          </button>
                          <button
                            className="btn btn-danger ms-2"
                            onClick={() => removeItem(item.id)}
                          >
                            Eliminar
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
            <div className="col-auto ms-auto">
              <h2>Total: $ {cartTotal}</h2>
            </div>
            <div className="col-auto">
              <button
                className="btn btn-danger m-2"
                onClick={() => emptyCart()}
              >
                Limpiar carrito
              </button>
            </div>
          </div>
        </Styles>
      </Layout>
    </React.Fragment>
  );
};

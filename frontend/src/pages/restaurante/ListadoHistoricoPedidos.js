import React, { useEffect, useRef, useState } from "react";
import styled from "styled-components";
import { Col } from "react-bootstrap";

const Styles = styled.div`
  .lista {
    padding-top: 35px;
  }
  .form-floating {
    margin-bottom: 15px;
  }
  h1 {
    color: #e88121;
    font-weight: bold;
    text-align: center;
    font-size: 30px;
    font-family: "Poppins", sans-serif;
  }
  table {
    background-color: #ffffff;
    text-align: center;
    font-family: "Poppins", sans-serif;
    border-collapse: collapse;
    border: 3px solid #fefefe;
    width: 100%;
  }

  td,
  tr {
    border: 1px solid #eee;
    padding: 6px;
    width: 8%;
    &:hover {
      background-color: #fffff5;
    }
  }

  #itemId {
    font-weight: lighter;
    font-size: 18px;
    &:hover {
      background-color: #fffff1;
    }
  }

  .row,
  .col {
    padding: 1px;
  }

  img {
    height: 6rem;
    border-radius: 5px;
  }

  .text-center {
    position: relative;
  }

  button {
    color: white;
    background-color: #e87121;
    border: none;
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
      background-color: #e87121;
    }
    &:hover {
      background-color: #da6416;
    }
    &:active {
      background-color: #e87121;
    }
  }
`;

export default function ListadoHistoricoPedidos({ datos, onVisible }) {
  console.log(datos);
  return (
    <>
      <Styles>
        <div className="container-lg">
          <main className="lista">
            <h1 className="text-center h5 mb-3 fw-normal">Pedidos Recibidos</h1>
            <div className="form-floating">
              <div className="table-responsive justify-content-center">
                <table className="table table-hover">
                  <tbody>
                    {datos.pedidos
                      ? datos.pedidos.map((item) => {
                          return (
                            <>
                              <Col>
                                <tr key={item.id}>
                                  <td id="itemId">ID Pedido: {item.id}</td>
                                  <td>Dirección: {item.direccion}</td>
                                  <td>Cliente: {item.nombreApellidoCliente}</td>
                                  <td>Medio de Pago: {item.medioPago}</td>
                                  <td>Estado: {item.estadoPedido}</td>
                                  <td>
                                    Fecha Entrega: {item.fechaHoraEntrega}
                                  </td>
                                  <td>
                                    Calificación:{" "}
                                    {item.calificacionRestaurante !== ""
                                      ? item.calificacionRestaurante
                                      : "Sin Calificar"}
                                  </td>
                                  <td>Total: ${item.total}</td>
                                  <td>
                                    {
                                      <button
                                        className="btn btn-sm btn-secondary"
                                        type="button"
                                        onClick={(e) => onVisible(item.id)}
                                      >
                                        +
                                      </button>
                                    }
                                  </td>
                                </tr>
                              </Col>
                              {item.visible && (
                                <Col>
                                  {item.menus
                                    ? item.menus.map((menu, menuindex) => {
                                        return (
                                          <>
                                            <tr key={menuindex}>
                                              <td>
                                                <img
                                                  src={menu.imagen}
                                                  alt="productimg"
                                                  width="150"
                                                  hight="150"
                                                />
                                              </td>
                                              <td>Menú: {menu.menu}</td>
                                              <td>Precio: ${menu.precio}</td>
                                              <td>
                                                Descuento:{" "}
                                                {menu.multiplicadorPromocion} %
                                              </td>
                                              <td>
                                                Total Parcial: ${menu.calculado}
                                              </td>
                                            </tr>
                                          </>
                                        );
                                      })
                                    : null}
                                </Col>
                              )}
                            </>
                          );
                        })
                      : null}
                  </tbody>
                </table>
              </div>
            </div>
          </main>
        </div>
      </Styles>
    </>
  );
}

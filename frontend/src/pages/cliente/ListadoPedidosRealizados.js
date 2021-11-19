import React, { useEffect, useRef, useState } from "react";
import styled from "styled-components";
import { Col } from "react-bootstrap";
import Pagination from "@material-ui/lab/Pagination";

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
    table-layout: fixed;
  }

  td,
  tr {
    border: 1px solid #eee;
    padding: 2px;
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

export default function ListadoPedidosRealizados({
  datos,
  onVisibleMenu,
  onVisibleReclamo,
}) {
  const onReclamar = (item) => {
    sessionStorage.setItem("pedidoId", item);
    window.location.replace("/reclamo");
  };

  return (
    <>
      <Styles>
        <div className="container-lg">
          <main className="lista">
            <h1 className="text-center h5 mb-3 fw-normal">
              Pedidos Realizados
            </h1>
            <div className="form-floating">
              <div className="table-responsive justify-content-center">
                <table className="table table-hover">
                  <tbody>
                    {datos && datos.pedidos
                      ? datos.pedidos.map((item) => {
                          console.log(item);
                          return (
                            <>
                              <Col>
                                <tr key={item.id}>
                                  <td id="itemId">ID Pedido: {item.id}</td>
                                  <td>Dirección: {item.direccion}</td>
                                  <td>Restaurante: {item.nombreRestaurante}</td>
                                  <td>Medio de Pago: {item.medioPago}</td>
                                  <td>Estado: {item.estadoPedido}</td>
                                  <td>
                                    Fecha Entrega: {item.fechaHoraEntrega}
                                  </td>
                                  <td>
                                    Calificación:{" "}
                                    {item.calificacionCliente !== ""
                                      ? item.calificacionCliente
                                      : "Sin Calificar"}
                                  </td>
                                  <td>Total: ${item.total}</td>
                                  <td>
                                    {
                                      <button
                                        className="btn btn-sm btn-secondary"
                                        type="button"
                                        onClick={(e) => onVisibleMenu(item.id)}
                                      >
                                        Menús
                                      </button>
                                    }
                                  </td>
                                  <td>
                                    {item.reclamo && item.reclamo.id ? (
                                      <button
                                        className="btn btn-sm btn-secondary"
                                        type="button"
                                        onClick={(e) =>
                                          onVisibleReclamo(item.id)
                                        }
                                      >
                                        Ver Reclamo
                                      </button>
                                    ) : (
                                      <button
                                        className="btn btn-sm btn-secondary"
                                        type="button"
                                        disabled={
                                          item.estadoPedido !== "Finalizado"
                                        }
                                        onClick={(e) => onReclamar(item.id)}
                                      >
                                        Reclamar
                                      </button>
                                    )}
                                  </td>
                                </tr>
                              </Col>
                              {item.visibleMenu && (
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
                                              <td>
                                                Menú: {menu.menu} (x
                                                {menu.cantidad})
                                              </td>
                                              <td>
                                                Precio Unitario: ${menu.precio}
                                              </td>
                                              <td>
                                                Descuento:{" "}
                                                {menu.multiplicadorPromocion} %
                                              </td>
                                              <td>
                                                Total Parcial: $
                                                {menu.precioPorCantidad}
                                              </td>
                                              <td>
                                                Total Final: ${menu.calculado}
                                              </td>
                                            </tr>
                                          </>
                                        );
                                      })
                                    : null}
                                </Col>
                              )}
                              {item.visibleReclamo &&
                                (item.reclamo && item.reclamo.id ? (
                                  <Col>
                                    <tr key={item.reclamo.id}>
                                      <td>Razón: {item.reclamo.razon}</td>
                                      <td>
                                        Comentario: {item.reclamo.comentario}
                                      </td>
                                      <td>
                                        Fecha Reclamo: {item.reclamo.fecha}
                                      </td>
                                    </tr>
                                  </Col>
                                ) : null)}
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

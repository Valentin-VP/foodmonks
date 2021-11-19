import React, { useEffect, useRef, useState } from "react";
import styled from "styled-components";
import { Col, Button, Form, Alert } from "react-bootstrap";
import Pagination from "@material-ui/lab/Pagination";
import Modal, { ModalProvider } from "styled-react-modal";
import { Rating } from "react-simple-star-rating";
import {
  calificarRestaurante,
  modificarCalificacionRestaurante,
  eliminarCalificacionRestaurante,
} from "../../services/Requests";
import { NotiError } from "../../components/Notification";

const StyledModal = Modal.styled`
  border-radius: 5px;
  padding: 1.5%;
  width: 25%;
  align-items: center;
  justify-content: center;
  background-color: white;
  overflow-y:inherit !important;

  .cuerpo{
    text-align: center;
  }

  .abajo{
    text-align: right;
  }
`;

const Styles = styled.div`
  .lista {
    margin-top: 35px;
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
    border-radius: 10px;
  }

  td,
  tr {
    border: 2px solid grey;
    padding: 0px 0.5rem 0px 0.5rem;
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
    border-radius: 3px;
    object-fit: cover;
    border-color: grey;
  }

  .text-center {
    position: relative;
  }

  .oButton {
    color: white;
    background-color: #e87121;
    border: none;
    height: 2.38rem;
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

  #aceptarB {
    background-color: #e87121;
  }

  .clickeable {
    color: blue;
    background: transparent;
    border: none;
    color: white;
    background-color: #e87121;
    border: none;
    margin-bottom: 0.1rem;
    border-radius: 7px;
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
    // Mandar datos a pagina de reclamos (e ir a dicha pagina)
  };
  const [tipoAccion, setAccion] = useState();
  const [error, setError] = useState(null);
  const [pedido, setPedido] = useState();
  const [rating, setRating] = useState(0); // valor inicial de la calificacion
  // para el modal -----------------------------------------------------------------------------------------------
  const [isOpen, setIsOpen] = useState(false);

  const toggleModal = () => {
    setIsOpen(!isOpen);
    setError(null);
  };
  //termina para el modale ---------------------------------------------------------------------------------------

  const handleRating = (rate) => {
    setRating(rate);
    // Some logic
  };

  const crearCalificacion = (item, accion) => {
    setPedido(item);
    setAccion(accion);
    // if(!item.calificacionRestaurante){
    //   //aca iria el antiguo valor de la calificacion
    // }
    toggleModal();
  };

  const calificar = (e) => {
    e.preventDefault();
    if (rating === 0) {
      setError(
        <Alert variant="danger">La calificación no puede estar vacia</Alert>
      );
      return null;
    }
    const data = {
      idPedido: pedido.id,
      puntaje: rating,
      comentario: document.getElementById("comentario").value,
    };
    if (tipoAccion === "CALIFICAR") {
      calificarRestaurante(data)
        .then(() => {
          window.location.replace("listarPedidos");
        })
        .catch((error) => {
          NotiError(error.response.data);
        });
    } else {
      modificarCalificacionRestaurante(data)
        .then(() => {
          window.location.replace("listarPedidos");
        })
        .catch((error) => {
          NotiError(error.response.data);
        });
    }
  };

  const eliminarCalificacion = (item) => {
    console.log(item);
    eliminarCalificacionRestaurante(item.id).catch((error) => {
      NotiError(error.response.data);
    });
    // window.location.replace("listarPedidos");
  };

  return (
    <Styles>
      <ModalProvider>
        <div className="container-lg">
          <main className="lista">
            <h1 className="text-center h5 mb-3 fw-normal">
              Pedidos Realizados
            </h1>
            <div className="table-responsive">
              <table className="table table-bordered table-striped">
                <tbody>
                  {datos && datos.pedidos
                    ? datos.pedidos.map((item, index) => {
                        return (
                          <div key={index}>
                            <Col>
                              <thead>
                                <tr>
                                  <th scope="col">ID Pedido</th>
                                  <th scope="col">Dirección</th>
                                  <th scope="col">Restaurante</th>
                                  <th scope="col">M. de Pago</th>
                                  <th scope="col">Estado</th>
                                  <th scope="col">F. Entrega</th>
                                  <th scope="col">Total</th>
                                  <th scope="col">Menús</th>
                                  <th scope="col">Reclamo</th>
                                  <th scope="col">Calificación</th>
                                </tr>
                              </thead>
                              <tr key={item.id}>
                                <td id="itemId">{item.id}</td>
                                <td>{item.direccion}</td>
                                <td>{item.nombreRestaurante}</td>
                                <td>{item.medioPago}</td>
                                <td>{item.estadoPedido}</td>
                                <td>{item.fechaHoraEntrega}</td>
                                <td>${item.total}</td>
                                <td>
                                  {
                                    <button
                                      className="clickeable"
                                      type="button"
                                      onClick={(e) => onVisibleMenu(item.id)}
                                    >
                                      Ver
                                    </button>
                                  }
                                </td>
                                <td>
                                  {item.reclamo && item.reclamo.id ? (
                                    <button
                                      className="clickeable"
                                      type="button"
                                      onClick={(e) => onVisibleReclamo(item.id)}
                                    >
                                      Ver Reclamo
                                    </button>
                                  ) : (
                                    <button
                                      className="clickeable"
                                      type="button"
                                      onClick={(e) => onReclamar(item.id)}
                                    >
                                      Reclamar
                                    </button>
                                  )}
                                </td>
                                <td>
                                  {item.calificacionRestaurante == false ? (
                                    <button
                                      type="button"
                                      className="clickeable"
                                      onClick={() =>
                                        crearCalificacion(item, "CALIFICAR")
                                      }
                                    >
                                      Calificar
                                    </button>
                                  ) : (
                                    <div>
                                      <button
                                        className="clickeable"
                                        variant="secondary"
                                        type="button"
                                        onClick={() =>
                                          crearCalificacion(item, "MODIFICAR")
                                        }
                                      >
                                        Modificar
                                      </button>
                                      <br />
                                      <Button
                                        className=" mt-2 p-0"
                                        variant="danger"
                                        type="button"
                                        onClick={() =>
                                          eliminarCalificacion(item)
                                        }
                                      >
                                        Eliminar
                                      </Button>
                                    </div>
                                  )}
                                </td>
                              </tr>
                            </Col>
                            {item.visibleMenu && item.menus ? (
                              <Col>
                                {item.menus.map((menu, menuIndex) => {
                                  return (
                                    <tbody key={menuIndex}>
                                      <tr>
                                        <td>
                                          <img
                                            className="m-1"
                                            src={menu.imagen}
                                            alt="productimg"
                                            border="2"
                                            width="75"
                                            height="75"
                                          />
                                        </td>
                                        <td>
                                          Menú: {menu.menu} (x
                                          {menu.cantidad})
                                        </td>
                                        <td>Precio Unitario: ${menu.precio}</td>
                                        <td>
                                          Descuento:{" "}
                                          {menu.multiplicadorPromocion} %
                                        </td>
                                        <td>
                                          Total Parcial: $
                                          {menu.precioPorCantidad}
                                        </td>
                                        <td>Total Final: ${menu.calculado}</td>
                                      </tr>
                                    </tbody>
                                  );
                                })}
                              </Col>
                            ) : null}
                            {item.visibleReclamo &&
                              (item.reclamo && item.reclamo.id ? (
                                <Col key={item.reclamo.id}>
                                  <tr>
                                    <td>Razón: {item.reclamo.razon}</td>
                                    <td>
                                      Comentario: {item.reclamo.comentario}
                                    </td>
                                    <td>Fecha Reclamo: {item.reclamo.fecha}</td>
                                  </tr>
                                </Col>
                              ) : null)}
                          </div>
                        );
                      })
                    : null}
                </tbody>
              </table>
            </div>
          </main>
        </div>

        <StyledModal
          isOpen={isOpen}
          onBackgroundClick={toggleModal}
          onEscapeKeydown={toggleModal}
        >
          <h2>Calificar Restaurante</h2>
          <hr />
          <Form>
            <div className="cuerpo">
              <Rating onClick={handleRating} ratingValue={rating} size="50" />
              <div className="form-floating">
                <input
                  className="form-control mb-2"
                  type="text"
                  name="comentario"
                  id="comentario"
                  placeholder="comentario"
                  required
                />
                <label htmlFor="floatingInput">Comentario</label>
              </div>
            </div>
            {error}
            <div className="abajo">
              <Button
                className="oButton"
                variant="secondary"
                type="submit"
                onClick={calificar}
              >
                Aceptar
              </Button>{" "}
              <Button variant="secondary" onClick={toggleModal}>
                Cancelar
              </Button>
            </div>
          </Form>
        </StyledModal>
      </ModalProvider>
    </Styles>
  );
}

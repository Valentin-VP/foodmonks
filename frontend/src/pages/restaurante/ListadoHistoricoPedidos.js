import React, { useEffect, useRef, useState } from "react";
import styled from "styled-components";
import { Col, Button, Alert, Form, InputGroup } from "react-bootstrap";
import { NotiError } from "../../components/Notification";
import {
  calificarCliente,
  modificarCalificacionCliente,
  eliminarCalificacionCliente,
} from "../../services/Requests";
import Modal, { ModalProvider } from "styled-react-modal";
import { Rating } from "react-simple-star-rating";

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
    border: 1px solid grey;
    padding: 6px;
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

  .calificaciones {
    margin-right: 0.2rem;
    margin-left: 0.2rem;
    Button {
      padding: 0.1rem;
      padding-right: 0.5rem;
      padding-left: 0.5rem;
    }
    .modificar {
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
  }
`;

export default function ListadoHistoricoPedidos({ datos, onVisible }) {
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
    if (item.calificacionCliente === "false") {
      setRating(0);
    } else {
      setRating(item.calificacionCliente);
    }
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
      calificarCliente(data)
        .then(() => {
          window.location.replace("historico");
        })
        .catch((error) => {
          NotiError(error.response.data);
        });
    } else {
      modificarCalificacionCliente(data)
        .then(() => {
          window.location.replace("historico");
        })
        .catch((error) => {
          NotiError(error.response.data);
        });
    }
  };

  const eliminarCalificacion = (item) => {
    console.log(item);
    eliminarCalificacionCliente(item.id)
      .then(() => {
        window.location.replace("historico");
      })
      .catch((error) => {
        NotiError(error.response.data);
      });
  };

  return (
    <Styles>
      <ModalProvider>
        <div className="container-lg">
          <main className="lista">
            <h1 className="text-center h5 mb-3 fw-normal">Pedidos Recibidos</h1>
            <div className="form-floating">
              <div className="table-responsive justify-content-center">
                <table className="table table-hover">
                  <tbody>
                    {datos.pedidos
                      ? datos.pedidos.map((item, index) => {
                          return (
                            <div key={index}>
                              <Col>
                                <thead>
                                  <tr>
                                    <th scope="col">ID Pedido</th>
                                    <th scope="col">Dirección</th>
                                    <th scope="col">Cliente</th>
                                    <th scope="col">M. de Pago</th>
                                    <th scope="col">Estado</th>
                                    <th scope="col">F. Entrega</th>
                                    <th scope="col">Total</th>
                                    <th scope="col">Menús</th>
                                    <th scope="col">Calificación</th>
                                  </tr>
                                </thead>
                                <tr>
                                  <td id="itemId">{item.id}</td>
                                  <td>{item.direccion}</td>
                                  <td>{item.nombreApellidoCliente}</td>
                                  <td>{item.medioPago}</td>
                                  <td>{item.estadoPedido}</td>
                                  <td>{item.fechaHoraEntrega}</td>
                                  <td>${item.total}</td>
                                  <td>
                                    {
                                      <button
                                        className="clickeable"
                                        type="button"
                                        onClick={(e) => onVisible(item.id)}
                                      >
                                        ver
                                      </button>
                                    }
                                  </td>
                                  {item.calificacionCliente === "false" ? (
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
                                    <InputGroup className="calificaciones">
                                      <Button
                                        className="modificar"
                                        variant="secondary"
                                        type="button"
                                        onClick={() =>
                                          crearCalificacion(item, "MODIFICAR")
                                        }
                                      >
                                        Modificar
                                      </Button>
                                      <br />
                                      <Button
                                        variant="danger"
                                        type="button"
                                        onClick={() =>
                                          eliminarCalificacion(item)
                                        }
                                      >
                                        Eliminar
                                      </Button>
                                    </InputGroup>
                                  )}
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
                            </div>
                          );
                        })
                      : null}
                  </tbody>
                </table>
              </div>
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

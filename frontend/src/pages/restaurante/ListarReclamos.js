import React, { useState } from "react";
import styled from "styled-components";
import Modal, { ModalProvider } from "styled-react-modal";
import { fetchPedidoFromReclamo } from "../../services/Requests";
import { Noti, NotiError } from "../../components/Notification";
import { Button } from "react-bootstrap";
import { realizarDevolucion } from "../../services/Requests";

const StyledModal = Modal.styled`
  * {
    margin: 0;
    box-sizing: border-box;
  }

  border-radius: 5px;
  padding: 1.5%;
  width: 25%;
  align-items: center;
  justify-content: center;
  background-color: white;
  overflow-y:inherit !important;

  .cuerpo{
    margin-bottom: 15px;
  }
  .abajo{
    text-align: right;
  }
  Button {
    margin-left: 5px;
  }
  #inputRechazar {
    margin-bottom: 10px;
    width: 100%;
    height: 150px;
    border-radius: 5px;
    resize: none;
  }
`;

const Styles = styled.div`
  * {
    margin: 0;
    box-sizing: border-box;
  }

  h1 {
    text-align: center;
  }
  table {
    background-color: #ffffff;
  }
`;

function ListarReclamos({ reclamos }) {
  const [pedido, setPedido] = useState();
  const [loading, isLoading] = useState(true);
  const [isOpen, setIsOpen] = useState(false);
  const [isAceptar, setIsAceptar] = useState(false);
  const [isRechazar, setIsRechazar] = useState(false);
  const [comentario, setComentario] = useState({
    motivoDevolucion: "",
  });

  const toggleModal = (e) => {
    setIsOpen(!isOpen);
  };

  const toggleModalAceptar = (e) => {
    setIsAceptar(!isAceptar);
  };

  const toggleModalRechazar = (e) => {
    setIsRechazar(!isRechazar);
  };

  const obtenerPedido = (id) => {
    fetchPedidoFromReclamo(id)
      .then((response) => {
        if (response.status === 200) {
          console.log(response.data);
          setPedido(response.data);
          isLoading(false);
        } else {
          Noti(response.data);
        }
      })
      .catch((error) => {
        Noti(error.message);
      });
  };

  const handleChangeRechazar = (e) => {
    e.persist();
    setComentario((comentario) => ({
      ...comentario,
      [e.target.name]: e.target.value,
    }));
  };

  const aceptarReclamo = () => {
    realizarDevolucion(pedido.id, true, comentario)
      .then((response) => {
        console.log(response);
        Noti("El reclamo fue aceptado con éxito");
      })
      .catch((error) => {
        NotiError(error.response.data);
      });
  };

  const rechazarReclamo = () => {
    realizarDevolucion(pedido.id, false, comentario)
      .then((response) => {
        console.log(response);
        Noti("El reclamo fue rechazado con éxito");
      })
      .catch((error) => {
        NotiError(error.response.data);
      });
  };

  return (
    <>
      <Styles>
        <ModalProvider>
          <div className="table-responsive justify-content-center" id="list">
            <table className="table table-hover">
              <tbody>
                {reclamos.map((reclamo) => {
                  return (
                    <>
                      <br />
                      <tr key={reclamo.id}>
                        <td>PEDIDO: {reclamo.idPedido}</td>
                        <th scope="col" />
                        <td>RAZON: {reclamo.razon}</td>
                        <th scope="col" />
                        <td>FECHA: {reclamo.fecha}</td>
                        <th scope="col" />
                        <td>COMENTARIO: {reclamo.comentario}</td>
                        <td>
                          {
                            <button
                              className="btn btn-sm btn-secondary"
                              type="button"
                              onClick={(e) => {
                                obtenerPedido(reclamo.idPedido);
                                toggleModal();
                              }}
                            >
                              INFO PEDIDO
                            </button>
                          }
                        </td>
                        <th scope="col" />
                        <td>
                          {
                            <button
                              className="btn btn-sm btn-secondary"
                              type="button"
                              disabled={(e) => {
                                obtenerPedido(reclamo.idPedido);
                                if (pedido.estado === "DEVUELTO") {
                                  NotiError("El pedido fue devuelto");
                                  return true;
                                } else if (
                                  pedido.estado === "RECLAMORECHAZADO"
                                ) {
                                  NotiError("El pedido Rechazado");
                                  return true;
                                } else {
                                  return false;
                                }
                              }}
                              onClick={(e) => {
                                obtenerPedido(reclamo.idPedido);
                                toggleModalAceptar();
                              }}
                            >
                              ACEPTAR RECLAMO
                            </button>
                          }
                        </td>
                        <th scope="col" />
                        <td>
                          {
                            <button
                              className="btn btn-sm btn-secondary"
                              type="button"
                              disabled={(e) => {
                                obtenerPedido(reclamo.idPedido);
                                if (pedido.estado === "DEVUELTO") {
                                  NotiError("El pedido fue devuelto");
                                  return true;
                                } else if (
                                  pedido.estado === "RECLAMORECHAZADO"
                                ) {
                                  NotiError("El pedido Rechazado");
                                  return true;
                                } else {
                                  return false;
                                }
                              }}
                              onClick={(e) => {
                                obtenerPedido(reclamo.idPedido);
                                toggleModalRechazar();
                              }}
                            >
                              RECHAZAR RECLAMO
                            </button>
                          }
                        </td>
                        <th scope="col" />
                      </tr>
                      <br />
                    </>
                  );
                })}
              </tbody>
            </table>
          </div>
          {!loading ? (
            <StyledModal
              isOpen={isOpen}
              onBackgroundClick={toggleModal}
              onEscapeKeydown={toggleModal}
              pedido={pedido}
            >
              <h2>Información Pedido</h2>
              <hr />
              <div className="cuerpo">
                <p>Precio: {pedido.total}$</p>
                <p>Medio de Pago: {pedido.medioPago}</p>
                <p>Restaurante: {pedido.nombreRestaurante}</p>
                <p>Cliente: {pedido.nombreApellidoCliente}</p>
              </div>
              <div className="abajo">
                <Button variant="secondary" onClick={toggleModal}>
                  Ok
                </Button>
              </div>
            </StyledModal>
          ) : null}
          <StyledModal
            isOpen={isAceptar}
            onBackgroundClick={toggleModalAceptar}
            onEscapeKeydown={toggleModalAceptar}
          >
            <h2>Aceptar Reclamo</h2>
            <hr />
            <div className="cuerpo">
              <span>
                ¿Seguro que desea aceptar el reclamo y realizar la devolución?
              </span>
            </div>
            <div className="abajo">
              <Button variant="danger" onClick={aceptarReclamo}>
                Aceptar
              </Button>
              <Button variant="secondary" onClick={toggleModalAceptar}>
                Cancelar
              </Button>
            </div>
          </StyledModal>
          <StyledModal
            isOpen={isRechazar}
            onBackgroundClick={toggleModalRechazar}
            onEscapeKeydown={toggleModalRechazar}
          >
            <h2>Rechazar Reclamo</h2>
            <hr />
            <div className="cuerpo">
              <br />
              <span> ¿Cual es la razon de el rechazo? </span>
              <textarea
                id="inputRechazar"
                name="motivo"
                onChange={handleChangeRechazar}
                required
              />
            </div>
            <div className="abajo">
              <Button variant="danger" onClick={rechazarReclamo}>
                Rechazar
              </Button>
              <Button variant="secondary" onClick={toggleModalRechazar}>
                Cancelar
              </Button>
            </div>
          </StyledModal>
        </ModalProvider>
      </Styles>
    </>
  );
}

export default ListarReclamos;

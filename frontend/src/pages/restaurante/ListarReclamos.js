import React, { useState } from "react";
import styled from "styled-components";
import Modal, { ModalProvider } from "styled-react-modal";
import { fetchPedidoFromReclamo } from "../../services/Requests";
import { Noti } from "../../components/Notification";
import { Button } from "react-bootstrap";

const StyledModal = Modal.styled`
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
    type="text"
    border-radius: 5px;
  }
  #inputAceptar {
    type="text"
    border-radius: 5px;
  }
`;

const Styles = styled.div`
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

  const aceptarReclamo = () => {};

  const rechazarReclamo = () => {};

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
                                obtenerPedido(reclamo.pedidoId);
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
                              onClick={(e) => toggleModalAceptar()}
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
                              onClick={(e) => toggleModalRechazar()}
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
            >
              <h2>Información Pedido</h2>
              <hr />
              <div className="cuerpo">
                <p>Precio: {pedido.precio}</p>
                <p>Descripcion: {pedido.descripcion}</p>
                <p>Cantidad: {pedido.cantidad}</p>
                <p>Categoria: {pedido.categoria}</p>
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
              <input id="inputAceptar" />
              {/* input para mensaje del restaurante*/}
            </div>
            <div className="abajo">
              <Button variant="danger" onClick={aceptarReclamo}>
                Aceptar
              </Button>
              <Button variant="secondary" onClick={toggleModal}>
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
              <input id="inputRechazar" />
              {/* input para mensaje del restaurante*/}
            </div>
            <div className="abajo">
              <Button variant="danger" onClick={rechazarReclamo}>
                Rechazar
              </Button>
              <Button variant="secondary" onClick={toggleModal}>
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

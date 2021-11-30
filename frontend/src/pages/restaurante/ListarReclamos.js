import React, { useState, useEffect } from "react";
import styled from "styled-components";
import Modal, { ModalProvider } from "styled-react-modal";
import { fetchPedidoFromReclamo } from "../../services/Requests";
import { Noti, NotiError } from "../../components/Notification";
import { Button } from "react-bootstrap";
import { realizarDevolucion, fetchReclamos } from "../../services/Requests";
import { Loading } from "../../components/Loading";

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
  #scroll {
    background: "transparent";
    height: 6rem;
  }
`;

const perPage = 8;
const types = {
  start: "START",
  loaded: "LOADED",
};

const reducer = (state, action) => {
  switch (action.type) {
    case types.start:
      return { ...state, loading: true };
    case types.loaded:
      return {
        ...state,
        loading: false,
        data: [...state.data, ...action.newData],
        more: action.newData.length === perPage,
        after: state.after + action.newData.length,
      };
    default:
      throw new Error("Don't understand action");
  }
};

const MyContext = React.createContext();

function MyProvider({ children }) {
  const [reclamos, setReclamos] = useState([]);
  const [cargando, isCargando] = useState(true);
  const values = {
    razon: "",
    cliente: "",
    ordenar: false,
  };

  useEffect(() => {
    values.razon = sessionStorage.getItem("reclamos-razon");
    values.cliente = sessionStorage.getItem("reclamos-cliente");
    values.ordenar = sessionStorage.getItem("reclamos-ordenar");
    if (values.razon === null) {
      values.razon = "";
    }
    if (values.cliente === null) {
      values.cliente = "";
    }
    if (values.ordenar === null) {
      values.ordenar = false;
    }
    fetch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetch = () => {
    fetchReclamos(values)
      .then((response) => {
        if (response.status === 200) {
          console.log(response.data);
          setReclamos(response.data);
          isCargando(false);
        } else {
          Noti(response.data);
        }
      })
      .catch((error) => {
        Noti(error.message);
      });
  };

  const [state, dispatch] = React.useReducer(reducer, {
    loading: false,
    more: true,
    data: [],
    after: 0,
  });
  const { loading, data, after, more } = state;

  const load = () => {
    dispatch({ type: types.start });

    setTimeout(() => {
      const newData = reclamos.slice(after, after + perPage);
      dispatch({ type: types.loaded, newData });
    }, 300);
  };

  return (
    <MyContext.Provider value={{ cargando, loading, data, more, load }}>
      {children}
    </MyContext.Provider>
  );
}

function ListarReclamos() {
  const [pedido, setPedido] = useState();
  const [loadingModal, isLoading] = useState(true);
  const [isOpen, setIsOpen] = useState(false);
  const [isAceptar, setIsAceptar] = useState(false);
  const [isRechazar, setIsRechazar] = useState(false);
  const [comentario, setComentario] = useState({
    motivoDevolucion: "",
  });
  const [isProcessing, setIsProcessing] = useState(false);

  const { data, loading, more, load, cargando } = React.useContext(MyContext);
  const loader = React.useRef(load);
  const observer = React.useRef(
    new IntersectionObserver(
      (entries) => {
        const first = entries[0];
        if (first.isIntersecting) {
          loader.current();
        }
      },
      { threshold: 1 }
    )
  );
  const [element, setElement] = React.useState(null);

  React.useEffect(() => {
    loader.current = load;
  }, [load]);

  React.useEffect(() => {
    const currentElement = element;
    const currentObserver = observer.current;

    if (currentElement) {
      currentObserver.observe(currentElement);
    }

    return () => {
      if (currentElement) {
        currentObserver.unobserve(currentElement);
      }
    };
  }, [element]);

  if (cargando) {
    return (
      <div className="App">
        <Loading />
      </div>
    );
  }

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
    console.log(e.target.value);
    setComentario((comentario) => ({
      ...comentario,
      [e.target.name]: e.target.value,
    }));
  };

  const aceptarReclamo = () => {
    setIsProcessing(true);
    realizarDevolucion(pedido.id, true, comentario)
      .then((response) => {
        console.log(response);
        Noti("El reclamo fue aceptado con éxito");
        setTimeout(() => {
          window.location.replace("/");
        }, 3000);
      })
      .catch((error) => {
        NotiError(error.response.data);
      });
  };

  const rechazarReclamo = () => {
    setIsProcessing(true);
    if (comentario.motivoDevolucion === "") {
      NotiError("debe haber un comentario");
    } else {
      realizarDevolucion(pedido.id, false, comentario)
        .then((response) => {
          console.log(response);
          Noti("El reclamo fue rechazado con éxito");
          setTimeout(() => {
            window.location.replace("/");
          }, 3000);
        })
        .catch((error) => {
          NotiError(error.response.data);
        });
    }
  };

  return (
    <>
      <Styles>
        <ModalProvider>
          <br />
          {sessionStorage.getItem("reclamos-razon") ? (
            <h3>
              resultados de la busqueda por razon del reclamo:
              {sessionStorage.getItem("reclamos-razon")}
            </h3>
          ) : (
            <h3> resultados de la busqueda </h3>
          )}
          {sessionStorage.getItem("reclamos-cliente") ? (
            <h3>
              resultados de la busqueda por cliente del reclamo:
              {sessionStorage.getItem("reclamos-cliente")}
            </h3>
          ) : null}
          {sessionStorage.getItem("reclamos-ordenar") ? (
            <h3>resultados de la busqueda ordenados</h3>
          ) : null}
          <br />
          <div className="table-responsive justify-content-center" id="list">
            <table className="table table-hover">
              <tbody>
                {data.map((reclamo) => {
                  return (
                    <>
                      <br />
                      <tr key={reclamo.id}>
                        <td>ESTADO: {reclamo.estadoPedido}</td>
                        <th scope="col" />
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
                              disabled={
                                reclamo.estadoPedido === "DEVUELTO" ||
                                reclamo.estadoPedido === "RECLAMORECHAZADO"
                              }
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
                              disabled={
                                reclamo.estadoPedido === "DEVUELTO" ||
                                reclamo.estadoPedido === "RECLAMORECHAZADO"
                              }
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
            {loading && more && <Loading />}
            {!loading && more && <div ref={setElement} id="scroll"></div>}
          </div>
          {!loadingModal ? (
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
                <p>Estado: {pedido.estado}</p>
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
              <Button
                variant="danger"
                disabled={isProcessing}
                onClick={aceptarReclamo}
              >
                Aceptar
              </Button>
              <Button
                variant="secondary"
                disabled={isProcessing}
                onClick={toggleModalAceptar}
              >
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
                name="motivoDevolucion"
                onChange={handleChangeRechazar}
              />
            </div>
            <div className="abajo">
              <Button
                variant="danger"
                disabled={isProcessing}
                onClick={rechazarReclamo}
              >
                Rechazar
              </Button>
              <Button
                variant="secondary"
                disabled={isProcessing}
                onClick={toggleModalRechazar}
              >
                Cancelar
              </Button>
            </div>
          </StyledModal>
        </ModalProvider>
      </Styles>
    </>
  );
}

// eslint-disable-next-line import/no-anonymous-default-export
export default () => {
  return (
    <MyProvider>
      <ListarReclamos />
    </MyProvider>
  );
};

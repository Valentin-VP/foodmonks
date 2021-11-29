import React, { useEffect, useState } from "react";
import styled from "styled-components";
import {
  actualizarEstadoPedido,
  obtenerPedidosSinFinalizarEfectivo,
} from "../../services/Requests";
import { ModalItem } from "../../components/ModalItem";
import { Noti } from "../../components/Notification";
import { Col } from "react-bootstrap";
import { Loading } from "../../components/Loading";

const Styles = styled.div`
  .lista {
    padding-top: 35px;
  }
  .form-floating {
    margin-bottom: 15px;
  }
  h1 {
    color: #e87121;
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
    padding: 5px;
    width: 9%;
    &:hover {
      background-color: #fffff5;
    }
  }

  .text-center {
    position: relative;
  }

  .form-floating {
    margin-bottom: 15px;
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
  #scroll {
    background: "transparent";
    height: 6rem;
  }
`;

const perPage = 9;
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
  const [datos, setDatos] = useState([]);
  const [cargando, isLoading] = useState(true);

  useEffect(() => {
    fetch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetch = () => {
    obtenerPedidosSinFinalizarEfectivo()
      .then((response) => {
        if (response.status === 200) {
          setDatos(response.data);
          isLoading(false);
        } else {
          Noti(response.data);
        }
      })
      .catch((error) => {
        Noti(error.message);
      });
  };

  const updateState = (item) => {
    console.log(item);
    actualizarEstadoPedido("FINALIZADO", item.id)
      .then((response) => {
        if (response.status === 200) {
          Noti("El estado del pedido ha sido cambiado.");
          fetch();
          window.location.reload();
        } else {
          Noti(response.data);
        }
      })
      .catch((error) => {
        Noti(error.response.data);
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
      const newData = datos.slice(after, after + perPage);
      dispatch({ type: types.loaded, newData });
    }, 300);
  };

  return (
    <MyContext.Provider
      value={{ cargando, loading, data, more, load, updateState }}
    >
      {children}
    </MyContext.Provider>
  );
}

function ListadoPedidosEfectivo() {
  const [modal, setModal] = useState({ show: false, item: [] });

  const { data, loading, more, load, cargando, updateState } =
    React.useContext(MyContext);
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

  return (
    <>
      <Styles>
        <div className="container-lg">
          <main className="lista">
            <h1 className="text-center h5 mb-3 fw-normal">
              Cobrar Pagos Efectivo
            </h1>
            <div className="form-floating">
              <div className="table-responsive justify-content-center">
                <table className="table table-hover">
                  <tbody>
                    {data
                      ? data.map((item, index) => {
                          return (
                            <>
                              <Col>
                                <tr key={item.id}>
                                  <td>ID Pedido: {item.id}</td>
                                  <td>
                                    Fecha Confirmación:{" "}
                                    {item.fechaHoraProcesado}
                                  </td>
                                  <td>
                                    Fecha Entrega: {item.fechaHoraEntrega}
                                  </td>
                                  <td>Total: ${item.total}</td>
                                  <td>
                                    {
                                      <button
                                        className="btn btn-sm btn-secondary"
                                        type="button"
                                        onClick={(e) =>
                                          setModal({ item: item, show: true })
                                        }
                                      >
                                        Cobrar Pago
                                      </button>
                                    }
                                  </td>
                                </tr>
                              </Col>
                            </>
                          );
                        })
                      : null}
                  </tbody>
                </table>
                {!data.length > 0 && (
                  <h5 className="text-center h5 mb-3 fw-normal">
                    No hay pagos sin cobrar
                  </h5>
                )}
                {loading && more && <Loading />}
                {!loading && more && <div ref={setElement} id="scroll"></div>}
              </div>
            </div>
          </main>
          <ModalItem
            titulo="Gestión de Pedidos"
            cuerpo="¿Confirmar el cobro del pedido en efectivo? Esto no tiene vuelta atrás."
            visible={modal.show}
            onAceptar={() => {
              updateState(modal.item);
              setModal({ ...modal, show: false });
            }}
            onCancelar={() => {
              setModal({ item: [], show: false });
            }}
          ></ModalItem>
        </div>
      </Styles>
    </>
  );
}

// eslint-disable-next-line import/no-anonymous-default-export
export default () => {
  return (
    <MyProvider>
      <ListadoPedidosEfectivo />
    </MyProvider>
  );
};

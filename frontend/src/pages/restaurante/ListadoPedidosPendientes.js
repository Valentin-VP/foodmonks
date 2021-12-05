import React, { useEffect, useState } from "react";
import styled from "styled-components";
import {
  actualizarEstadoPedidoPendientes,
  obtenerPedidosSinConfirmar,
} from "../../services/Requests";
import { Noti } from "../../components/Notification";
import { Col } from "react-bootstrap";
import { ModalItem } from "../../components/ModalItem";
import { Loading } from "../../components/Loading";

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
    //let a = [{lol: "1", asd: "asdasd"}, {lol: "2", asd: "vbbv"}, {lol: "3", asd: "ff"}];
    //console.log(a.map((item) => (Object.assign(item, {visible: false}))));
    obtenerPedidosSinConfirmar()
      .then((response) => {
        if (response.status === 200) {
          response.data.map((item) => Object.assign(item, { visible: false }));
          console.log(response.data);
          setDatos(response.data);
          isLoading(false);
        } else {
          Noti(response.data);
        }
      })
      .catch((error) => {
        Noti(error.message);
      });
    //setData([...data, {tipoUser: "restaurante", nombreRestaurante: "asd", estado : "bloqueado"}]);
  };

  const updateState = (item, estado, minutos) => {
    if (!minutos) minutos = "60";
    console.log(item);
    actualizarEstadoPedidoPendientes(estado, item.id, minutos)
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
      value={{ cargando, loading, data, more, load, updateState, setDatos }}
    >
      {children}
    </MyContext.Provider>
  );
}

function ListadoPedidosPendientes(abierto) {
  const [modal, setModal] = useState({
    show: false,
    item: [],
    estado: "",
  });
  const [inputMinutos, setInputMinutos] = useState({
    minutos: "60",
  });

  const { data, loading, more, load, cargando, updateState, setDatos } =
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

  const onConfirmar = (e, item) => {
    e.preventDefault();
    setModal({ item: item, show: true, estado: "CONFIRMADO" });
  };

  const onRechazar = (e, item) => {
    e.preventDefault();
    setModal({ item: item, show: true, estado: "RECHAZADO" });
  };

  const onVisible = (id) => {
    let items = [...data];
    //de paso le pregunto si tiene menus (normalmente deberia tener), sino tiene no hago nada
    items.map((i) => {
      if (i.id === id && i.menus) i.visible = !i.visible;
      return i;
    });
    console.log(items);
    setDatos(items);
  };

  const handleChange = (e) => {
    e.persist();
    setInputMinutos((values) => ({
      ...values,
      [e.target.name]: e.target.value,
    }));
  };

  return (
    <>
      <Styles>
        <div className="container-lg">
          <main className="lista">
            <h1 className="text-center h5 mb-3 fw-normal">
              Pedidos Pendientes
            </h1>
            <div className="form-floating">
              <div className="table-responsive justify-content-center">
                <table className="table table-hover">
                  <tbody>
                    {/* <Col>
                                <tr>
                                  <td>ID Pedido: 1</td>
                                  <td>Nombre: Peddo1</td>
                                  <td>Dirección: Avenida Italia 1234 esq. Constituyente</td>
                                  <td>Cliente: Nombre Apellido</td>
                                  <td>Medio de Pago: Efectivo</td>
                                  <td>Total: $1234.00</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onConfirmar(e, {id : "1"}))}>
                                    Confirmar
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onRechazar(e, {id: "1"}))}>
                                    Rechazar
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button">
                                    +
                                  </button>}</td>
                                </tr>
                              </Col>
                              <Col>
                                    <tr>
                                        <td>
                                            <img
                                                src={"https://media.istockphoto.com/vectors/creative-hamburger-logo-design-symbol-vector-illustration-vector-id1156464773?k=20&m=1156464773&s=170667a&w=0&h=AcKSZuETET89SF-Liid0mAWTL5w6YQCIxeynD8J01Lk="}
                                                alt="productimg"
                                                width="150"
                                                hight="150"
                                            />
                                        </td>
                                        <td>Menú: Nombre1</td>
                                        <td>Precio: $1234.00</td>
                                        <td>Descuento: 0 %</td>
                                        <td>Total Parcial: $1234.00</td>
                                    </tr>
                              </Col>
                              <Col>
                                <tr>
                                  <td>ID Pedido: 2</td>
                                  <td>Nombre: Peddo2</td>
                                  <td>Dirección: Avenida Italia 1234 esq. Constituyente</td>
                                  <td>Cliente: Nombre Apellido</td>
                                  <td>Medio de Pago: Efectivo</td>
                                  <td>Total: $1234.00</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onConfirmar(e, {id : "2"}))}>
                                    Confirmar
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onRechazar(e, {id: "2"}))}>
                                    Rechazar
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button">
                                    +
                                  </button>}</td>
                                </tr>
                              </Col>
                              <Col>
                                    <tr>
                                        <td>
                                            <img
                                                src={"https://media.istockphoto.com/vectors/creative-hamburger-logo-design-symbol-vector-illustration-vector-id1156464773?k=20&m=1156464773&s=170667a&w=0&h=AcKSZuETET89SF-Liid0mAWTL5w6YQCIxeynD8J01Lk="}
                                                alt="productimg"
                                                width="150"
                                                hight="150"
                                            />
                                        </td>
                                        <td>Menú: Nombre1</td>
                                        <td>Precio: $1234.00</td>
                                        <td>Descuento: 0 %</td>
                                        <td>Total Parcial: $1234.00</td>
                                    </tr>
                              </Col> */}
                    {data
                      ? data.map((item) => {
                          return (
                            <>
                              <Col>
                                <tr key={item.id}>
                                  <td>ID Pedido: {item.id}</td>
                                  <td>Dirección: {item.direccion}</td>
                                  <td>Cliente: {item.nombreApellidoCliente}</td>
                                  <td>Medio de Pago: {item.medioPago}</td>
                                  <td>Total: ${item.total}</td>
                                  <td>
                                    {
                                      <button
                                        className="btn btn-sm btn-secondary"
                                        type="button"
                                        onClick={(e) => onConfirmar(e, item)}
                                        disabled={!abierto}
                                      >
                                        Confirmar
                                      </button>
                                    }
                                  </td>
                                  <td>
                                    {
                                      <button
                                        className="btn btn-sm btn-secondary"
                                        type="button"
                                        onClick={(e) => onRechazar(e, item)}
                                        disabled={!abierto}
                                      >
                                        Rechazar
                                      </button>
                                    }
                                  </td>
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
                                              <td>
                                                Menú: {menu.menu} (x
                                                {menu.cantidad})
                                              </td>
                                              <td>
                                                Precio unitario: ${menu.precio}
                                              </td>
                                              <td>
                                                Descuento:{" "}
                                                {menu.multiplicadorPromocion} %
                                              </td>
                                              <td>
                                                Total parcial: $
                                                {menu.precioPorCantidad}
                                              </td>
                                              <td>
                                                Total final: ${menu.calculado}
                                              </td>
                                              {/* <td>Cantidad: ${item.cantidad}</td> */}
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
                {!data.length > 0 && (
                  <h5 className="text-center h5 mb-3 fw-normal">
                    No hay pedidos pendientes
                  </h5>
                )}
                {loading && more && <Loading />}
                {!loading && more && <div ref={setElement} id="scroll"></div>}
              </div>
            </div>
          </main>
          <ModalItem
            titulo="Gestión de Pedidos"
            cuerpo={
              <>
                <div>
                  ¿Seguro que{" "}
                  {modal.estado === "RECHAZADO" ? "rechazas" : "confirmas"} este
                  pedido? Esto no tiene vuelta atrás.
                </div>
                {modal.estado === "CONFIRMADO" ? (
                  <div className="form-floating">
                    <input
                      className="form-control"
                      type="number"
                      name="minutos"
                      id="minutos"
                      value={inputMinutos.minutos}
                      placeholder="60"
                      min="1"
                      max="1000"
                      step="1"
                      onChange={handleChange}
                    />
                    <label htmlFor="floatingInput">T. Estimado (min.)</label>
                  </div>
                ) : null}
              </>
            }
            visible={modal.show}
            onAceptar={() => {
              updateState(modal.item, modal.estado, inputMinutos.minutos);
              setModal({ item: [], estado: "", show: false });
            }}
            onCancelar={() => {
              setModal({ item: [], estado: "", show: false });
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
      <ListadoPedidosPendientes />
    </MyProvider>
  );
};

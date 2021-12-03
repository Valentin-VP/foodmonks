import { React, Fragment, useState, useEffect } from "react";
import styled from "styled-components";
import { obtenerPedidosRealizados } from "../../services/Requests";
import { Noti } from "../../components/Notification";
import DatePicker from "react-datepicker";
import ListadoPedidosRealizados from "./ListadoPedidosRealizados";
import { Col } from "react-bootstrap";
import Pagination from "@material-ui/lab/Pagination";

const Styles = styled.div`
  .form {
    padding-top: 35px;
  }
  .text-center {
    position: relative;
  }

  .form-floating {
    margin-bottom: 15px;
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

  input {
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
  }

  .form-check-input {
    &:hover {
      border-color: #2080ff;
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
  }
  #fecha {
    height: 58px;
  }

  .MuiPaginationItem-page.Mui-selected {
    background-color: #e87121;
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
      background-color: #f87121;
    }
    &:hover {
      background-color: #da6416;
    }
    &:active {
      background-color: #d87121;
    }
  }
`;

export default function BuscarPedidosRealizados() {
  const [data, setData] = useState([]);
  //const [isLoading, setIsLoading] = useState(false);
  //const [loaded, setLoaded] = useState(false);
  //const [error, setError] = useState(false);
  const [values, setValues] = useState({
    nombreRestaurante: "",
    nombreMenu: "",
    estadoPedido: "",
    medioPago: "",
    minTotal: "",
    maxTotal: "",
    ordenamiento: "",
  });

  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const onChangeDate = (dates) => {
    const [start, end] = dates;
    setStartDate(start);
    setEndDate(end);
  };

  let estadoPedido = [
    { nombre: "(Cualquiera)", value: "" },
    { nombre: "Devuelto", value: "DEVUELTO" },
    { nombre: "Finalizado", value: "FINALIZADO" },
    { nombre: "Rechazado", value: "RECHAZADO" },
    { nombre: "A la Espera", value: "CONFIRMADO" },
    { nombre: "Pendiente", value: "PENDIENTE" },
  ];

  let medioPago = [
    { nombre: "(Cualquiera)", value: "" },
    { nombre: "PayPal", value: "PAYPAL" },
    { nombre: "Efectivo", value: "EFECTIVO" },
  ];

  let ordenamiento = [
    { nombre: "(Ninguno)", value: "" },
    { nombre: "Precio (asc.)", value: "asc" },
    { nombre: "Precio (desc.)", value: "desc" },
  ];

  if (
    sessionStorage.getItem("pedidoId") !== undefined ||
    sessionStorage.getItem("pedidoId") !== "" ||
    sessionStorage.getItem("pedidoId") !== null
  ) {
    sessionStorage.removeItem("pedidoId");
  }

  useEffect(() => {
    fetch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetch = (page) => {
    let p = page ? page - 1 : 0;
    obtenerPedidosRealizados(values, startDate, endDate, p)
      .then((response) => {
        if (response.status === 200) {
          //console.log(response.data);
          setData(response.data);
        } else {
          Noti(response.data);
        }
      })
      .catch((error) => {
        Noti(error.response);
      });
  };

  const onPageChange = (page) => {
    fetch(page);
  };

  const handleChange = (e) => {
    e.persist();
    setValues((values) => ({
      ...values,
      [e.target.name]:
        e.target.type === "checkbox" ? e.target.checked : e.target.value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log(values.nombreRestaurante);
    setPage(1);
    onPageChange(1);
  };

  const onVisibleMenu = (id) => {
    let items = [...data.pedidos];
    //de paso le pregunto si tiene menus (normalmente deberia tener), sino tiene no hago nada
    items.map((i) => {
      if (i.id === id && i.menus) i.visibleMenu = !i.visibleMenu;
      return i;
    });
    setData({ ...data, pedidos: items });
  };

  const onVisibleReclamo = (id) => {
    let items = [...data.pedidos];
    //le pregunto si tiene reclamo
    items.map((i) => {
      if (i.id === id && i.reclamo) i.visibleReclamo = !i.visibleReclamo;
      return i;
    });
    setData({ ...data, pedidos: items });
  };

  const [page, setPage] = useState(1);

  const handlePageChange = (e, value) => {
    setPage(value);
    onPageChange(value);
  };

  return (
    <Styles>
      <Fragment>
        <div className="container-lg">
          <main className="form">
            <form id="inputs" onSubmit={handleSubmit}>
              <div className="row align-items-center">
                <div className="col-lg">
                  <div className="form-floating">
                    <input
                      name="minTotal"
                      className="form-control"
                      type="number"
                      onChange={handleChange}
                      id="minTotal"
                      min="0"
                      max="100000"
                      value={values.minTotal}
                    ></input>
                    <label htmlFor="minTotal">Total Inicial</label>
                  </div>
                  <div className="form-floating">
                    <input
                      name="maxTotal"
                      className="form-control"
                      type="number"
                      onChange={handleChange}
                      id="maxTotal"
                      min="0"
                      max="100000"
                      value={values.maxTotal}
                    ></input>
                    <label htmlFor="maxTotal">Total Final</label>
                  </div>
                </div>
                <div className="col-lg">
                  <div className="form-floating">
                    <DatePicker
                      id="fecha"
                      name="fecha"
                      className="form-control"
                      selected={startDate}
                      onChange={onChangeDate}
                      startDate={startDate}
                      endDate={endDate}
                      selectsRange
                      dateFormat="yyyy-MM-dd"
                      placeholderText="Fechas Entrega"
                    />
                  </div>
                  <div className="form-floating">
                    <select
                      name="ordenamiento"
                      className="form-select"
                      onChange={handleChange}
                      id="ordenamiento"
                    >
                      {ordenamiento.map((item) => (
                        <option key={item.nombre} value={item.value}>
                          {item.nombre}
                        </option>
                      ))}
                    </select>
                    <label htmlFor="ordenamiento">Ordenamiento</label>
                  </div>
                </div>
                <div className="col-lg">
                  <div className="form-floating">
                    <input
                      name="nombreRestaurante"
                      className="form-control"
                      type="text"
                      onChange={handleChange}
                      id="nombreRestaurante"
                    ></input>
                    <label htmlFor="nombreRestaurante">Restaurante</label>
                  </div>
                  <div className="form-floating">
                    <input
                      name="nombreMenu"
                      className="form-control"
                      type="text"
                      onChange={handleChange}
                      id="nombreMenu"
                      value={values.nombreMenu}
                    ></input>
                    <label htmlFor="nombreMenu">Menú</label>
                  </div>
                </div>
                <div className="col-lg">
                  <div className="form-floating">
                    <select
                      name="estadoPedido"
                      className="form-select"
                      onChange={handleChange}
                      id="estadoPedido"
                    >
                      {estadoPedido.map((item) => (
                        <option key={item.nombre} value={item.value}>
                          {item.nombre}
                        </option>
                      ))}
                    </select>
                    <label htmlFor="estadoPedido">Estado</label>
                  </div>
                  <div className="form-floating">
                    <select
                      name="medioPago"
                      className="form-select"
                      onChange={handleChange}
                      id="medioPago"
                    >
                      {medioPago.map((item) => (
                        <option key={item.nombre} value={item.value}>
                          {item.nombre}
                        </option>
                      ))}
                    </select>
                    <label htmlFor="medioPago">Medio de pago</label>
                  </div>
                </div>
              </div>

              <button
                className="w-100 btn btn-md btn-primary oButton"
                type="submit"
              >
                Buscar
              </button>
            </form>
            <div className="form-floating">
              {/*Espacio para alguna otra cosa?¿?*/}
            </div>

            <div className="form-floating">
              <div className="row align-items-center">
                <div className="col-md">
                  {
                    <ListadoPedidosRealizados
                      datos={data}
                      onVisibleMenu={onVisibleMenu}
                      onVisibleReclamo={onVisibleReclamo}
                    />
                  }
                  {data.pedidos && data.pedidos.length > 0 ? (
                    <Col
                      style={{ display: "flex" }}
                      className="justify-content-center"
                    >
                      <Pagination
                        className="my-3"
                        count={data.totalPages ? data.totalPages : 0}
                        page={page}
                        siblingCount={1}
                        boundaryCount={1}
                        variant="outlined"
                        shape="rounded"
                        onChange={handlePageChange}
                      />
                    </Col>
                  ) : (
                    <h5 className="text-center h5 mb-3 fw-normal">
                      No se encontraron pedidos.
                    </h5>
                  )}
                </div>
              </div>
            </div>
          </main>
        </div>
      </Fragment>
    </Styles>
  );
}

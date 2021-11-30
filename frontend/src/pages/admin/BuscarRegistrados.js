import { React, Fragment, useState, useEffect } from "react";
import styled from "styled-components";
import { fetchUsuariosBusqueda } from "../../services/Requests";
import ListadoRegistrados from "./ListadoRegistrados";
import { Noti } from "../../components/Notification";

import DatePicker from "react-datepicker";

import "react-datepicker/dist/react-datepicker.css";
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
  #fecha{
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

export default function BuscarRegistrados() {
  const [data, setData] = useState([]);
  //const [isLoading, setIsLoading] = useState(false);
  //const [loaded, setLoaded] = useState(false);
  //const [error, setError] = useState(false);
  const [values, setValues] = useState({
    tipoUser: "",
    estado: "",
    correo: "",
    ordenar: false,
  });

  useEffect(() => {
    fetch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const onChangeDate = (dates) => {
    const [start, end] = dates;
    setStartDate(start);
    setEndDate(end);
  };

  let tipoUser = [
    { tipo: "N/A", value: "" },
    { tipo: "Restaurante", value: "restaurante" },
    { tipo: "Cliente", value: "cliente"},
    { tipo: "Admin", value: "admin"},
  ];

  let estado = [
    { estado: "N/A", value: "" },
    { estado: "Bloqueado", value: "BLOQUEADO" },
    { estado: "Desbloqueado", value: "DESBLOQUEADO" },
    { estado: "Eliminado", value: "ELIMINADO" },
  ];

  const fetch = (page) => {
    let p = page ? page - 1 : 0;
    fetchUsuariosBusqueda(values, startDate, endDate, p)
      .then((response) => {
        if (response.status === 200) {
          console.log(response.data);
          setData(response.data);
        } else {
          Noti(response.data);
        }
      })
      .catch((error) => {
        Noti(error.response.data);
      });
  };

  // Pagination
  const onPageChange = (page) => {
    fetch(page);
  };

  const [page, setPage] = useState(1);

  const handlePageChange = (e, value) => {
    setPage(value);
    onPageChange(value);
  };
  // End Pagination

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
    setPage(1);
    onPageChange(1);
    // dependiendo de la respuesta del servidor para el request de buscar, muestro una tabla con
    // los datos de los usuarios y eventualmente 2 botones (des/bloquear y elim-perm (solo si bloq))
    //fetch();
    //setLoaded(!loaded);
  };

  //   useEffect(() => {
  //     fetch();
  //  }, [])

  return (
    <Styles>
      <Fragment>
        <div className="container-lg">
          <main className="form">
            <form id="inputs" onSubmit={handleSubmit}>
              <h1 className="text-center h5 mb-3 fw-normal">
                Búsqueda de Usuarios Registrados
              </h1>
              <div className="row align-items-center">
                <div className="col-lg">
                  <div className="form-floating">
                    <input
                      name="correo"
                      className="form-control"
                      onChange={handleChange}
                      id="correo"
                      value={values.correo}
                    ></input>
                    <label htmlFor="correo">Email del Usuario</label>
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
                      placeholderText="Fecha Registro"
                    />
                  </div>
                </div>
                <div className="col-lg">
                  <div className="form-floating">
                    <select
                      name="tipoUser"
                      className="form-select"
                      onChange={handleChange}
                      id="tipoUser"
                    >
                      {tipoUser.map((item) => (
                        <option key={item.tipo} value={item.value}>
                          {item.tipo}
                        </option>
                      ))}
                    </select>
                    <label htmlFor="tipoUser">Tipo de Usuario</label>
                  </div>
                </div>
                <div className="col-lg">
                  <div className="form-floating">
                    <select
                      name="estado"
                      className="form-select"
                      onChange={handleChange}
                      id="estado"
                    >
                      {estado.map((item,index) => (
                        <option key={index} value={item.value}>
                          {item.estado}
                        </option>
                      ))}
                    </select>
                    <label htmlFor="estado">Estado</label>
                  </div>
                </div>
                <div className="col-lg">
                      <div className="form-floating">
                          <div className="checkbox">
                              <label>
                                  <input
                                      name="ordenar"
                                      className="form-check-input"
                                      type="checkbox"
                                      checked={values.ordenar}
                                      onChange={handleChange}
                                      id="ordenar"
                                      disabled={!values.tipoUser || values.tipoUser==="admin"}
                                  /> Ordenar por Calificación según {values.tipoUser==="restaurante" ? values.tipoUser : 
                                  values.tipoUser==="cliente" ? values.tipoUser : "tipo de usuario"}
                              </label>
                          </div>
                      </div>
                  </div>
              </div>

              <button className="w-100 btn btn-md btn-primary" type="submit">
                Buscar
              </button>
            </form>
            <div className="form-floating">
              {/*Espacio para alguna otra cosa?¿?*/}
            </div>

            <div className="form-floating">
              <div className="row align-items-center">
                <div className="col-md">
                  {<ListadoRegistrados data={data} fetchFunc={fetch} />}
                  {data && data.usuarios && data.usuarios.length > 0 ? (
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
                      No se encontraron usuarios.
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

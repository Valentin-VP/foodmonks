import React, { useState, useEffect, Fragment } from "react";
import styled from "styled-components";
import { fetchReclamos } from "../../services/Requests";
import { Noti } from "../../components/Notification";
import ListarReclamos from "./ListarReclamos";
import { Loading } from "../../components/Loading";

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
`;

function BuscarReclamos() {
  const [values, setValues] = useState({
    razon: "",
    cliente: "",
    ordenar: false,
  });

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
    sessionStorage.setItem("reclamos-razon", values.razon);
    sessionStorage.setItem("reclamos-cliente", values.cliente);
    sessionStorage.setItem("reclamos-ordenar", values.ordenar);
    window.location.reload();
  };

  return (
    <Styles>
      <Fragment>
        <div className="container-lg">
          <main className="form">
            <form id="inputs" onSubmit={handleSubmit}>
              <h1 className="text-center h5 mb-3 fw-normal">
                Búsqueda de reclamos
              </h1>
              <div className="row align-items-center">
                <div className="col-lg">
                  <div className="form-floating">
                    <input
                      name="cliente"
                      className="form-control"
                      onChange={handleChange}
                      id="cliente"
                      value={values.cliente}
                    ></input>
                    <label htmlFor="correo">Email del cliente</label>
                  </div>
                </div>
                <div className="col-lg">
                  <div className="form-floating">
                    <input
                      name="razon"
                      className="form-control"
                      onChange={handleChange}
                      id="razon"
                      value={values.razon}
                    ></input>
                    <label htmlFor="razon">Razón del reclamo</label>
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
                        />{" "}
                        Ordenar por fecha
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
              <div className="row align-items-center">
                <div className="col-md">
                  <ListarReclamos />
                </div>
              </div>
            </div>
          </main>
        </div>
      </Fragment>
    </Styles>
  );
}

export default BuscarReclamos;

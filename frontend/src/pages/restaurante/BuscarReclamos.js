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
  const [reclamos, setReclamos] = useState([]);
  const [isLoading, setLoading] = useState(true);
  const [values, setValues] = useState({
    razon: "",
    cliente: "",
    ordenar: false,
  });

  useEffect(() => {
    fetch();
    setLoading(false);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetch = () => {
    fetchReclamos(values)
      .then((response) => {
        if (response.status === 200) {
          console.log(response.data);
          setReclamos(response.data);
        } else {
          Noti(response.data);
        }
      })
      .catch((error) => {
        Noti(error.message);
      });
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
    fetch();
  };

  if (isLoading) {
    return <Loading />;
  }

  return (
    <Styles>
      <Fragment>
        <div className="container-lg">
          <main className="form">
            <form id="inputs" onSubmit={handleSubmit}>
              <h1 className="text-center h5 mb-3 fw-normal">
                Búsqueda de Reclamos
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
                    <label htmlFor="correo">Email del Cliente</label>
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
                    <label htmlFor="razon">Razon del Reclamo</label>
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
                        Ordenar por Fecha
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
                  {reclamos !== null ? (
                    <ListarReclamos reclamos={reclamos} />
                  ) : (
                    <p> No hay reclamos </p>
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

export default BuscarReclamos;

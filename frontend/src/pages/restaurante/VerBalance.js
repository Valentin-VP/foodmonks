import { React, Fragment, useState, useEffect } from "react";
import styled from "styled-components";
import { obtenerBalance } from "../../services/Requests";
import { Noti } from "../../components/Notification";
import DatePicker from "react-datepicker";
import ResultadoBalance from "./ResultadoBalance";

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
    border-radius: 10px;
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

export default function VerBalance() {
  const [data, setData] = useState([]);
  //const [isLoading, setIsLoading] = useState(false);
  const [loaded, setLoaded] = useState(false);
  //const [error, setError] = useState(false);
  const [values, setValues] = useState({
    categoria: "",
    medioPago: "",
  });

  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const onChangeDate = (dates) => {
    const [start, end] = dates;
    setStartDate(start);
    setEndDate(end);
  };

  let categoria = [
    { nombre: "(Cualquiera)", value: "" },
    { nombre: "Pizzas", value: "PIZZAS" },
    { nombre: "Hamburguesas", value: "HAMBURGUESAS" },
    { nombre: "Bebidas", value: "BEBIDAS" },
    { nombre: "Combos", value: "COMBOS" },
    { nombre: "Minutas", value: "MINUTAS" },
    { nombre: "Postres", value: "POSTRES" },
    { nombre: "Pastas", value: "PASTAS" },
    { nombre: "Comida Arabe", value: "COMIDAARABE" },
    { nombre: "Sushi", value: "SUSHI" },
    { nombre: "Otros", value: "OTROS" },
  ];

  let medioPago = [
    { nombre: "(Cualquiera)", value: "" },
    { nombre: "PayPal", value: "PAYPAL" },
    { nombre: "Efectivo", value: "EFECTIVO" },
  ];

  useEffect(() => {
    fetch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetch = () => {
    obtenerBalance(values, startDate, endDate)
      .then((response) => {
        if (response.status === 200) {
          setLoaded(true);
          //console.log(response.data);
          setData(response.data);
        } else {
          Noti(response.data);
        }
      })
      .catch((error) => {
        Noti(error.response.data);
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

  return (
    <Styles>
      <Fragment>
        <div className="container-lg">
          <main className="form">
            <form id="inputs" onSubmit={handleSubmit}>
              <div className="row align-items-center">
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
                </div>
                <div className="col-lg">
                  <div className="form-floating">
                    <select
                      name="categoria"
                      className="form-select"
                      onChange={handleChange}
                      id="categoria"
                    >
                      {categoria.map((item) => (
                        <option key={item.nombre} value={item.value}>
                          {item.nombre}
                        </option>
                      ))}
                    </select>
                    <label htmlFor="categoria">Categoría Menú</label>
                  </div>
                </div>
                <div className="col-lg">
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

              <button className="w-100 btn btn-md btn-primary" type="submit">
                Obtener
              </button>
            </form>
            <div className="form-floating">
              {/*Espacio para alguna otra cosa?¿?*/}
            </div>

            <div className="form-floating">
              <div className="row align-items-center">
                <div className="col-md">
                  {loaded && (!data.meses || !data.meses.length > 0) ? (
                    <h5 className="text-center h5 mb-3 fw-normal">
                      No se encontraron pedidos.
                    </h5>
                  ) : loaded && data.meses && data.meses.length > 0 ? (
                    <ResultadoBalance datos={data} />
                  ) : null}
                </div>
              </div>
            </div>
          </main>
        </div>
      </Fragment>
    </Styles>
  );
}

import { React, Fragment, useState, useEffect, useRef } from "react";
import styled from "styled-components";
import { fetchRestaurantesBusqueda, obtenerPedidosHistorico } from "../../services/Requests";
import { Noti } from "../../components/Notification"
import ListadoHistoricoPedidos from "./ListadoHistoricoPedidos";

const Styles = styled.div`
  .form{
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

  .form-check-input{
    &:hover {
      border-color: #2080FF;
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
  }

`;

export default function BuscarHistoricoPedidos() {
  const [data, setData] = useState([]);
  //const [isLoading, setIsLoading] = useState(false);
  //const [loaded, setLoaded] = useState(false);
  //const [error, setError] = useState(false);
  const [values, setValues] = useState({
    categoria: "",
    nombre: "",
    calificacion: false,
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
    { nombre: "Hamburguesas", value: "HAMBURGUESAS"},
    { nombre: "Bebidas", value: "BEBIDAS" },
    { nombre: "Combos", value: "COMBOS" },
    { nombre: "Minutas", value: "MINUTAS" },
    { nombre: "Postres", value: "POSTRES" },
    { nombre: "Pastas", value: "PASTAS" },
    { nombre: "Comida Arabe", value: "COMIDAARABE" },
    { nombre: "Sushi", value: "SUSHI" },
    { nombre: "Otros", value: "OTROS" },
  ];

  const fetch = () => {
    obtenerPedidosHistorico(values, startDate, endDate).then((response)=>{
      if (response.status===200){
        console.log(response.data);
        setData(response.data);
      }else{
        Noti(response.data);
      }
    }).catch((error)=>{
      Noti(error.response.data);
    })
  }

  const handleChange = (e) => {
    e.persist();
    setValues((values) => ({
      ...values,
      [e.target.name]: e.target.type === 'checkbox' ? e.target.checked : e.target.value,
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
              <div class="row align-items-center">
                  <div class="col-lg">
                      <div className="form-floating">
                          <input 
                              name="nombre"
                              className="form-control"
                              onChange={handleChange}
                              id="nombre"
                              value={values.nombre}>
                          </input>
                          <label for="nombre">Nombre</label>
                      </div>
                  </div>
                  <div class="col-lg">
                      <div className="form-floating">
                          <select 
                              name="categoria"
                              className="form-select"
                              onChange={handleChange}
                              id="categoria">
                              {categoria.map((item)=>(
                                <option key={item.nombre} value={item.value}>{item.nombre}</option>
                              ))}
                          </select>
                          <label for="categoria">Categoría</label>
                      </div>
                  </div>
                  <div class="col-lg">
                      <div className="form-floating">
                          <div className="checkbox">
                              <label>
                                  <input
                                      name="calificacion"
                                      class="form-check-input"
                                      type="checkbox"
                                      checked={values.calificacion}
                                      onChange={handleChange}
                                      id="calificacion"
                                  /> Ordenar por Calificación
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
                <div class="row align-items-center">
                  <div class="col-md">
                    {<ListadoHistoricoPedidos data={data} />}
                  </div>
                </div>
              </div>
          </main>
        </div>
      </Fragment>
    </Styles>
  );
}
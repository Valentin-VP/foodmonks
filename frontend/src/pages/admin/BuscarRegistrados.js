import { React, Fragment, useState, useEffect, useRef } from "react";
import styled from "styled-components";
import { fetchUsuariosBusqueda } from "../../services/Requests";
import ListadoRegistrados from "./ListadoRegistrados";
import { Noti } from "../../components/Notification"

import DatePicker from "react-datepicker";

import "react-datepicker/dist/react-datepicker.css";

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

  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const onChangeDate = (dates) => {
    const [start, end] = dates;
    setStartDate(start);
    setEndDate(end);
  };

  let tipoUser = [
    { tipo: "N/A", value: ""},
    { tipo: "Restaurante", value: "restaurante" },
    { tipo: "Cliente", value: "cliente"},
    { tipo: "Admin", value: "admin"},
  ];

  let estado = [
    { estado: "N/A", value:""},
    { estado: "Bloqueado", value:"BLOQUEADO"},
    { estado: "Desbloqueado", value:"DESBLOQUEADO"},
    { estado: "Eliminado", value:"ELIMINADO"},
  ];

  const fetch = () => {
    //let a = [{lol: "1", asd: "asdasd"}, {lol: "2", asd: "vbbv"}, {lol: "3", asd: "ff"}];
    //console.log(a.map((item) => (Object.assign(item, {visible: false}))));
    fetchUsuariosBusqueda(values, startDate, endDate).then((response)=>{
      if (response.status===200){
        console.log(response.data);
        setData(response.data);
      }else{
        Noti(response.data);
      }
    }).catch((error)=>{
      Noti(error.message);
    })
    //setData([...data, {tipoUser: "restaurante", nombreRestaurante: "asd", estado : "bloqueado"}]);
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

    // dependiendo de la respuesta del servidor para el request de buscar, muestro una tabla con
    // los datos de los usuarios y eventualmente 2 botones (des/bloquear y elim-perm (solo si bloq))
    fetch();
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
              <h1 className="text-center h5 mb-3 fw-normal">Búsqueda de Usuarios Registrados</h1>
              <div class="row align-items-center">
                  <div class="col-lg">
                      <div className="form-floating">
                          <input 
                              name="correo"
                              className="form-control"
                              onChange={handleChange}
                              id="correo"
                              value={values.correo}>
                          </input>
                          <label for="correo">Email del Usuario</label>
                      </div>
                  </div>
                  <div class="col-lg">
                      <div className="form-floating">
                        <DatePicker
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
                  <div class="col-lg">
                      <div className="form-floating">
                          <select 
                              name="tipoUser"
                              className="form-select"
                              onChange={handleChange}
                              id="tipoUser">
                              {tipoUser.map((item)=>(
                                <option key={item.tipo} value={item.value}>{item.tipo}</option>
                              ))}
                          </select>
                          <label for="tipoUser">Tipo de Usuario</label>
                      </div>
                  </div>
                  <div class="col-lg">
                      <div className="form-floating">
                          <select 
                              name="estado"
                              className="form-select"
                              onChange={handleChange}
                              id="estado">
                              {estado.map((item)=>(
                                <option key={item.estado} value={item.value}>{item.estado}</option>
                              ))}
                          </select>
                          <label for="estado">Estado</label>
                      </div>
                  </div>
                  <div class="col-lg">
                      <div className="form-floating">
                          <div className="checkbox">
                              <label>
                                  <input
                                      name="ordenar"
                                      class="form-check-input"
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
                <div class="row align-items-center">
                  <div class="col-md">
                    {<ListadoRegistrados data={data} fetchFunc={fetch}/>}
                  </div>
                </div>
              </div>
          </main>
        </div>
      </Fragment>
    </Styles>
  );
}
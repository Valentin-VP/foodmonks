import { React, Fragment, useState, useEffect, useRef } from "react";
import styled from "styled-components";
import { fetchUsuariosBusqueda } from "../../services/Requests";
import ListadoRegistrados from "./ListadoRegistrados";

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
  const [isLoading, setIsLoading] = useState(false);
  const [loaded, setLoaded] = useState(false);
  const [error, setError] = useState(false);
  const [values, setValues] = useState({
    filtro1: "1",
    filtro2: "2",
    filtro3: "3",
    email: "",
    ordenar: false,
  });

  const fetch = () => {
    //let a = [{lol: "1", asd: "asdasd"}, {lol: "2", asd: "vbbv"}, {lol: "3", asd: "ff"}];
    //console.log(a.map((item) => (Object.assign(item, {visible: false}))));
    // fetchUsuariosBusqueda(values).then((response)=>{
    //   if (response.status===200){
    //     setData(response.data);
    //     setError(null);
    //   }else{
    //     alert(response.status);
    //     setError(null);
    //   }
    // }).catch((error)=>{
    //   setError(error);
    //   alert(error);
    // })
    setData([...data, {estado : "eliminado"}]);
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
    setLoaded(!loaded);
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
                              name="email"
                              className="form-control"
                              onChange={handleChange}
                              id="email"
                              value={values.email}>
                          </input>
                          <label for="email">Email del Usuario</label>
                      </div>
                  </div>
                  <div class="col-lg">
                      <div className="form-floating">
                          <select 
                              name="filtro1"
                              className="form-select"
                              onChange={handleChange}
                              id="filtro1"
                              value={values.filtro1}>
                              <option selected>Placeholder 1</option>
                              <option value="1">One</option>
                              <option value="2">Two</option>
                              <option value="3">Three</option>
                          </select>
                          <label for="filtro1">Filtro 1</label>
                      </div>
                  </div>
                  <div class="col-lg">
                      <div className="form-floating">
                          <select 
                              name="filtro2"
                              className="form-select"
                              onChange={handleChange}
                              id="filtro2"
                              value={values.filtro2}>
                              <option selected>Placeholder 2</option>
                              <option value="1">One</option>
                              <option value="2">Two</option>
                              <option value="3">Three</option>
                          </select>
                          <label for="filtro2">Filtro 2</label>
                      </div>
                  </div>
                  <div class="col-lg">
                      <div className="form-floating">
                          <select 
                              name="filtro3"
                              className="form-select"
                              onChange={handleChange}
                              id="filtro3"
                              value={values.filtro3}>
                              <option selected>Placeholder 3</option>
                              <option value="1">One</option>
                              <option value="2">Two</option>
                              <option value="3">Three</option>
                          </select>
                          <label for="filtro3">Filtro 3</label>
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
                                  /> Ordenar
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
                    {loaded && <ListadoRegistrados data={data} fetchFunc={fetch}/>}
                  </div>
                </div>
              </div>
          </main>
        </div>
      </Fragment>
    </Styles>
  );
}
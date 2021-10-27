import { React, Fragment, useState, useEffect, useRef } from "react";
import styled from "styled-components";
import { Alert } from "react-bootstrap";
import { altaAdmin } from "../../services/Requests";

const Styles = styled.div`
  .form{
    padding: 35px;
    width: 500px;
    margin: auto;
    border-radius: 5px;
    box-shadow: 10px 15px 500px #CCC;
    background: white;
    @media screen and (max-width: 576px) {
      width: auto;
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

  .alert-dismissible .btn-close{
    background-color: transparent;
  }

`;

export default function AltaAdmin() {
  const [isLoading, setIsLoading] = useState(false);
  const [success, setSuccess] = useState(null);
  const [error, setError] = useState(null);
  const [values, setValues] = useState({
    nombre: "",
    apellido: "",
    password: "",
    email: "",
  });

  const enviarAltaAdmin = () => {
    setIsLoading(true);
    altaAdmin(values).then((response)=>{
      if (response.status===201){
        setSuccess(<Alert variant="success" dismissible onClose={() => setSuccess(null)}>Administrador creado con éxito. </Alert>);
        setError(null);
      }else{
        setSuccess(null);
        setError(<Alert variant="danger" dismissible onClose={() => setError(null)}>Error al intentar dar el alta.</Alert>);
      }
    }).catch((error)=>{
      setSuccess(null);
      setError(<Alert variant="danger" dismissible onClose={() => setError(null)}>Error al intentar dar el alta.</Alert>);
    })
    setIsLoading(false);
  }; 

  const handleChange = (e) => {
    e.persist();
    setValues((values) => ({
      ...values,
      [e.target.name]: e.target.type === 'checkbox' ? e.target.checked : e.target.value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    enviarAltaAdmin();
  };

//   useEffect(() => {
//     fetch();
//  }, [])

  return (
    <Styles>
      <Fragment>
        <div className="container-fluid">
          <main className="form">
            <form id="inputs" onSubmit={handleSubmit}>
              <h1 className="text-center h5 mb-3 fw-normal">Dar de Alta un Admin</h1>
                  <div class="col-lg">
                      <div className="form-floating">
                        <input
                          className="form-control"
                          type="text"
                          name="nombre"
                          id="nombre"
                          value={values.nombre}
                          placeholder="Nombre"
                          onChange={handleChange}
                          required={true}
                        />
                        <label for="floatingInput">Nombre</label>
                      </div>
                  </div>
                  <div class="col-lg">
                      <div className="form-floating">
                        <input
                          className="form-control"
                          type="text"
                          name="apellido"
                          id="apellido"
                          value={values.apellido}
                          placeholder="Apellido"
                          onChange={handleChange}
                          required={true}
                        />
                        <label for="floatingInput">Apellido</label>
                      </div>
                  </div>
                  <div class="col-lg">
                      <div className="form-floating">
                        <input
                          className="form-control"
                          type="email"
                          name="email"
                          id="email"
                          value={values.email}
                          placeholder="Correo"
                          onChange={handleChange}
                          required={true}
                        />
                        <label for="floatingInput">Correo</label>
                      </div>
                  </div>
                  <div class="col-lg">
                      <div className="form-floating">
                        <input
                          className="form-control"
                          type="password"
                          name="password"
                          id="password"
                          value={values.password}
                          placeholder="Password"
                          onChange={handleChange}
                          required={true}
                        />
                        <label for="floatingInput">Password</label>
                      </div>
                  </div>
                  <div class="col-lg">

                  </div>

              <button className="w-100 btn btn-md btn-primary" type="submit">
                Alta
              </button>
            </form>
              <div className="form-floating">
                {success}
                {error}
              </div>

              <div className="form-floating">
                <div class="row align-items-center">
                  <div class="col-md">
                    
                  </div>
                </div>
              </div>
          </main>
        </div>
      </Fragment>
    </Styles>
  );
}
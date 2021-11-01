import styled from "styled-components";
import { useState } from "react";
import logo from "../assets/foodMonks-sinfondo.png";
import { recuperarPassword } from "../services/Requests";
import { Alert } from "react-bootstrap";
import { Fragment } from "react";

const Styles = styled.div`
  .text-center {
    position: relative;
    padding: 30px;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
  }

  .form-signin {
    width: 400px;
    @media screen and (max-width: 576px) {
      width: auto;
    }
  }

  .form-floating {
    margin-bottom: 10px;
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

  #rpAlert {
    margin-top: 30px;
  }
`;

export default function ResetPassword() {
  //const [confirmado, setConfirmado] = useState(false);
  //const [isConfirmando, setIsConfirmando] = useState(false);
  // const [isEnviandoCodigo, setIsEnviandoCodigo] = useState(false);
  const [values, setValues] = useState({
    email: "",
  });
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const handleChange = (e) => {
    e.persist();
    setValues({
      ...values,
      [e.target.name]: e.target.value,
    });
  };

  async function handleEnviarCodigoClick(e) {
    e.preventDefault();

    // setIsEnviandoCodigo(true);
    await recuperarPassword(values)
      .then((response) => {
        setError(null);
        setSuccess(
          <Alert
            variant="success"
            dismissible
            onClose={() => {
              setSuccess(null);
            }}
          >
            {response.data}
          </Alert>
        );
        //localStorage.setItem('recover.mail',values.email);
      })
      .catch((error) => {
        setError(
          <Alert
            variant="danger"
            dismissible
            onClose={() => {
              setError(null);
            }}
          >
            {error.response.data}
          </Alert>
        );
        setSuccess(null);
      });
    // setIsEnviandoCodigo(false);
    //setSuccess(<Redirect to='/changePassword'/>);
  }
  return (
    <Styles>
      <Fragment>
        <div className="text-center">
          <main className="form-signin">
            <form id="inputs" onSubmit={handleEnviarCodigoClick}>
              <a href="/">
                <img
                  className="mb-4"
                  src={logo}
                  alt=""
                  width="200"
                  height="200"
                />
              </a>
              <h1 className="h3 mb-3 fw-normal">Recuperar contraseña</h1>

              <div className="form-floating">
                <input
                  type="email"
                  name="email"
                  className="form-control"
                  id="email"
                  placeholder="name@example.com"
                  onChange={handleChange}
                  //value={values.email}
                  required
                />
                <label htmlFor="floatingInput">Correo electronico</label>
              </div>
              <button className="w-100 btn btn-lg btn-primary" type="submit">
                Solicitar nueva contraseña
              </button>
              <div id="rpAlert">
                {success}
                {error}
              </div>
              <p className="mt-2 mb-3 text-muted">
                ¿No tienes cuenta?<a href="/register">Registrate</a>
              </p>
            </form>
          </main>
        </div>
      </Fragment>
    </Styles>
  );
  /*useEffect(() =>{    
        document.title = `Code is ${campos.code}`;  
    }, [campos.code]);*/
} //{(!redirect ? renderEmailForm() : <Redirect to={redirect} />)}
//{!codigoEnviado ? renderEmailForm() : !confirmado ? renderConfirmacionForm() : r

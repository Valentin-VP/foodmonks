import { React, Fragment, useState } from "react";
import { Link } from "react-router-dom";
import styled from "styled-components";
import logo from "../assets/foodMonks-sinfondo.png";
import { userLogin } from "../services/Requests";
import { Error } from "../components/Error";
import { Base64 } from "js-base64";

const Styles = styled.div`
  .text-center {
    position: absolute;
    left: 50%;
    top: 50%;
    -webkit-transform: translate(-50%, -50%);
    transform: translate(-50%, -50%);
    width: 30%;
  }

  @media only screen and (max-width: 768px) {
    .text-center {
      top 40%;
      width: 80%;
      height: 80%;
      max-height: 100%;
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
    $:active {
      background-color: #e87121;
    }
  }

  input {
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
  }

  #loginError {
    margin-top: 20px;
  }

`;

function Login() {
  const [values, setValues] = useState({
    email: "",
    password: "",
  });

  const handleChange = (e) => {
    e.persist();
    setValues((values) => ({
      ...values,
      [e.target.name]: Base64.encode(e.target.value),
    }));
  };
  const [error, guardarError] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log(values);
    userLogin(values)
      .then((response) => {
        if (response.status === 200) {
          localStorage.setItem("token", response.data.token);
          localStorage.setItem("refreshToken", response.data.refreshToken);
          window.location.replace("/");
        } else {
          guardarError("Algo salio mal!!");
        }
      })
      .catch((err) => {
        if (err && err.response) {
          switch (err.response.status) {
            case 401:
              guardarError("Algo salio mal!! correo o contraseña incorrectos");
              break;
            default:
              guardarError("Algo salio mal!!");
          }
        } else {
          guardarError("Algo salio mal!!");
        }
      });
  };

  let componente;
  if (error !== "") {
    componente = <Error error={error} />;
  } else {
    componente = null;
  }

  return (
    <Styles>
      <div className="text-center">
        <main className="form-signin">
          <form id="inputs" onSubmit={handleSubmit}>
            <a href="/">
              <img
                className="mb-4"
                src={logo}
                alt=""
                width="200"
                height="200"
              />
            </a>
            <h1 className="h3 mb-3 fw-normal">Ingrese con su correo</h1>

            <div className="form-floating">
              <input
                type="email"
                name="email"
                className="form-control"
                id="email"
                placeholder="name@example.com"
                onChange={handleChange}
                required
              />
              <label htmlFor="floatingInput">Correo electronico</label>
            </div>
            <div className="form-floating">
              <input
                type="password"
                name="password"
                className="form-control"
                id="password"
                placeholder="Password"
                onChange={handleChange}
                required
              />
              <label htmlFor="floatingPassword">Contraseña</label>
            </div>
            <button className="mt-3 w-100 btn btn-lg btn-primary" type="submit">
              Entrar
            </button>
            <div id="loginError">{componente}</div>
            <p className="mt-5 mb-3 text-muted">
              <Link to="/forgotPassword">Olvide mi contraseña</Link>
            </p>
            <p className="mt-2 mb-3 text-muted">
              ¿No tienes cuenta?<a href="/register">Registrate</a>
            </p>
          </form>
        </main>
      </div>
    </Styles>
  );
}

export default Login;

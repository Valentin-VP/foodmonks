import { Link , Redirect, useParams, useLocation } from "react-router-dom"
import { Loading } from "../components/Loading";
import styled from "styled-components";
import { useState , useEffect} from "react";
import logo from "../assets/foodMonks-sinfondo.png";
import { cambiarPassword, checkPwdRecoveryToken } from "../services/Requests";
import { Alert } from "react-bootstrap";
import { Fragment } from "react";
import { Base64 } from "js-base64";

const Styles = styled.div`
  .text-center {
    position: relative;
    padding: 30px;
    display: flex;
    flex-direction: column;
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
    &:disabled {
      background-color: #aa3208;
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

function useQuery(){
    return new URLSearchParams(useLocation().search);
}

export default function ResetPasswordConfirm() {
    const [isValidandoToken, setIsValidandoToken] = useState(true);
    const [tokenInvalido, setTokenInvalido] = useState(false);
    const [isConfirmando, setIsConfirmando] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);
    const [confirmado, setConfirmado] = useState(false);
    const { token } = useParams();
    let query = useQuery();
    
    
    const [values, setValues] = useState({
        correo: "",
        password: "",
        confirmarPassword: "",
    });

    const handleChange = (e) => {
        e.persist();
        setValues({
            ...values,
            [e.target.name]: e.target.value
        })
    };

    const handleChange64 = (e) => {
        e.persist();
        setValues({
            ...values,
            [e.target.name]: Base64.encode(e.target.value)
        })
    };

    function validarPasswordForm() {
      return (
          (values.password.length > 0 && values.correo.length > 0) &&
          values.password === values.confirmarPassword
        );
    }

    async function validarToken() {
        setIsValidandoToken(true);
        await checkPwdRecoveryToken(query.get("email"), query.get("token"))
        .then((response)=>{
            if (response.status===200){
                setTokenInvalido(false);
                setIsValidandoToken(false);
            }else if(response.status===401){
                setTokenInvalido(true);
                setIsValidandoToken(false);
            }
        }).catch((error)=>{
            setTokenInvalido(true);
            setIsValidandoToken(false);
        })
    }

    function handleConfirmarClick(event) {
        event.preventDefault();
        setIsConfirmando(true);
        cambiarPassword(values.correo, values.password, query.get("token"))
        .then((response)=>{
            setConfirmado(true);
            setError(null);
            //clearRecoverEmail();
            setSuccess(<Alert variant="success" dismissible onClose={()=>{setSuccess(null)}}>{response.data}</Alert>)   
        }).catch((error)=>{
            setError(<Alert variant="danger" dismissible onClose={()=>{setError(null)}}>{error.response.data}</Alert>)
            setSuccess(null);
        });
        setIsConfirmando(false);
    }

    function renderConfirmacionForm() {
        return (
            <Styles>
                <Fragment>
                <div className="text-center">
                <main className="form-signin">
                <form id="inputs" onSubmit={handleConfirmarClick}>
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
                            name="correo"
                            className="form-control"
                            id="correo"
                            onChange={handleChange}
                            value={values.correo}
                            required
                        />
                        <label for="floatingInput">Correo</label>
                    </div>
                    <div className="form-floating">
                        <input
                            type="password"
                            name="password"
                            className="form-control"
                            id="password"
                            onChange={handleChange64}
                            required
                        />
                        <label for="floatingInput">Password</label>
                    </div>
                    <div className="form-floating">
                        <input
                            type="password"
                            name="confirmarPassword"
                            className="form-control"
                            id="confirmarPassword"
                            onChange={handleChange64}
                            required
                        />
                        <label for="floatingInput">Confirmar Password</label>
                    </div>
                    <button className="w-100 btn btn-lg btn-primary" type="submit" disabled={!validarPasswordForm()}>
                        Ingresar
                    </button>
                    <div id="rpAlert">
                    {success}
                    {error}
                    </div>
                </form>
                </main>
                </div>
                </Fragment>
            </Styles>
        );
        
    }

    useEffect(() => {
      validarToken()
    }, [])
    
    function renderMensajeDeSuceso() {
        return (
            <Styles>
                <Fragment>
                    <div className="text-center">
                        <img
                            className="mb-4"
                            src={logo}
                            alt=""
                            width="200"
                            height="200"
                        />
                        <p>Password reestablecido con suceso.</p>
                        <p>
                        <Link to="/">
                            Clic acá para volver al formulario de login.
                        </Link>
                        </p>
                    </div>
                </Fragment>
            </Styles>
        );
    }

    function renderLoading(){
        return (
            <Styles>
                <Fragment>
                    <div className="text-center">
                        <img
                            className="mb-4"
                            src={logo}
                            alt=""
                            width="200"
                            height="200"
                        />
                        <div className="form-floating">
                            <Loading />
                        </div>
                    </div>
                </Fragment>
            </Styles>
        )
    }

    return (
        <div className="ConfirmPassword">
            {isValidandoToken ? renderLoading() : tokenInvalido ? <Redirect to="/"></Redirect>: !confirmado ? renderConfirmacionForm() : renderMensajeDeSuceso()}
        </div>
    );
}
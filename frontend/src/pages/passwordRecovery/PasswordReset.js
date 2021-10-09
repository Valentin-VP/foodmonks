import "./PasswordReset.styles.css"
import React, { useState , useEffect} from "react";
import { Link, Redirect  } from "react-router-dom"
import { useFormFields } from "./useFormFields"
import LoaderButton from "./LoaderButton"
import { changePassword, recoverPassword } from "../../api/passwordRecoveryService";
import { FormGroup, FormControl, FormLabel } from "react-bootstrap";


export default function ResetPassword() {
    const [codigoEnviado, setCodigoEnviado] = useState(false);  
    //const [confirmado, setConfirmado] = useState(false);
    //const [isConfirmando, setIsConfirmando] = useState(false);
    const [isEnviandoCodigo, setIsEnviandoCodigo] = useState(false);
    const [redirect, setRedirect] = useState("");

    const [campos, handleOnChange] = useFormFields({
        code: "",
        email: "",
        password: "",
        confirmarPassword: "",
    });

    function validarEmail() {
        return campos.email.length > 0;
    }
    /*
    function validarPasswordForm() {
        return (
          campos.code.length > 0 &&
          campos.password.length > 0 &&
          campos.password === campos.confirmarPassword
        );
    }
*/
    async function handleEnviarCodigoClick(event) {
      event.preventDefault();
  
      setIsEnviandoCodigo(true);
      await recoverPassword(campos.email).then((response)=>{
        setCodigoEnviado(true);
      }).catch((error)=>{
        setIsEnviandoCodigo(false);
      });
    }
    
    function renderEmailForm() {
        return (
          <form onSubmit={handleEnviarCodigoClick}>
            <FormGroup controlId="email">
              <FormLabel>Email</FormLabel>
              <FormControl autoFocus type="email" value={campos.email} onChange={handleOnChange} />
            </FormGroup>
            <LoaderButton block type="submit" isLoading={isEnviandoCodigo} disabled={!validarEmail()}>
              Enviar Confirmación
            </LoaderButton>
          </form>
        );
      }
    
    /*useEffect(() =>{    
        document.title = `Code is ${campos.code}`;  
    }, [campos.code]);*/

    function renderMensajeDeSuceso() {
      return (
        <div className="success">
          <p>Email enviado.</p>
          <p>
            <Link to="/">
              Clic acá para volver al formulario de login.
            </Link>
          </p>
        </div>
      );
  }

    return (
      <div className="ResetPassword">
        {!codigoEnviado ? renderEmailForm() : renderMensajeDeSuceso()}
      </div>
    );
}//{(!redirect ? renderEmailForm() : <Redirect to={redirect} />)}
//{!codigoEnviado ? renderEmailForm() : !confirmado ? renderConfirmacionForm() : renderMensajeDeSuceso()}
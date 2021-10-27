import { React, Fragment, useState } from "react";
import styled from "styled-components";
import { Form } from "react-bootstrap";
import logo from "../../assets/foodMonks-sinfondo.png";
import usePlacesAutocomplete, {
  getGeocode,
  getLatLng,
} from "use-places-autocomplete";
import {
  Combobox,
  ComboboxInput,
  ComboboxPopover,
  ComboboxList,
  ComboboxOption,
} from "@reach/combobox";
import "@reach/combobox/styles.css";
import { registrarCliente } from "../../services/Requests";
import { Alerta } from "../../components/Alerta";
import { Base64 } from "js-base64";

const Styles = styled.div`
  .text-center {
    position: absolute;
    left: 50%;
    -webkit-transform: translate(-50%);
    transform: translate(-50%);
  }

  .form-signin {
    width: 500px;
  }

  .form-floating {
    margin-bottom: 10px;
  }

  button {
    color: white;
    background-color: #e87121;
    border: none;
    margin-bottom: 30px;
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
      background-color: #e87121;
    }
    &:hover {
      background-color: #da6416;
    }
    $:active {
      background-color: black !important;
    }
  }

  input {
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
  }

  .busqueda {
    height: 55px;
    width: 500px;
    margin-bottom: 10px;
    .form-control {
      height: 55px;
      width: 500px;
      border-radius: 5px;
    }
  }

  #esquina {
    margin-bottom: 10px;
    height: 55px;
  }
`;

function RegistroCliente() {
  const registro = {
    nombre: "",
    apellido: "",
    correo: "",
    password: "",
    direccion: {
      calle: "",
      numero: "",
      esquina: "",
      detalles: "",
      latitud: "",
      longitud: "",
    },
  };

  // esto es para la direccion
  const [alerta, setAlerta] = useState(null);
  const [tipoError, setTipo] = useState(null);

  const {
    ready,
    value,
    suggestions: { status, data },
    setValue,
    clearSuggestions,
  } = usePlacesAutocomplete({
    requestOptions: {
      /*-32.522779, -55.765835 coordenadas del medio de uruguay*/
      // location: { lat: () => 43.6532, lng: () => -79.3832 },
      // radius: 350 * 1000, //a 350 kilometros a la redonda va a buscar direcciones
    },
    debounce: 300,
  });

  const handleInput = (e) => {
    // actualiza el texto en el imput
    setValue(e.target.value);
  };

  const handleSelect = (address) => {
    //cuando un suario selecciona un lugar, podemos remplazar la palabra clave sin necesidad de hacer un request a la API
    // setteando el segundo parametro en false
    setValue(address, false);
    clearSuggestions();
  };

  // hasta aca viene la direccion

  const handleSubmit = async (event) => {
    event.preventDefault(); //para que no haga reload la pagina por el form
    var address = document.getElementById("address").value;
    getGeocode({ address })
      .then((results) => {
        getLatLng(results[0]).then(({ lat, lng }) => {
          registro.direccion.latitud = lat;
          registro.direccion.longitud = lng;
        });
        registro.direccion.calle = results[0].address_components[1].long_name;
        registro.direccion.numero = results[0].address_components[0].long_name;
        registro.nombre = document.getElementById("nombre").value;
        registro.apellido = document.getElementById("apellido").value;
        registro.correo = document.getElementById("correo").value;
        registro.direccion.esquina = document.getElementById("esquina").value;
        registro.direccion.detalles = document.getElementById("detalles").value;
        var pass1 = document.getElementById("password1").value;
        var pass2 = document.getElementById("password2").value;
        if (pass1 === pass2) {
          registro.password = Base64.encode(pass1);
          console.log(registro);
          registrarCliente(registro).then((response) => {
            if (response.status === 201) {
              setAlerta("Registro hecho con exito");
              setTipo("success");
              setTimeout(() => { window.location.replace("/"); }, 5000); //para esperar 5 segundos y redireccionar
            }
          }).catch(() =>{
              setAlerta("La direccion debe de tener la calle y el número de puerta");
              setTipo("danger");        
          });
        } else {
          setAlerta("Las contraseñas no coinciden");
          setTipo("danger");
        }
      })
      .catch((error) => {
        setAlerta(error);
        setTipo("danger");
      });
    // registrarCliente(registro);
  };

  let errorMsg;
  if (alerta !== null) {
    errorMsg = <Alerta msg={alerta} tipo={tipoError} />;
  } else {
    errorMsg = null;
  }

  return (
    <Styles>
      <Fragment>
        <div className="text-center">
          <main className="form-signin">
            <form id="inputs" onSubmit={handleSubmit}>
              <a href="/">
                <img className="" src={logo} alt="" width="200" height="200" />
              </a>
              <h2 className="mb-3">Registrate</h2>

              <div className="form-floating">
                <input
                  type="text"
                  className="form-control"
                  id="nombre"
                  placeholder="Nombre"
                  required
                />
                <label htmlFor="floatingInput">Nombre</label>
              </div>
              <div className="form-floating">
                <input
                  type="text"
                  className="form-control"
                  id="apellido"
                  placeholder="Apellido"
                  required
                />
                <label htmlFor="floatingInput">Apellido</label>
              </div>
              <div className="form-floating">
                <input
                  type="email"
                  className="form-control"
                  id="correo"
                  placeholder="name@example.com"
                  required
                />
                <label htmlFor="floatingInput">Correo electronico</label>
              </div>
              <div className="form-floating">
                <input
                  type="password"
                  className="form-control"
                  id="password1"
                  placeholder="Password"
                  required
                />
                <label htmlFor="floatingPassword">Contraseña</label>
              </div>

              <div className="form-floating">
                <input
                  type="password"
                  className="form-control"
                  id="password2"
                  placeholder="Password"
                  required
                />
                <label htmlFor="floatingPassword">Repite la contraseña</label>
              </div>
              {/* Direccion */}
              <label className="tituloDir">Dirección</label>
              <Combobox className="busqueda" onSelect={handleSelect}>
                <ComboboxInput
                  className="form-control"
                  id="address"
                  value={value}
                  onChange={handleInput}
                  disabled={!ready}
                  placeholder="Dirección"
                  type="search"
                  autoComplete="off"
                />
                <ComboboxPopover>
                  <ComboboxList>
                    {status === "OK" &&
                      data.map(({ id, description }) => (
                        <ComboboxOption key={id} value={description} />
                      ))}
                  </ComboboxList>
                </ComboboxPopover>
              </Combobox>
              <Form.Control id="esquina" placeholder="Esquina" />
              <Form.Control
                as="textarea"
                id="detalles"
                placeholder="Detalles"
                rows={3}
              />
              <br />
              {errorMsg}
              <button className="w-100 btn btn-lg btn-primary">
                Registrarme
              </button>
            </form>
            <p className="mt-2 mb-3 text-muted">
              ¿Ya tienes cuenta?<a href="/login">Inicia sesión</a>
            </p>
            <p className="mt-2 mb-3 text-muted">
              ¿Eres una empresa?
              <a href="/registroRestaurante">Registrate como restaurante</a>
            </p>
          </main>
        </div>
      </Fragment>
    </Styles>
  );
}

export default RegistroCliente;

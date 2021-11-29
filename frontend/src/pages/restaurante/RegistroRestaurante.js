import { React, Fragment, useState } from "react";
import styled from "styled-components";
import { Form, Button } from "react-bootstrap";
import logo from "../../assets/foodMonks-sinfondo.png";
import arrow from "../../assets/arrow.png";
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
import { Alerta } from "../../components/Alerta";
import { Base64 } from "js-base64";
import { storage } from "../../Firebase";

const Styles = styled.div`
  * {
    margin: 0;
    box-sizing: border-box;
  }

  .container {
    position: relative;
    left: 50%;
    transform: translate(-50%);
    max-width: 30%;
  }

  @media only screen and (max-width: 768px) {
    .container {
      max-width: 100%;
      width: 80%;
      height: 80%;
      max-height: 100%;
    }
  }

  .form-floating {
    margin-bottom: 13px;
  }

  .busqueda {
    margin-bottom: 10px;
    .form-control {
      height: 55px;
      border-radius: 5px;
    }
  }
  #esquina {
    height: 55px;
    margin-bottom: 10px;
  }

  Button {
    width: 70%;
    color: white;
    background-color: #e87121;
    border: none;
    padding: 15px;
    margin-top: 30px;
    font-size: 25px;
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
      background-color: #e87121;
    }
    &:hover {
      background-color: #da6416;
    }
    &:active {
      background-color: black !important;
    }
  }

  .ultimo {
    padding-bottom: 30px;
  }

  .flecha {
    position: relative;
    margin-left: 15px;
    bottom: 2px;
  }
`;

function RegistroRestaurante() {
  sessionStorage.getItem("registroRestaurante");

  const restaurante = {
    nombre: "",
    apellido: "",
    correo: "",
    password: "",
    nombreRestaurante: "",
    rut: "",
    telefono: "",
    cuentaPaypal: "",
    descripcion: "",
    url: "",
    direccion: {
      calle: "",
      numero: "",
      esquina: "",
      detalles: "",
      latitud: "",
      longitud: "",
    },
    menus: [],
  };

  const [alerta, setAlerta] = useState(null);
  const [tipoError, setTipo] = useState();

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

  const handleSubmit = async (event) => {
    event.preventDefault(); //para que no haga reload la pagina por el form
    var pass1 = document.getElementById("password1").value;
    var pass2 = document.getElementById("password2").value;
    if (pass1 === pass2) {
      //si las contraseñas coinciden
      var address = document.getElementById("address").value;
      getGeocode({ address })
        .then((results) => {
          //si obtengo la direccion
          getLatLng(results[0]).then(({ lat, lng }) => {
            restaurante.direccion.latitud = lat;
            restaurante.direccion.longitud = lng;
            restaurante.direccion.calle =
              results[0].address_components[1].long_name;
            restaurante.direccion.numero =
              results[0].address_components[0].long_name;
            restaurante.nombre = document.getElementById("nombre").value;
            restaurante.apellido = document.getElementById("apellido").value;
            restaurante.correo = document.getElementById("correo").value;
            restaurante.nombreRestaurante = document.getElementById("nombreRestaurante").value;
            restaurante.rut = document.getElementById("rut").value;
            restaurante.telefono = document.getElementById("telefono").value;
            restaurante.cuentaPaypal = document.getElementById("paypal").value;
            restaurante.descripcion = document.getElementById("descripcion").value;
            restaurante.direccion.esquina =
              document.getElementById("esquina").value;
            restaurante.direccion.detalles =
              document.getElementById("detalles").value;
            restaurante.password = Base64.encode(pass1);
            var img = document.getElementById("img").files[0];
            //si se selecciona una imagen
            const uploadTask = storage.ref(`/restaurantes/${img.name}`).put(img);
            uploadTask.on(
              "state_changed",
              (snapshot) => {}, //el snapshot tiene que ir
              (error) => {
                console.log(error.message);
                setAlerta("Error al subir la imagen");
                setTipo("danger");
              },
              () => {
                setAlerta(null);
                storage
                  .ref("restaurantes")
                  .child(img.name)
                  .getDownloadURL()
                  .then((imgUrl) => {
                    restaurante.url = imgUrl;
                    //en este punto el restaurante esta listo para agregarlo al objeto
                    const json = {
                      nroMenu: 1,
                    };
                    //para que lo guarde como string porque el storage no tiene otra cosa
                    json["restaurante"] = restaurante;
                    sessionStorage.setItem(
                      "registroRestaurante",
                      JSON.stringify(json)
                    );
                    window.location.reload();
                  });
              }
            );
          });
        })
        .catch((error) => {
          //si da error al obtener la direccion
          setAlerta("Ha ocurrido un error");
          setTipo("danger");
        });
    } else {
      setAlerta("Las contraseñas no coinciden");
      setTipo("danger");
    }
  };

  let errorMsg;
  if (alerta !== null) {
    errorMsg = <Alerta className="mt-5" msg={alerta} tipo={tipoError} />;
  } else {
    errorMsg = null;
  }

  return (
    <Styles>
      <Fragment>
        <div className="container text-center">
          <a href="/">
            <img className="" src={logo} alt="" width="200" height="200" />
          </a>
          <h2 className="mb-3">Registrate</h2>
          <form onSubmit={handleSubmit}>
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

            <h5 className="mb-1 mt-4">Restaurante</h5>
            <div className="form-floating">
              <input
                type="text"
                className="form-control"
                id="nombreRestaurante"
                placeholder="nombre del restaurante"
                required
              />
              <label htmlFor="floatingInput">Nombre del restaurante</label>
            </div>
            <div className="form-floating">
              <input
                type="number"
                className="form-control"
                id="rut"
                placeholder="rut"
                min="0"
                required
              />
              <label htmlFor="floatingInput">RUT</label>
            </div>
            <div className="form-floating">
              <input
                type="number"
                className="form-control"
                id="telefono"
                placeholder="Telefono"
                min="0"
                required
              />
              <label htmlFor="floatingInput">Telefono</label>
            </div>
            <div className="form-floating">
              <input
                type="text"
                className="form-control"
                id="paypal"
                placeholder="PayPal"
                required
              />
              <label htmlFor="floatingInput">PayPal</label>
            </div>
            <div className="form-floating">
              <input
                type="text"
                className="form-control"
                id="descripcion"
                placeholder="Descripción"
                required
              />
              <label htmlFor="floatingInput">Descripción</label>
            </div>
            <label className="mb-1">Logo</label>
            <Form.Control type="file" size="lg" id="img" required />
            <label className="mb-1 mt-4">Dirección</label>
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
                required
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
              className="mb-4"
              as="textarea"
              id="detalles"
              placeholder="Detalles"
              rows={3}
            />
            {errorMsg}
            <Button className="btn-lg" type="submit">
              Siguiente
              <span className="flecha">
                <img src={arrow} alt="arrow" width="25" />
              </span>
            </Button>
          </form>
          <p className="mt-2 mb-3 text-muted">
            ¿Ya tienes cuenta?<a href="/">Inicia sesión</a>
          </p>
          <a href="/register" className="ultimo">
            Registrate como cliente
          </a>
        </div>
      </Fragment>
    </Styles>
  );
}

export default RegistroRestaurante;

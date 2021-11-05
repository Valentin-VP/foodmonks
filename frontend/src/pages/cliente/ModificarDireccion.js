import React, { useState } from "react";
import styled from "styled-components";
import { Button, Form, Alert } from "react-bootstrap";
import { modificarDireccion } from "../../services/Requests";
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

const Styles = styled.div`
  #page-container {
    background-image: url("https://images.pexels.com/photos/6419720/pexels-photo-6419720.jpeg?auto=compress&cs=tinysrgb&dpr=3&h=750&w=1260");
    filter: blur(6px);
    background-position: center;
    background-repeat: no-repeat;
    background-size: cover;
    margin-top: -3.5rem;
    margin-bottom: -3.5rem;
  }

  h4 {
    margin-bottom: 20px;
  }

  .form-alta {

    position: absolute;
    left: 50%;
    top 45%;
    -webkit-transform: translate(-50%, -50%);
    transform: translate(-50%, -50%);
    width: 400px;
    background: white;
    padding: 30px;
    margin: auto;
    border-radius: 5px;
    box-shadow: 7px 13px 37px #000;
  }

  .form-floating {
    margin-bottom: 13px;
  }

  .botones {
      text-align: right;
      Button{
          margin-left: 5px;
      }
  }
`;

function ModificarDireccion() {
  const [error, setError] = useState(null);
  // esto es para la direccion

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
    console.log(data);
    setValue(e.target.value);
  };

  const handleSelect = (address) => {
    //cuando un suario selecciona un lugar, podemos remplazar la palabra clave sin necesidad de hacer un request a la API
    // setteando el segundo parametro en false
    setValue(address, false);
    clearSuggestions();
  };

  // hasta aca viene la direccion

  const onSubmit = (event) => {
    event.preventDefault(); //para que no haga reload la pagina por el form
    var oldDir = JSON.parse(sessionStorage.getItem("dirModificar"));
    const dir = {
      id: oldDir.id,
      numero: "",
      calle: "",
      esquina: "",
      detalles: "",
      latitud: "",
      longitud: "",
    };
    var address = document.getElementById("address").value;
    getGeocode({ address }).then((results) => {
      getLatLng(results[0]).then(({ lat, lng }) => {
        dir.calle = results[0].address_components[1].long_name;
        dir.numero = results[0].address_components[0].long_name;
        dir.esquina = document.getElementById("esquina").value;
        dir.detalles = document.getElementById("detalles").value;
        dir.latitud = lat;
        dir.longitud = lng;
        modificarDireccion(dir, dir.id)
          .then((response) => {
            console.log(response);
            sessionStorage.removeItem("dirModificar");
            window.location.replace("/perfil");
          })
          .catch((error) => {
            console.log(dir);
            setError(
              <Alert variant="danger">
                Error al modificar, recuerda que la dirección debe tener calle y
                numero
              </Alert>
            );
          });
      });
    });
  };

  return (
    <Styles>
      <div id="page-container"></div>
      <section className="form-alta">
        <Form onSubmit={onSubmit}>
          <h4>Modificar Dirección</h4>
          <div className="mb-2">
            <Combobox className="busqueda mb-3" onSelect={handleSelect}>
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
            <Form.Control className="mb-3" id="esquina" placeholder="Esquina" />
            <Form.Control
              as="textarea"
              id="detalles"
              placeholder="Detalles"
              rows={3}
            />
          </div>
          <hr />
          {error}
          <div className="botones mt-3">
            <Button id="submit" variant="success" type="submit">
              Modificar
            </Button>
            <Button
              variant="danger"
              onClick={() => window.location.replace("/perfil")}
            >
              Cancelar
            </Button>
          </div>
        </Form>
      </section>
    </Styles>
  );
}

export default ModificarDireccion;

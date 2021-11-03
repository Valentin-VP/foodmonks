import React, { useState, useEffect } from "react";
import { Form, Button } from "react-bootstrap";
import styled from "styled-components";
import {
  agregarDireccion,
  fetchUserData,
  eliminarDireccion,
} from "../../services/Requests";
import { Loading } from "../../components/Loading";
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
import { Noti } from "../../components/Notification";

const Styles = styled.div`
  .pp {
    height: 140px;
    width: 140px;
    border-radius: 50%;
    color: white;
    background-color: #e87121;
    box-shadow: none;
    border: none;
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
      background-color: #e87121;
    }
    &:hover {
      background-color: #da6416;
      img {
        transform: scale(1.5);
      }
    }
    $:active {
      background-color: #e87121;
    }
  }

  img {
    transition: all 0.8s;
    color: black;
  }

  .nombre {
    font-weight: bold;
    font-family: "Poppins", sans-serif;
  }

  .direccion {
    text-decoration: none;
    font-weight: bold;
  }

  .oButton {
    margin-top: 30px;
    color: white;
    background-color: #e87121;
    border: none;
    width: 40%;
    height: 42px;
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

  #dir {
    margin-left: 5px;
    margin-right: 10px;
    border-radius: 0.25rem;
    background-color: white;
    &:focus {
      box-shadow: none;
      border-color: #e87121;
    }
  }

  .btn-close {
    margin: 0;
    border: 0;
    padding: 0;
    background: hsl(216, 100, 50);
    border-radius: 50%;
    width: 50px;
    height: 50px;
    display: flex;
    flex-flow: column nowrap;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    transition: all 150ms;
    background-color: white;

    .icon-cross {
      @include cross(40px, #fff, 6px);
    }

    &:hover,
    &:focus {
      transform: rotateZ(90deg);
      background: hsl(216, 100, 40);
      box-shadow: none;
      background-color: white;
    }
  }

  .join {
    font-size: 14px;
    color: #a0a0a0;
    font-weight: bold;
  }
  .date {
    background-color: #ccc;
  }
  .numero{
    padding-left: 35px;
  }

  .eliminar {
    text-align: right;
  }
`;

function PerfilCliente() {
  const [perfil, setPerfil] = useState();
  const [isLoading, setLoading] = useState(true);
  const [alerta, setAlerta] = useState(null);

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
    setValue(e.target.value);
  };

  const handleSelect = (address) => {
    //cuando un suario selecciona un lugar, podemos remplazar la palabra clave sin necesidad de hacer un request a la API
    // setteando el segundo parametro en false
    setValue(address, false);
    clearSuggestions();
  };

  // hasta aca viene la direccion

  useEffect(() => {
    fetchUserData().then((response) => {
      setPerfil(response.data);
      setLoading(false);
    });
  }, []);

  const deleteDireccion = (direccion) => {
    eliminarDireccion(direccion.latitud, direccion.longitud)
      .then(() => {
        window.location.reload();
      })
      .catch((error) => {
        Noti("❌ No te puedes quedar sin direcciones");
      });
  };

  const addDir = (event) => {
    event.preventDefault(); //para que no haga reload la pagina por el form
    const dir = {
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
        agregarDireccion(dir)
          .then((response) => {
            console.log(response);
            window.location.reload();
          })
          .catch((error) => {
            console.log(dir);
            Noti("❌ Error al ingresar la dirección");
          });
      });
    });
  };

  if (isLoading) {
    return <Loading />;
  }

  return (
    <Styles>
      <div className="container rounded bg-white mt-5">
        <div className="row">
          <div className="col-md-4 border-right">
            <div className="d-flex flex-column align-items-center text-center p-3 py-5">
              <button className="pp btn btn-secondary">
                <img
                  alt="profilePic"
                  src="https://www.pinclipart.com/picdir/big/496-4968268_profile-icon-png-white-clipart.png"
                  height="50"
                  width="50"
                />
              </button>
              <br />
              <span className="nombre">
                {perfil.nombre} {perfil.apellido}
              </span>
              <span className="mail">{perfil.correo}</span>
              <br />
              <span>{perfil.calificacion}⭐</span>
              <br/>
              <span className="direccion">Direcciones</span>
              {perfil.direcciones.map((direccion, index) => {
                return (
                  <div
                    className="d-flex justify-content-between align-items-center"
                    key={index}
                  >
                    <span className="numero">{index + 1}-</span>
                    <input
                      className="form-control"
                      id="dir"
                      value={direccion.calle + " " + direccion.numero}
                      readOnly
                    />
                    <button
                      type="button"
                      className="btn-close"
                      onClick={() => deleteDireccion(direccion)}
                    >
                      <span className="icon-cross"></span>
                    </button>
                  </div>
                );
              })}
              <div className=" px-2 rounded mt-4 date ">
                <span className="join">Se unió el {perfil.fechaRegistro}</span>
              </div>
            </div>
          </div>
          <div className="col-md-4 border-right">
            <div className="p-3 pt-5 mt-5">
              <Form onSubmit={addDir}>
                <h4>Añadir una direccion</h4>
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
                <Form.Control
                  className="mb-3"
                  id="esquina"
                  placeholder="Esquina"
                />
                <Form.Control
                  as="textarea"
                  id="detalles"
                  placeholder="Detalles"
                  rows={3}
                />
                <div className="text-center">
                  <Button className="oButton text-center" id="submit" type="submit">
                    Agregar
                  </Button>
                </div>
              </Form>
            </div>
            {/* EDITAR PERFIL -------------------------------------------------------------------------------------------- */}
            <div className="p-3 pt-2">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h4 className="text-right">Editar perfil</h4>
              </div>
              <div className="row mt-3">
                <div className="col-md-12 mb-2">
                  <label className="labels">Nombre</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="nombre"
                  />
                </div>
                <div className="col-md-12">
                  <label className="labels">Apellido</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="apellido"
                  />
                </div>
              </div>
              <div className="text-center">
                <Button
                  className="oButton"
                  type="button"
                >
                  Editar
                </Button>
              </div>
            </div>
          </div>
          <div className="col-md-4 border-right">
            <div className="p-3 pt-5 mt-5">
              <h3>Eliminar Cuenta</h3>
              <p>
                Si eliminas tu cuenta, no podrás recuperar el contenido ni la
                información. Esta decisión es definitiva.
              </p>
              <div className="eliminar">
                <button className="eliminar btn btn-danger">Eliminar cuenta</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Styles>
  );
}

export default PerfilCliente;

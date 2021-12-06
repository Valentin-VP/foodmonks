import React, { useState, useEffect } from "react";
import { Form, Button } from "react-bootstrap";
import styled from "styled-components";
import {
  agregarDireccion,
  fetchUserData,
  eliminarDireccion,
  editNombre,
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
import { Noti, NotiError } from "../../components/Notification";
import {
  clearState,
  eliminarCuentaClientePropia,
} from "../../services/Requests";
import {
  MdOutlineModeEditOutline,
  CgClose,
  BiHistory,
  AiFillStar,
} from "react-icons/all";
import Modal, { ModalProvider } from "styled-react-modal";

const StyledModal = Modal.styled`
  border-radius: 5px;
  padding: 1.5%;
  width: 25%;
  align-items: center;
  justify-content: center;
  background-color: white;
  overflow-y:inherit !important;

  .cuerpo{
    margin-bottom: 15px;
  }
  .abajo{
    text-align: right;
  }
  Button {
    margin-left: 5px;
  }
`;

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
    border-radius: 5px;
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

  CgClose {
    width: 500px;
  }

  .dirBtn {
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

    &:hover {
      transform: rotateZ(90deg);
      background-color: transparent;
      &:focus {
        background: hsl(216, 100, 40);
        box-shadow: none;
      }
    }
  }

  .react-icons {
    vertical-align: middle;
  }

  .join {
    font-size: 14px;
    color: #a0a0a0;
    font-weight: bold;
  }
  .date {
    background-color: #ccc;
  }
  .numero {
    padding-left: 35px;
  }

  .eliminar {
    text-align: right;
  }

  .hButton {
    paddin: 5rem;
    color: white;
    background-color: #e87121;
    border: none;
    border-radius: 5px;
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

  .nuevo {
    width: 100%;
    color: white;
    border-radius: 3rem;
    background-color: #262626;
    padding: 0.2rem;
  }

  .estrella {
    vertical-align: bottom;
  }
`;

function PerfilCliente() {
  const [perfil, setPerfil] = useState();
  const [isLoading, setLoading] = useState(true);
  // para el modal -----------------------------------------------------------------------------------------------
  const [isOpen, setIsOpen] = useState(false);

  const toggleModal = (e) => {
    setIsOpen(!isOpen);
  };
  //termina para el modale ---------------------------------------------------------------------------------------

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

  useEffect(() => {
    fetchUserData().then((response) => {
      setPerfil(response.data);
      setLoading(false);
    });
  }, []);

  const deleteDireccion = (direccion) => {
    eliminarDireccion(direccion.id)
      .then(() => {
        window.location.reload();
      })
      .catch((error) => {
        NotiError(error.response.data);
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
            window.location.reload();
          })
          .catch((error) => {
            NotiError("Error al ingresar la dirección");
          });
      });
    });
  };

  const editarNombre = (e) => {
    e.preventDefault();
    var nombre = perfil.nombre;
    var apellido = perfil.apellido;
    if (document.getElementById("editNombre").value !== "")
      nombre = document.getElementById("editNombre").value;
    if (document.getElementById("editApellido").value !== "")
      apellido = document.getElementById("editApellido").value;
    editNombre(nombre, apellido)
      .then((response) => {
        window.location.reload();
      })
      .catch(() => {
        Noti("Error al cambiar el nombre");
      });
  };

  const editDireccion = (direccion) => {
    sessionStorage.removeItem("dirModificar");
    sessionStorage.setItem("dirModificar", JSON.stringify(direccion));
    window.location.replace("/modificarDireccion");
  };

  const eliminarCuenta = () => {
    eliminarCuentaClientePropia()
      .then((response) => {
        clearState();
      })
      .catch((error) => {
        NotiError(error.response.data);
      });
  };

  if (isLoading) {
    return <Loading />;
  }

  return (
    <Styles>
      <ModalProvider>
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
                {perfil.cantidadCalificaciones < 10 ? (
                  <div>
                    <label className="nuevo">NUEVO</label>
                    <span className="mt-2">
                      {perfil.calificacion}
                      <AiFillStar
                        className="estrella"
                        color="gold"
                        size="1.5rem"
                      />
                    </span>
                  </div>
                ) : (
                  <span>
                    {perfil.calificacion}
                    <AiFillStar className="estrella" color="gold" size="1rem" />
                  </span>
                )}
                <br />
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

                      <button className="dirBtn">
                        <MdOutlineModeEditOutline
                          color="#3d3d3d"
                          fontSize="1.5rem"
                          onClick={() => editDireccion(direccion)}
                        />
                      </button>
                      <button
                        className="dirBtn"
                        onClick={() => deleteDireccion(direccion)}
                      >
                        <CgClose color="#3d3d3d" fontSize="1.5rem" />
                      </button>
                    </div>
                  );
                })}
                <div className=" px-2 rounded mt-4 date ">
                  <span className="join">
                    Se unió en {perfil.fechaRegistro}
                  </span>
                </div>
              </div>
            </div>
            <div className="col-md-4 border-right">
              <div className="p-3 pt-5 mt-5">
                <Form onSubmit={addDir}>
                  <h4>Añadir una dirección</h4>
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
                    <Button
                      className="oButton text-center"
                      id="submit"
                      type="submit"
                    >
                      Agregar
                    </Button>
                  </div>
                </Form>
              </div>
              {/* EDITAR PERFIL -------------------------------------------------------------------------------------------- */}
              <Form onSubmit={editarNombre}>
                <div className="p-3 pt-2">
                  <div className="d-flex justify-content-between align-items-center mb-3">
                    <h4 className="text-right">Editar perfil</h4>
                  </div>
                  <div className="row mt-3">
                    <div className="col-md-12 mb-2">
                      <label className="labels">Nombre</label>
                      <input
                        id="editNombre"
                        type="text"
                        className="form-control"
                        placeholder="nombre"
                      />
                    </div>
                    <div className="col-md-12">
                      <label className="labels">Apellido</label>
                      <input
                        id="editApellido"
                        type="text"
                        className="form-control"
                        placeholder="apellido"
                      />
                    </div>
                  </div>
                  <div className="text-center">
                    <Button className="oButton" type="submit">
                      Editar
                    </Button>
                  </div>
                </div>
              </Form>
            </div>
            <div className="col-md-4 border-right">
              <div className="p-3 pt-5 mt-5">
                <h3>Historial de pedidos</h3>
                <p>
                  En esta sección se encuentra el historial de los pedidos que
                  has hecho
                </p>
                <div className="eliminar">
                  <button
                    className="btn hButton"
                    onClick={() => {
                      window.location.replace("/listadoPedidos");
                    }}
                  >
                    Historial <BiHistory color="white" size="1.5rem" />
                  </button>
                </div>
              </div>
              <div className="p-3 pt-5 mt-5">
                <h3>Eliminar Cuenta</h3>
                <p>
                  Si eliminas tu cuenta, aún podrás seguir teniendo acceso a tus
                  datos si te registras nuevamente. Tus pedidos e información
                  seguiran guardados.
                </p>
                <div className="eliminar">
                  <button className="btn btn-danger" onClick={toggleModal}>
                    Eliminar cuenta
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <StyledModal
          isOpen={isOpen}
          onBackgroundClick={toggleModal}
          onEscapeKeydown={toggleModal}
        >
          <h2>Eliminar cuenta</h2>
          <hr />
          <div className="cuerpo">
            <span>¿Seguro que desea eliminar su cuenta?</span>
          </div>
          <div className="abajo">
            <Button variant="danger" onClick={eliminarCuenta}>
              Eliminar cuenta
            </Button>
            <Button variant="secondary" onClick={toggleModal}>
              Cancelar
            </Button>
          </div>
        </StyledModal>
      </ModalProvider>
    </Styles>
  );
}

export default PerfilCliente;

import React, { useState } from "react";
import styled from "styled-components";
import {
  Button,
  FloatingLabel,
  Form,
  ButtonGroup,
  ProgressBar,
} from "react-bootstrap";
import { storage } from "../../Firebase";
import { Alerta } from "../../components/Alerta";
import { registrarRestaurante } from "../../services/Requests";

const Styles = styled.div`
  * {
    box-sizing: border-box;
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


    Button {
      width: 100%;
      color: white;
      background-color: #e87121;
      border: none;
      padding: 15px;
      margin-top: 15px;
      &:focus {
        box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
      }
      &:hover {
        background-color: #da6416;
      }
      $:active {
        background-color: black !important;
      }
    }
  }

  .form-floating {
    margin-bottom: 13px;
  }

  .bGroup{
      width: 100%;
  }
`;

function RegistroAltaMenu() {
  const state = {
    img: "",
  };

  let menu = {
    nombre: "",
    price: "",
    descripcion: "",
    multiplicador: "0",
    categoria: "",
    visibilidad: true,
    imagen: "",
  };

  let categorias = [
    { nombre: "PIZZAS" },
    { nombre: "HAMBURGUESAS" },
    { nombre: "BEBIDAS" },
    { nombre: "COMBOS" },
    { nombre: "MINUTAS" },
    { nombre: "POSTRES" },
    { nombre: "PASTAS" },
    { nombre: "COMIDAARABE" },
    { nombre: "SUSHI" },
    { nombre: "OTROS" },
  ];

  const [alerta, setAlerta] = useState(null);
  const [tipoError, setTipo] = useState();
  const [uploadBar, setUploadBar] = useState(null);

  const handleChange = (e) => {
    e.persist();
    state[e.target.name] = e.target.value;
  };

  const onCancel = () => {
    sessionStorage.clear();
    window.location.replace("/");
  };

  const onSubmit = (event) => {
    event.preventDefault(); //para que no haga reload la pagina por el form
    var json = JSON.parse(sessionStorage.getItem("registroRestaurante"));
    menu.nombre = document.getElementById("nombre").value;
    var unico = true;
    var menus = json.restaurante.menus;
    menus.forEach((element) => {
      if (element.nombre === menu.nombre)
        //veo si no hay un menu con ese nombre
        unico = false;
    });
    if (unico) {
      menu.categoria = document.getElementById("categoria").value;
      menu.descripcion = document.getElementById("descripcion").value;
      menu.price = document.getElementById("price").value;
      if (document.getElementById("img").files[0] !== undefined) {
        //si se selecciona una imagen
        var img = document.getElementById("img").files[0];
        const uploadTask = storage.ref(`/menus/${img.name}`).put(img);
        uploadTask.on(
          "state_changed",
          (snapshot) => {
            let percentage =
              (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
            setUploadBar(<ProgressBar now={percentage} />);
          },
          (error) => {
            setAlerta("Error al subir la imagen");
            setTipo("danger");
          },
          () => {
            storage
              .ref("menus")
              .child(img.name)
              .getDownloadURL()
              .then((url) => {
                menu.imagen = url;
                var json = JSON.parse(
                  sessionStorage.getItem("registroRestaurante")
                );
                json.restaurante.menus.push(menu);
                json.nroMenu++;
                sessionStorage.setItem(
                  "registroRestaurante",
                  JSON.stringify(json)
                );
                window.location.reload();
              });
          }
        );
      } else {
        menu.imagen = process.env.REACT_APP_GENERIC_MENU; //cargo la imagen generica
        json.restaurante.menus.push(menu);
        json.nroMenu++;
        sessionStorage.setItem("registroRestaurante", JSON.stringify(json));
        window.location.reload();
      }
    } else {
      setAlerta("El nombre del menu debe ser unico");
      setTipo("danger");
    }
  };

  const onEnd = (event) => {
    event.preventDefault();
    var json = JSON.parse(sessionStorage.getItem("registroRestaurante"));
    menu.nombre = document.getElementById("nombre").value;
    var unico = true;
    var menus = json.restaurante.menus;
    menus.forEach((element) => {
      if (element.nombre === menu.nombre)
        //veo si no hay un menu con ese nombre
        unico = false;
    });
    if (unico) {
      document.getElementById("cancelar").disabled = true;
      document.getElementById("agregar").disabled = true;
      document.getElementById("terminar").disabled = true;
      menu.categoria = document.getElementById("categoria").value;
      menu.descripcion = document.getElementById("descripcion").value;
      menu.price = document.getElementById("price").value;
      if (document.getElementById("img").files[0] !== undefined) {
        //si se selecciona una imagen
        var img = document.getElementById("img").files[0];
        const uploadTask = storage.ref(`/menus/${img.name}`).put(img);
        uploadTask.on(
          "state_changed",
          (snapshot) => {}, //el snapshot tiene que ir
          (error) => {
            setAlerta("Error al subir la imagen");
            setTipo("danger");
          },
          () => {
            storage
              .ref("menus")
              .child(img.name)
              .getDownloadURL()
              .then((url) => {
                menu.imagen = url;
                var json = JSON.parse(
                  sessionStorage.getItem("registroRestaurante")
                );
                json.restaurante.menus.push(menu);
                //aca hago el rest
                registrarRestaurante(json.restaurante)
                  .then(() => {
                    setAlerta("Registro exitoso");
                    setTipo("success");
                    setTimeout(() => {
                      window.location.replace("/");
                      sessionStorage.clear();
                    }, 3000);
                  })
                  .catch((error) => {
                    setAlerta(error.response.data);
                    setTipo("danger");
                    setTimeout(() => {
                      window.location.replace("/");
                      sessionStorage.clear();
                    }, 3000);
                  });
              });
          }
        );
      } else {
        menu.imagen = process.env.REACT_APP_GENERIC_MENU; //cargo la imagen generica
        json.restaurante.menus.push(menu);
        registrarRestaurante(json.restaurante)
          .then(() => {
            setAlerta("Registro exitoso");
            setTipo("success");
            setTimeout(() => {
              window.location.replace("/");
              sessionStorage.clear();
            }, 3000);
          })
          .catch((error) => {
            setAlerta(error.response.data);
            setTipo("danger");
            setTimeout(() => {
              window.location.replace("/");
              sessionStorage.clear();
            }, 3000);
          });
      }
    } else {
      setAlerta("El nombre del menu debe ser unico");
      setTipo("danger");
    }
  };

  var botonTerminar;
  if (
    sessionStorage.getItem("registroRestaurante") != null &&
    JSON.parse(sessionStorage.getItem("registroRestaurante")).nroMenu >= 3
  ) {
    botonTerminar = (
      <Button onClick={onEnd} id="terminar" type="submit">
        Terminar Altas
      </Button>
    );
  } else {
    botonTerminar = null;
  }

  let infoMsg;
  if (alerta !== null) {
    infoMsg = <Alerta className="mt-2" msg={alerta} tipo={tipoError} />;
  } else {
    infoMsg = null;
  }

  return (
    <Styles>
      <div id="page-container"></div>
      <section className="form-alta">
        <Form onSubmit={onSubmit}>
          <h4>Alta Menú</h4>
          {/*nombre del menu*/}
          <div className="form-floating">
            <input
              className="form-control"
              type="text"
              name="nombre"
              id="nombre"
              placeholder="Nombre del Menú"
              onChange={handleChange}
              required
            />
            <label htmlFor="floatingInput">Nombre del Menú</label>
          </div>
          {/*Precio*/}
          <div className="form-floating">
            <input
              className="form-control"
              type="number"
              id="price"
              placeholder="Precio"
              min="1"
              onChange={handleChange}
              required
            />
            <label htmlFor="floatingInput">Precio</label>
          </div>
          {/*descripcion*/}
          <div className="form-floating">
            <input
              className="form-control"
              type="text"
              id="descripcion"
              placeholder="Descripcion"
              onChange={handleChange}
              required
            />
            <label htmlFor="floatingInput">Descripción</label>
          </div>
          <FloatingLabel controlId="floatingSelect" label="Categoría">
            <Form.Select
              aria-label="Floating label select example"
              id="categoria"
              onChange={handleChange}
              required
            >
              <option>Seleccione una categoría</option>
              {categorias.map((categoria) => (
                <option key={categoria.nombre} value={categoria.nombre}>
                  {categoria.nombre}
                </option>
              ))}
            </Form.Select>
          </FloatingLabel>
          <label className="mb-2">Imágen del menú</label>
          {/* image uploader */}
          <Form.Control className="archivo mb-3" id="img" type="file" />
          {infoMsg}
          {uploadBar}
          <ButtonGroup className="bGroup">
            <Button id="cancelar" onClick={onCancel}>
              Cancelar
            </Button>
            <Button id="agregar" type="submit">
              Agregar otro
            </Button>
            {botonTerminar}
          </ButtonGroup>
        </Form>
      </section>
    </Styles>
  );
}

export default RegistroAltaMenu;

import React, { useState } from "react";
import styled from "styled-components";
import {
  Button,
  FloatingLabel,
  Form,
  Alert,
  ButtonGroup,
} from "react-bootstrap";
import { storage } from "../../Firebase";
import { altaMenu } from "../../services/Requests";
import { Error } from "../../components/Error";

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
  console.log(JSON.parse(sessionStorage.getItem("registroRestaurante")).nroMenu);
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

  const [success, setSuccess] = useState(null);
  const [componente, setComponente] = useState(null);

  const handleChange = (e) => {
    e.persist();
    state[e.target.name] = e.target.value;
  };

  const onCancel = () => {
    sessionStorage.clear();
    window.location.replace("/");
  };

  const onSubmit = () => {
    menu.nombre = document.getElementById("nombre").value;
    menu.categoria = document.getElementById("categoria").value;
    menu.descripcion = document.getElementById("descripcion").value;
    menu.price = document.getElementById("price").value;
    console.log(menu);
    if (document.getElementById("img").files[0] !== undefined) {
      //si se selecciona una imagen
      var img = document.getElementById("img").files[0];
      const uploadTask = storage.ref(`/menus/${img.name}`).put(img);
      uploadTask.on(
        "state_changed",
        (snapshot) => {}, //el snapshot tiene que ir
        (error) => {
          console.log(error.message);
          setComponente(<Error error="Error al subir la imagen" />);
        },
        () => {
          setComponente(null);
          storage
            .ref("menus")
            .child(img.name)
            .getDownloadURL()
            .then((url) => {
              menu.imagen = url;
              var json = JSON.parse(
                sessionStorage.getItem("registroRestaurante")
              );
              json[`menu${json.nroMenu}`] = menu;
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
      var json = JSON.parse(sessionStorage.getItem("registroRestaurante"));
      json[`menu${json.nroMenu}`] = menu;
      json.nroMenu++;
      sessionStorage.setItem("registroRestaurante", JSON.stringify(json));
      window.location.reload();
    }
  };

  const onEnd = () => {
    menu.nombre = document.getElementById("nombre").value;
    menu.categoria = document.getElementById("categoria").value;
    menu.descripcion = document.getElementById("descripcion").value;
    menu.price = document.getElementById("price").value;
    console.log(menu);
    if (document.getElementById("img").files[0] !== undefined) {
      //si se selecciona una imagen
      var img = document.getElementById("img").files[0];
      const uploadTask = storage.ref(`/menus/${img.name}`).put(img);
      uploadTask.on(
        "state_changed",
        (snapshot) => {}, //el snapshot tiene que ir
        (error) => {
          console.log(error.message);
          setComponente(<Error error="Error al subir la imagen" />);
        },
        () => {
          setComponente(null);
          storage
            .ref("menus")
            .child(img.name)
            .getDownloadURL()
            .then((url) => {
              menu.imagen = url;
              var json = JSON.parse(
                sessionStorage.getItem("registroRestaurante")
              );
              json[`menu${json.nroMenu}`] = menu;
              json.nroMenu++;
              sessionStorage.clear();
              //aca hago el rest
              window.location.replace("/");
            });
        }
      );
    } else {
      menu.imagen = process.env.REACT_APP_GENERIC_MENU; //cargo la imagen generica
      var json = JSON.parse(sessionStorage.getItem("registroRestaurante"));
      json[`menu${json.nroMenu}`] = menu;
      json.nroMenu++;
      sessionStorage.clear();
      console.log("termino");
      console.log((json));
      //aca hago el rest
      window.location.replace("/");
    }
  }

  let botonTerminar;
  if (JSON.parse(sessionStorage.getItem("registroRestaurante")).nroMenu >= 3) {
    botonTerminar = (
      <Button onClick={onEnd} type="submit">
        Terminar Altas
      </Button>
    );
  } else {
    botonTerminar = null;
  }

  return (
    <Styles>
      <div id="page-container"></div>
      <section className="form-alta">
        <Form>
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
            />
            <label htmlFor="floatingInput">Descripción</label>
          </div>
          <FloatingLabel controlId="floatingSelect" label="Categoría">
            <Form.Select
              aria-label="Floating label select example"
              id="categoria"
              onChange={handleChange}
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
          <Form.Control className="archivo" id="img" type="file" required />
          {success}
          {componente}
          <ButtonGroup className="bGroup">
            <Button onClick={onCancel}>Cancelar</Button>
            <Button onClick={onSubmit}>Agregar otro</Button>
            {botonTerminar}
          </ButtonGroup>
        </Form>
      </section>
    </Styles>
  );
}

export default RegistroAltaMenu;

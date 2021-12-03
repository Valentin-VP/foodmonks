import React, { useState } from "react";
import styled from "styled-components";
import {
  Button,
  FloatingLabel,
  Form,
  Alert,
  ProgressBar,
} from "react-bootstrap";
import { storage } from "../../Firebase";
import { altaMenu } from "../../services/Requests";
import { Error } from "../../components/Error";

const Styles = styled.div`
  * {
    margin: 0;
    box-sizing: border-box;
  }

  #page-container {
    padding-top: 35rem;
    background-image: url("https://images.pexels.com/photos/6419720/pexels-photo-6419720.jpeg?auto=compress&cs=tinysrgb&dpr=3&h=750&w=1260");
    filter: blur(6px);
    background-position: center;
    background-repeat: no-repeat;
    background-size: cover;
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

    Button {
      width: 100%;
      color: white;
      background-color: #e87121;
      border: none;
      padding: 15px;
      margin-top: 15px;
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

    }
  }

  .form-floating {
    margin-bottom: 13px;
  }

`;

function AltaMenu() {
  const state = {
    img: "",
    imgUrl: "",
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
    { value: "PIZZAS", nombre: "Pizzas" },
    { value: "HAMBURGUESAS", nombre: "Hamburguesas" },
    { value: "BEBIDAS", nombre: "Bebidas" },
    { value: "COMBOS", nombre: "Combos" },
    { value: "MINUTAS", nombre: "Minutas" },
    { value: "POSTRES", nombre: "Postres" },
    { value: "PASTAS", nombre: "Pastas" },
    { value: "COMIDAARABE", nombre: "Comida arabe" },
    { value: "SUSHI", nombre: "Sushi" },
    { value: "OTROS", nombre: "Otros" },
  ];

  const [success, setSuccess] = useState(null);
  const [componente, setComponente] = useState(null);
  const [uploadBar, setUploadBar] = useState(null);

  const handleUpload = (data) => {
    state.img = data.target.files[0];
  };

  const onSubmit = (e) => {
    e.preventDefault();
    menu.nombre = document.getElementById("nombre").value;
    menu.categoria = document.getElementById("categoria").value;
    menu.descripcion = document.getElementById("descripcion").value;
    menu.price = document.getElementById("price").value;
    if (state.img !== "") {
      //si se selecciona una imagen
      const uploadTask = storage.ref(`/menus/${state.img.name}`).put(state.img);
      uploadTask.on(
        "state_changed",
        (snapshot) => {
          let percentage =
            (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
          setUploadBar(<ProgressBar now={percentage} />);
        },
        (error) => {
          setComponente(<Error error="Error al subir la imagen" />);
        },
        () => {
          setUploadBar(null);
          setComponente(null);
          storage
            .ref("menus")
            .child(state.img.name)
            .getDownloadURL()
            .then((url) => {
              state.imgUrl = url;
              //ahora cargo el json y hago el alta
              menu.imagen = state.imgUrl;
              altaMenu(menu).then((response) => {
                if (response.status === 201) setUploadBar(null);
                setSuccess(
                  <Alert variant="success">Menú creado con exito!</Alert>
                );
                setTimeout(() => {
                  window.location.replace("/menu");
                }, 3000);
              }).catch((error) => {
                setComponente(<Error error={error.response.data.detailMessage} />);
              });
            })
            .catch((error) => {
              setComponente(
                <Error error={error.response.data.detailMessage} />
              );
            });
        }
      );
    } else {
      menu.imagen = process.env.REACT_APP_GENERIC_MENU; //cargo la imagen generica
      altaMenu(menu)
        .then((response) => {
          //llamo al back
          if (response.status === 201)
            setSuccess(<Alert variant="success">Menú creado con éxito!</Alert>);
          setTimeout(() => {
            window.location.replace("/menu");
          }, 3000);
        })
        .catch((error) => {
          setComponente(<Error error={error.response} />);
        });
    }
  };

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
              required
            />
            <label htmlFor="floatingInput">Nombre del Menú</label>
          </div>
          {/*Precio*/}
          <div className="form-floating">
            <input
              className="form-control"
              type="number"
              name="price"
              id="price"
              placeholder="Precio"
              min="1"
              required
            />
            <label htmlFor="floatingInput">Precio</label>
          </div>
          {/*descripcion*/}
          <div className="form-floating">
            <input
              className="form-control"
              type="text"
              name="descripcion"
              id="descripcion"
              placeholder="Descripción"
              required
            />
            <label htmlFor="floatingInput">Descripción</label>
          </div>
          <FloatingLabel controlId="floatingSelect" label="Categoría">
            <Form.Select
              aria-label="Floating label select example"
              name="categoria"
              id="categoria"
              required
            >
              <option value="">Seleccione una categoría</option>
              {categorias.map((categoria) => (
                <option key={categoria.nombre} value={categoria.value}>
                  {categoria.nombre}
                </option>
              ))}
            </Form.Select>
          </FloatingLabel>
          <label className="mb-2">Imágen del menú</label>
          {/* image uploader */}
          <Form.Control
            className="archivo mb-3"
            type="file"
            size="lg"
            onChange={handleUpload}
          />
          {success}
          {componente}
          {uploadBar}
          <Button id="submit" type="submit">
            Alta
          </Button>
        </Form>
      </section>
    </Styles>
  );
}

export default AltaMenu;

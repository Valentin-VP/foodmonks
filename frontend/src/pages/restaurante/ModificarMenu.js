import { React, useState, useEffect } from "react";
import styled from "styled-components";
import { Button, FloatingLabel, Form, Alert } from "react-bootstrap";
import { storage } from "../../Firebase";
import { getMenuInfo, modMenu } from "../../services/Requests";
import { Error } from "../../components/Error";

const Styles = styled.div`
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  #page-container {
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

`;

function ModificarMenu() {
  const [menu, setMenu] = useState();
  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    getMenuInfo().then((response) => {
      console.log("paso por fetch de modificar");
      console.log(response.data);
      setMenu(response.data);
      setLoading(false);
    });
  }, []);

  if (isLoading) {
    return <div className="App">Cargando...</div>;
  }

  const state = {//valores cargados del menu a modificar
    id: menu.id,
    nombre: menu.nombre,
    price: menu.price,
    descripcion: menu.descripcion,
    descuento: menu.multiplicadorPromocion,
    categoria: menu.categoria,
    img: "",
    imgUrl: menu.imagen,
  };

  const menuRetorno = {
    nombre: "",
    price: "",
    descripcion: "",
    visibilidad: true,
    multiplicador: "",
    categoria: "",
    imagen: "",
  }


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

  let success;
  let componente;

  const handleUpload = (data) => {
    state.img = data.target.files[0];
  };

  const handleChange = (e) => {
    e.persist();
    state[e.target.name] = e.target.value;
  };

  const onSubmit = () => {
    //cargo todo menos la imagen
    menuRetorno.nombre = state.nombre;
    menuRetorno.categoria = state.categoria;
    menuRetorno.descripcion = state.descripcion;
    menuRetorno.multiplicador = state.descuento;
    menuRetorno.price = state.price;
    if(state.img !== "") {//si se selecciona una imagen
      const uploadTask = storage.ref(`/menus/${state.img.name}`).put(state.img);
      uploadTask.on(
        "state_changed",
        (snapshot) => {}, //el snapshot tiene que ir
        (error) => {
          console.log(error.message);
          componente = <Error error="Error al subir la imagen" />;
        },
        () => {
          componente = null;
          console.log("entro al storage");
          storage
            .ref("menus")
            .child(state.img.name)
            .getDownloadURL()
            .then((url) => {
              //cambio la imagen
              state.imgUrl = url;
              menuRetorno.imagen = state.imgUrl;
              console.log(menuRetorno);
              console.log(state.id);
              modMenu(menuRetorno, state.id).then((response) => {//request al backend
                if (response.status === 200)
                  success = <Alert variant="success">Menú modificado con exito!</Alert>;
                console.log(response);
                sessionStorage.removeItem("menuId");
              });
            });
        }
      );
    } else {//sino
      menuRetorno.imagen = state.imgUrl;//cargo la imagen que ya estaba
      modMenu(menuRetorno, state.id).then((response) => {//request al backend
        if (response.status === 200) success = <Alert variant="success">Menú modificado con exito!</Alert>;
        console.log(response);
        sessionStorage.removeItem("menuId");
      });
    }
  };

  return (
    <Styles>
      <div id="page-container"></div>
      <section className="form-alta">
        <Form>
          <h4>Modificar Menú</h4>
          {/*nombre del menu*/}
          <div className="form-floating">
            <input
              className="form-control"
              type="text"
              name="nombre"
              id="nombre"
              placeholder="Nombre del Menú"
              onChange={handleChange}
              disabled
            />
            <label for="floatingInput">{state.nombre}</label>
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
              onChange={handleChange}
            />
            <label for="floatingInput">{state.price}</label>
          </div>
          {/*descripcion*/}
          <div className="form-floating">
            <input
              className="form-control"
              type="text"
              name="descripcion"
              id="descripcion"
              placeholder="Descripcion"
              onChange={handleChange}
            />
            <label for="floatingInput">{state.descripcion}</label>
          </div>
          {/*descuento*/}
          <div className="form-floating">
            <input
              className="form-control"
              type="number"
              name="descuento"
              id="descuento"
              placeholder="Descuento"
              max="100"
              min="0"
              defaultValue="0"
              onChange={handleChange}
            />
            <label for="floatingInput">Descuento</label>
          </div>
          <FloatingLabel controlId="floatingSelect" label="Categoría">
            <Form.Select
              aria-label="Floating label select example"
              name="categoria"
              onChange={handleChange}
            >
              <option>{state.categoria}</option>
              {categorias.map((categoria) => (
                <option key={categoria.nombre} value={categoria.nombre}>
                  {categoria.nombre}
                </option>
              ))}
            </Form.Select>
          </FloatingLabel>
          <label className="mb-2">Imágen del menú</label>
          {/* image uploader */}
          <Form.Control type="file" onChange={handleUpload} />
          {success}
          {componente}
          <Button onClick={onSubmit}>Modificar</Button>
        </Form>
      </section>
    </Styles>
  );
}

export default ModificarMenu;

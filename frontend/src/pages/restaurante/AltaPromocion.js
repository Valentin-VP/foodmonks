import React, { useState, useEffect } from "react";
import styled from "styled-components";
import { Button, FloatingLabel, Form, Alert } from "react-bootstrap";
import { getMenuInfo, altaMenu } from "../../services/Requests";

const Styles = styled.div`
  * {
    margin: 0;
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

  .form-control {
      margin-top: 13px;
  }

  img {
      border-radius: 5px;
  }

`;

function AltaPromocion() {
    const [menu, setMenu] = useState();
    const [isLoading, setLoading] = useState(true);
    const [success, setSuccess] = useState(null);

    useEffect(() => {
        getMenuInfo().then((response) => {
            console.log("paso por fetch de promocionar");
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

    const handleChange = (e) => {
        e.persist();
        state[e.target.name] = e.target.value;
    };

    const onSubmit = (e) => {
        e.preventDefault();
        menuRetorno.nombre = state.nombre;
        menuRetorno.categoria = state.categoria;
        menuRetorno.descripcion = state.descripcion;
        menuRetorno.multiplicador = state.descuento;
        menuRetorno.price = state.price;
        menuRetorno.imagen = state.imgUrl;

        altaMenu(menuRetorno).then((response) => {//request al backend
            document.getElementById("submit").disabled = true;
            console.log("entro al then");
            setSuccess(<Alert variant="success">Promocion creada con exito!</Alert>);
            console.log(response);
            sessionStorage.removeItem("menuId");
            setTimeout(() => {
                window.location.replace("/promocion");
            }, 3000);
        }).catch((error) => {
            console.log(error.response.data);
            setSuccess(<Alert variant="danger">{error.response.data.detailMessage}</Alert>);
        });
    };

    return (
        <Styles>
            <div id="page-container"></div>
            <section className="form-alta">
                <Form onSubmit={onSubmit}>
                {/*nombre del menu a promocionar*/}
                <div className="text-center">
                    <h4>Promocionar Menú</h4>
                    <img src={state.imgUrl} alt="productimg" height="150"/>
                </div>
                {/*imagen del menu a promocionar*/}
                <div className="form-floating">
                    <input
                    className="form-control"
                    type="text"
                    name="nombre"
                    id="nombre"
                    placeholder="Nombre de la Promocion"
                    onChange={handleChange}
                    />
                    <label htmlFor="floatingInput">{state.nombre}</label>
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
                    disabled
                    />
                    <label htmlFor="floatingInput">{state.price}</label>
                </div>
                {/*descripcion*/}
                <div className="form-floating">
                    <input
                    className="form-control"
                    type="text"
                    name="descripcion"
                    id="descripcion"
                    placeholder="Descripcion"
                    disabled
                    />
                    <label htmlFor="floatingInput">{state.descripcion}</label>
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
                    min="1"
                    required
                    defaultValue="1"
                    onChange={handleChange}
                    />
                    <label htmlFor="floatingInput">Descuento</label>
                </div>
                <FloatingLabel controlId="floatingSelect" label="Categoría">
                    <Form.Select
                    aria-label="Floating label select example"
                    name="categoria"
                    disabled
                    >
                    <option>{state.categoria}</option>
                    {categorias.map((categoria) => (
                        <option key={categoria.nombre} value={categoria.nombre}>
                        {categoria.nombre}
                        </option>
                    ))}
                    </Form.Select>
                </FloatingLabel>
                {success}
                <Button type="submit" id="submit">Promocionar</Button>
            </Form>
        </section>
        </Styles>
    );
}

export default AltaPromocion;
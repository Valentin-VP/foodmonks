import React, { Component } from "react";
import styled from "styled-components";
import { Button, FloatingLabel, Form } from "react-bootstrap";

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
    margin-bottom: 15px;
  }
`;

function AltaMenu() {
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

  return (
    <Styles>
      <div id="page-container"></div>
      <section className="form-alta">
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
          <label for="floatingInput">Nombre del Menú</label>
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
          />
          <label for="floatingInput">Precio</label>
        </div>
        {/*descripcion*/}
        <div className="form-floating">
          <input
            className="form-control"
            type="text"
            name="descripcion"
            id="descripcion"
            placeholder="Descripcion"
          />
          <label for="floatingInput">Descripción</label>
        </div>
        {/*multiplicadorPromocion*/}
        <div className="form-floating">
          <input
            className="form-control"
            type="number"
            name="multiplicadorPromocion"
            id="multiplicadorPromocion"
            placeholder="Descuento"
            step="0.01"
            max="1"
            min="0"
          />
          <label for="floatingInput">Descuento</label>
        </div>
        <FloatingLabel controlId="floatingSelect" label="Categoría">
          <Form.Select aria-label="Floating label select example">
            <option>Seleccione una categoría</option>
            {categorias.map((categoria) => 
              <option key={categoria.nombre} value={categoria.nombre}>{categoria.nombre}</option>
              // console.log(categoria.nombre)
            )}
          </Form.Select>
        </FloatingLabel>
        <Button className="" type="submit">
          Alta
        </Button>
      </section>
    </Styles>
  );
}

export default AltaMenu;

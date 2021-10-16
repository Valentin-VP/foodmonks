import React from "react";
import { Layout } from "../../components/Layout";
import MenuCard from "./MenuCard";
import styled from "styled-components";
import imgPrueba from "../../assets/productos/hamburguesa.jpg";
// import ItemCard from "../../components/itemCard";

const Styles = styled.div`
  #titulo {
    text-decoration: none;
    font-size: 30px;
    font-family: "Poppins", sans-serif;
    color: #e87121;
    padding-top: 20px;
    padding-bottom: 20px;
  }
`;

export const Menu = () => (
  <Styles>
    <React.Fragment>
      <Layout>
        <h2 id="titulo">Mis Menús</h2>
        <div className="row justify-content-center">
          {/*en esta linea va el iterador  */}
          <div className="column">
            <MenuCard
              imagen={imgPrueba}
              nombre="prueba"
              descripcion="descripcion de prueba"
              price="50"
              multiplicador="5"
              categoria="comida"
            />
          </div>
        </div>
      </Layout>
    </React.Fragment>
  </Styles>
);

import React from "react";
import { Layout } from "../../components/Layout";
import styled from "styled-components";
// import ItemCard from "../../components/itemCard";
// import prods from "../../productos";

const Styles = styled.div`
  #titulo {
    text-decoration: none;
    font-size: 30px;
    font-family: "Poppins", sans-serif;
    color: #e87121;
    font-weight: bold;
    padding-top: 30px;
  }
`;

export const Home = () => (
  <Styles>
    <React.Fragment>
      <Layout>
        <h2 id="titulo">Esto es de un restaurante</h2>
      </Layout>
    </React.Fragment>
  </Styles>
);

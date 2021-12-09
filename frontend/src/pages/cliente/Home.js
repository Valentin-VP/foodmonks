import React, { useEffect } from "react";
import styled from "styled-components";

import BuscarRestaurantesAbiertos from "./BuscarRestaurantesAbiertos";

const Styles = styled.div`
  .column {
    float: left;
    width: 300px;
    padding: 0 10px;
    margin-bottom: 5%;
  }

  .prods {
    text-align: center;
  }

  @media screen and (max-width: 700px) {
    .column {
      width: 100%;
      display: block;
      margin-bottom: 20px;
    }
  }
`;

export default function Home() {
  useEffect(() => {
    if (sessionStorage.getItem("restauranteId") !== null) {
      sessionStorage.removeItem("restauranteId");
      sessionStorage.removeItem("restauranteImagen");
      sessionStorage.removeItem("restauranteCalif");
      sessionStorage.removeItem("restauranteNombre");
      sessionStorage.setItem("values-categoria", "");
      sessionStorage.setItem("values-precioInicial", "");
      sessionStorage.setItem("values-precioFinal", "");
    }
  }, []);

  return (
    <Styles>
      <BuscarRestaurantesAbiertos />
    </Styles>
  );
}

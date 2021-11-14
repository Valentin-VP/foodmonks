import React, { useState, useEffect } from "react";
import { Layout } from "../../components/Layout";
import styled from "styled-components";
import { Loading } from "../../components/Loading";
import { listarRestaurantesPorEstado } from "../../services/Requests";

const Styles = styled.div`
  h2 {
    margin-top: 5rem;
    text-align: center;
    color: #e88121;
    font-weight: bold;
    text-align: center;
    font-size: 30px;
    font-family: "Poppins", sans-serif;
  }
`;

export default function Home() {
  const [restaurantes, setRestaurantes] = useState();
  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    listarRestaurantesPorEstado("PENDIENTE").then((response) => {
      setRestaurantes(response.data);
      console.log(response.data);
      setLoading(false);
    });
  }, []);

  if (isLoading) {
    return <Loading />;
  }

  return (
    <Styles>
      <Layout>
        <h2>Restaurantes con registros pendientes</h2>
      </Layout>
    </Styles>
  );
}

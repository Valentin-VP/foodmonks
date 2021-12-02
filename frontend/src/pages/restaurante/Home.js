import React, { useState, useEffect } from "react";
import { Layout } from "../../components/Layout";
import styled from "styled-components";
import { Noti } from "../../components/Notification";
import { cambiarEstado, fetchRestauranteInfo } from "../../services/Requests";
import ListadoPedidosPendientes from "./ListadoPedidosPendientes";
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

  #aperturaCierreSwitch {
    font-size: 25px;
  }

  #aperturaCierreLabel {
    text-decoration: none;
    font-size: 25px;
    font-family: "Poppins", sans-serif;
    color: #e87121;
    font-weight: bold;
  }

  img {
    object-fit: cover;
  }
`;

function Home() {
  sessionStorage.setItem("reclamos-razon", "");
  sessionStorage.setItem("reclamos-cliente", "");
  sessionStorage.setItem("reclamos-ordenar", false);

  const [abierto, setAbierto] = useState(false); //true: ABIERTO, false: CERRADO

  useEffect(() => {
    fetchRestauranteInfo().then((response) => {
      if (response.data.estado === "ABIERTO") {
        setAbierto(true);
        document.getElementById("aperturaCierreSwitch").checked = true;
      } else {
        setAbierto(false);
        document.getElementById("aperturaCierreSwitch").checked = false;
      }
    });
  }, []);

  const onChangeEstado = () => {
    if (!abierto) {
      //llamo al backend con estado ABIERTO
      cambiarEstado("ABIERTO").then((response) => {
        setAbierto(true);
        Noti("ABIERTO!!");
      });
    } else {
      //llamo al backend con estado CERRADO
      cambiarEstado("CERRADO").then((response) => {
        setAbierto(false);
        Noti("CERRADO!!");
      });
    }
  };
  return (
    <Styles>
      <React.Fragment>
        <Layout>
          <div className="form-check form-switch">
            <input
              className="form-check-input"
              type="checkbox"
              id="aperturaCierreSwitch"
              onChange={onChangeEstado}
              checked={abierto}
            ></input>
            <label
              className="form-check-label"
              htmlFor="aperturaCierreSwitch"
              id="aperturaCierreLabel"
            >
              El restaurante esta {abierto ? "Abierto" : "Cerrado"}
            </label>
          </div>
          <ListadoPedidosPendientes />
        </Layout>
      </React.Fragment>
    </Styles>
  );
}

export default Home;

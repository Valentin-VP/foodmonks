import React, {useState} from "react";
import { Layout } from "../../components/Layout";
import styled from "styled-components";
import { Noti } from "../../components/Notification";
import { cambiarEstado } from "../../services/Requests";
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
`;

function Home() {
  const [estado, setEstado] = useState(true);

  const onClick = () => {
    setEstado(!estado);
    if(estado) {
      //llamo al backend con estado ABIERTO
      cambiarEstado("ABIERTO").then((response) => {
        console.log(response);
        Noti("ABIERTO!!");
      });
    } else {
      //llamo al backend con estado CERRADO
      cambiarEstado("CERRADO").then((response) => {
        console.log(response);
        Noti("CERRADO!!");
      });
    }
  }

  return (
  <Styles>
    <React.Fragment>
      <Layout>
        <div class="form-check form-switch">
          <input class="form-check-input" type="checkbox" id="aperturaCierreSwitch" onClick={onClick}></input>
          <label class="form-check-label" for="aperturaCierreSwitch" id="aperturaCierreLabel">Indicar apertura/cierre</label>
        </div>
        <h2 id="titulo">Esto es de un restaurante</h2>
      </Layout>
    </React.Fragment>
  </Styles>
  );
}

export default Home;

import React, {useEffect} from "react";
import { Layout } from "../../components/Layout";
import { Portada } from "../../components/Portada";
import styled from "styled-components";
import ItemCard from "../../components/itemCard";
import prods from "../../productos";
import BuscarRestaurantesAbiertos from "./BuscarRestaurantesAbiertos";

const Styles = styled.div`
  .top {
    text-align: center;
    margin-top: 20px;
    margin-bottom: 100px;
    a {
      text-decoration: none;
      font-size: 30px;
      font-family: "Poppins", sans-serif;
      color: #0074d9;
    }
  }
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
    if(sessionStorage.getItem("restauranteId") !== null) {
      sessionStorage.removeItem("restauranteId");
      sessionStorage.removeItem("restauranteImagen");
      sessionStorage.removeItem("restauranteCalif");
      sessionStorage.removeItem("restauranteNombre");
    }
  }, []);

  return (
  <Styles>
    <React.Fragment>
      <Portada />
      <Layout>
        <h2>Restaurantes</h2>
        <BuscarRestaurantesAbiertos />
        
        <div className="top">
          <a href="/grafica">Top Restaurantes</a>
        </div>

        <h2 className="prods">Productos</h2>
        <div className="row justify-content-center">
          {prods.productData.map((item, index) => {
            return (
              <div className="column">
                <ItemCard
                  key={index}
                  img={item.img}
                  title={item.title}
                  desc={item.desc}
                  price={item.price}
                  item={item}
                />
              </div>
            );
          })}
        </div>
      </Layout>
    </React.Fragment>
  </Styles>
  );
}

import React from "react";
import { Layout } from "../../components/Layout";
import { Portada } from "../../components/Portada";
import styled from "styled-components";
import ItemCard from "../../components/itemCard";
import prods from "../../productos";

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

  .prods {
    text-align: center;
  }

  .column {
    float: left;
    width: 25%;
  }
`;

export const Home = () => (
  <Styles>
    <React.Fragment>
      <Portada />
      <Layout>
        <h2>Restaurantes</h2>
        <ul className="list-group">
          <li className="list-group-item">1. El Tío Bistró</li>
          <li className="list-group-item">2. Primuseum</li>
          <li className="list-group-item">3. Cafe Gourmand</li>
          <li className="list-group-item">4. Ashot Shawarma</li>
          <li className="list-group-item">5. Alquimista Montevideo</li>
          <li className="list-group-item">6. Es Mercat</li>
          <li className="list-group-item">7. estrecho</li>
          <li className="list-group-item">8. Restaurante Tandory</li>
          <li className="list-group-item">9. RUDY Burgers</li>
          <li className="list-group-item">10. Candy Bar</li>
          <li className="list-group-item">11. Baco Vino y Bistró</li>
        </ul>
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

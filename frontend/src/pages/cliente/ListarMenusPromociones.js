import React from "react";
import styled from "styled-components";
import { Layout } from "../../components/Layout";
import ItemCard from "../../components/itemCard";

const Styles = styled.div`
  .column {
    float: left;
    width: 300px;
    padding: 0 10px;
    margin-bottom: 5%;
  }

  @media screen and (max-width: 700px) {
    .column {
      width: 100%;
      display: block;
      margin-bottom: 20px;
    }
  }
`;

export default function ListadoMenusPromociones({ data }) {
  return (
    <Styles>
      <Layout>
        <h2> Promociones </h2>
        <div className="row justify-content-left">
          {data.map((item, index) => {
            return item.multiplicadorPromocion !== 0 ? (
              <div className="column" key={index}>
                <ItemCard
                  img={item.imagen}
                  title={item.nombre}
                  desc={item.multiplicadorPromocion}
                  price={item.price}
                  item={item}
                />
              </div>
            ) : null;
          })}
          <hr />
          <h2> Menus </h2>
          {data.map((item, index) => {
            return item.multiplicadorPromocion === 0 ? (
              <div className="column" key={index}>
                <ItemCard
                  img={item.imagen}
                  title={item.nombre}
                  price={item.price}
                  item={item}
                />
              </div>
            ) : null;
          })}
        </div>
      </Layout>
    </Styles>
  );
}

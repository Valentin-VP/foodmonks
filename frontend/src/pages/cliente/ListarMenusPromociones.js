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
      <React.Fragment>
        <Layout>
          <h2>Menus y Promociones</h2>
          <h3> Promociones </h3>
          {data.map((item, index) => {
            return (
              <div className="row justify-content-left">
                {item.multiplicadorPromocion !== 0 ? (
                  <div className="column">
                    <ItemCard
                      key={index}
                      img={item.imagen}
                      title={item.nombre}
                      desc={item.multiplicador}
                      price={item.price}
                      item={item}
                    />
                  </div>
                ) : null}
              </div>
            );
          })}
          <hr />
          <h3> Menus </h3>
          {data.map((item, index) => {
            return (
              <div className="row justify-content-left">
                {item.multiplicadorPromocion === 0 ? (
                  <div className="column">
                    <ItemCard
                      key={index}
                      img={item.imagen}
                      title={item.nombre}
                      price={item.price}
                      item={item}
                    />
                  </div>
                ) : null}
              </div>
            );
          })}
        </Layout>
      </React.Fragment>
    </Styles>
  );
}

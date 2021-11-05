import React from "react";
import styled from "styled-components";

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

export default function ListadoMenusPromociones({data}) {

    return (
        <Styles>
        <React.Fragment>
          <Layout>
            <h2>Menus y Promociones</h2>
            <div className="row justify-content-center">
            <h3> Promociones </h3>
              {data.map((item, index) => {
                return (
                  <div className="column">
                    {item.multiplicador !== 0 ?  
                    <ItemCard
                      key={index}
                      img={item.imagen}
                      title={item.nombre}
                      desc={item.multiplicador}
                      price={item.price}
                      item={item}
                    />
                    : null}
                  </div>                
                );
              })}
            <hr />
            <h3> Menus </h3>
              {data.map((item, index) => {
                return (
                  <div className="column">
                    {item.multiplicador === 0 ?  
                    <ItemCard
                      key={index}
                      img={item.imagen}
                      title={item.nombre}
                      price={item.price}
                      item={item}
                    />
                    : null}
                  </div>                
                );
              })}              
            </div>
          </Layout>
        </React.Fragment>
      </Styles>
    )
}
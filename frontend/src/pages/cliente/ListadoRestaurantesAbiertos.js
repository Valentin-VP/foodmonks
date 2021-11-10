import React from "react";
import styled from "styled-components";
import { Layout } from "../../components/Layout";
import RestauranteCard from "../../components/RestauranteCard";

const Styles = styled.div`
  h1 {
    text-align: center;
  }
  table {
    background-color: white;
  }
  img {
    height: 9rem;
    border-radius: 8px;
  }
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

export default function ListadoRestaurantesAbiertos({ data }) {
  return (
    <>
      <Styles>
        <Layout>
          <div className="table-responsive justify-content-center" id="list">
            <table className="table table-hover m-0">
              <tbody>
                {/* <tr>
                      <td>
                        <img
                          src="https://d1csarkz8obe9u.cloudfront.net/posterpreviews/restaurant-logo-design-template-b281aeadaa832c28badd72c1f6c5caad_screen.jpg?ts=1595421543"
                          alt="restimg"
                          width="150"
                          hight="150"
                        />
                      </td>
                      <td>Restaurante</td>
                      <td>Teléfono: 1234785967</td>
                      <td>Calificación: 5.0</td>
                    </tr> */}
                {data.map((item, index) => {
                  return (
                    <div className="column" key={index}>
                      <RestauranteCard
                        correo={item.correo}
                        imagen={item.imagen}
                        nombre={item.nombreRestaurante}
                        telefono={item.telefono}
                        calificacion={item.calificacion}
                        item={item}
                      />
                    </div>
                  );
                })}
              </tbody>
            </table>
          </div>
        </Layout>
      </Styles>
    </>
  );
}

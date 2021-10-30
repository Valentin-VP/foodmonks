import React, { useEffect, useRef, useState } from "react";
import { Button, Table } from "react-bootstrap";
import styled from "styled-components";
import { actualizarEstadoUsuario } from "../../services/Requests";
import { Noti } from "../../components/Notification"

const Styles = styled.div`
  h1 {
    text-align: center;
  }
  table{
    background-color: #FFFFFF;
  }
  img {
    height: 9rem;
    border-radius: 8px;
  }
`;

export default function ListadoRestaurantesAbiertos({data}) {

    return (
    <>
        <Styles>
            <div className="table-responsive justify-content-center" id="list">
            <table className="table table-light table-hover m-0">
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
                    <tr key={index}>
                      <td>
                        <img
                          src={item.imagen}
                          alt="restimg"
                          width="150"
                          hight="150"
                        />
                      </td>
                      <td>{item.nombre}</td>
                      <td>Teléfono: {item.telefono}</td>
                      <td>Calificación: {item.calificacion}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
            </div>
        </Styles>
    </>
    )
}
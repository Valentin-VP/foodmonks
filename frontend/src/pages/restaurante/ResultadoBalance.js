import React from "react";
import styled from "styled-components";
import { Col } from "react-bootstrap";

const Styles = styled.div`
  .lista {
    padding-top: 35px;
  }
  .form-floating {
    margin-bottom: 15px;
  }
  h1 {
    color: #e88121;
    font-weight: bold;
    text-align: center;
    font-size: 30px;
    font-family: "Poppins", sans-serif;
  }
  .tabla {
    background-color: #ffffff;
    text-align: center;
    font-family: "Poppins", sans-serif;
    border-collapse: collapse;
    border: 3px solid #fefefe;
    width: 100%;
  }
  td,
  tr {
    border: 1px solid #eee;
    padding: 6px;
    width: 8%;
    &:hover {
      background-color: #fffff5;
    }
  }
  #itemId {
    font-weight: lighter;
    font-size: 18px;
    &:hover {
      background-color: #fffff1;
    }
  }

  .row,
  .col {
    padding: 1px;
  }
  img {
    height: 6rem;
    border-radius: 5px;
  }
  .text-center {
    position: relative;
  }
  button {
    color: white;
    background-color: #e87121;
    border: none;
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
      background-color: #e87121;
    }
    &:hover {
      background-color: #da6416;
    }
    &:active {
      background-color: #e87121;
    }
  }
`;

export default function ResultadoBalance({ datos }) {
  console.log(datos);
  return (
    <>
      <Styles>
        <div className="container-lg">
          <main className="lista">
            <h1 className="text-center h5 mb-3 fw-normal">Balance</h1>
            <div className="form-floating">
              <div className="table-responsive justify-content-center tabla">

                    {datos.meses
                      ? datos.meses.map((item, index) => {
                          return (
                            <>
                              <Col>
                                <tr key={index}>
                                  <th>{item.mes} {item.año}</th>
                                  <th>Egresos</th>
                                  <th>Ingresos</th>
                                  <th>Cantidad</th>
                                </tr>
                                {item.indicadores.map((valor) => {
                                    if (valor.ventasEfectivo) {return(
                                        <tr key={valor.ventasEfectivo}>
                                            <td id="itemId">Ventas Efectivo</td>
                                            <td></td>
                                            <td>{valor.ventasEfectivo}</td>
                                            <td>{valor.cantidad}</td>
                                        </tr>
                                    )} else if (valor.ventasPaypal) {return(
                                        <tr key={valor.ventasPaypal}>
                                            <td id="itemId">Ventas PayPal</td>
                                            <td></td>
                                            <td>{valor.ventasPaypal}</td>
                                            <td>{valor.cantidad}</td>
                                        </tr>
                                    )} else if (valor.devolucionesEfectivo) {return(
                                        <tr key={valor.devolucionesEfectivo}>
                                            <td id="itemId">Devoluciones Efectivo</td>
                                            <td>{valor.devolucionesEfectivo}</td>
                                            <td></td>
                                            <td>{valor.cantidad}</td>
                                        </tr>
                                    )} else if (valor.devolucionesPaypal) {return(
                                        <tr key={valor.devolucionesPaypal}>
                                            <td id="itemId">Devoluciones Paypal</td>
                                            <td>{valor.devolucionesPaypal}</td>
                                            <td></td>
                                            <td>{valor.cantidad}</td>
                                        </tr>
                                    )} else return null;
                                    }
                                )}
                                <tr key={item.subtotal}>
                                    <td id="itemId">Subtotal</td>
                                    <td></td>
                                    <td>{item.subtotal}</td>
                                    <td></td>
                                </tr>
                              </Col>
                            </>  
                          )}
                        )
                      : null}
                     
                    {datos.totales ?
                    <Col>
                        <br/>
                        <tr key={2}>
                            <th>Totales</th>
                            <th>Egresos</th>
                            <th>Ingresos</th>
                            <th>Cantidad</th>
                        </tr>
                        {datos.totales.map((valor) => {
                            if (valor.ventasEfectivo) {return(
                                <tr key={valor.ventasEfectivo}>
                                    <td id="itemId">Ventas Efectivo</td>
                                    <td></td>
                                    <td>{valor.ventasEfectivo}</td>
                                    <td>{valor.cantidad}</td>
                                </tr>
                            )} else if (valor.ventasPaypal) {return(
                                <tr key={valor.ventasPaypal}>
                                    <td id="itemId">Ventas PayPal</td>
                                    <td></td>
                                    <td>{valor.ventasPaypal}</td>
                                    <td>{valor.cantidad}</td>
                                </tr>
                            )} else if (valor.devolucionesEfectivo) {return(
                                <tr key={valor.devolucionesEfectivo}>
                                    <td id="itemId">Devoluciones Efectivo</td>
                                    <td>{valor.devolucionesEfectivo}</td>
                                    <td></td>
                                    <td>{valor.cantidad}</td>
                                </tr>
                            )} else if (valor.devolucionesPaypal) {return(
                                <tr key={valor.devolucionesPaypal}>
                                    <td id="itemId">Devoluciones Paypal</td>
                                    <td>{valor.devolucionesPaypal}</td>
                                    <td></td>
                                    <td>{valor.cantidad}</td>
                                </tr>
                            )} else return null;
                            }
                        )}
                    </Col> : null}
                  
              </div>
            </div>
          </main>
        </div>
      </Styles>
    </>
  );
}

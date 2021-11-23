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
  //console.log(datos);
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
                            
                              <Col>
                                <tr key={index}>
                                  <th>{item.mes} {item.anio}</th>
                                  <th>Egresos</th>
                                  <th>Ingresos</th>
                                  <th>Cantidad</th>
                                </tr>
                                
                                    
                                <tr>
                                    <td id="itemId">Ventas Efectivo</td>
                                    <td></td>
                                    <td>{item.indicadores[0].ventasEfectivo}</td>
                                    <td>{item.indicadores[0].cantidad}</td>
                                </tr>
                            
                                <tr>
                                    <td id="itemId">Ventas PayPal</td>
                                    <td></td>
                                    <td>{item.indicadores[1].ventasPaypal}</td>
                                    <td>{item.indicadores[1].cantidad}</td>
                                </tr>
                            
                                <tr>
                                    <td id="itemId">Devoluciones Efectivo</td>
                                    <td>{item.indicadores[2].devolucionesEfectivo}</td>
                                    <td></td>
                                    <td>{item.indicadores[2].cantidad}</td>
                                </tr>
                            
                                <tr>
                                    <td id="itemId">Devoluciones Paypal</td>
                                    <td>{item.indicadores[3].devolucionesPaypal}</td>
                                    <td></td>
                                    <td>{item.indicadores[3].cantidad}</td>
                                </tr>
                                    
                                  
                                
                                <tr>
                                    <td id="itemId">Subtotal</td>
                                    <td></td>
                                    <td>{item.subtotal}</td>
                                    <td></td>
                                </tr>
                              </Col>
                             
                          )}
                        )
                      : null}
                     
                    {datos.totales ?
                    <Col>
                        <br/>
                        <tr>
                            <th>Totales</th>
                            <th>Egresos</th>
                            <th>Ingresos</th>
                            <th>Cantidad</th>
                        </tr>
                        <tr>
                            <td id="itemId">Ventas Efectivo</td>
                            <td></td>
                            <td>{datos.totales[0].ventasEfectivo}</td>
                            <td>{datos.totales[0].cantidad}</td>
                        </tr>
                        <tr>
                            <td id="itemId">Ventas PayPal</td>
                            <td></td>
                            <td>{datos.totales[1].ventasPayPal}</td>
                            <td>{datos.totales[1].cantidad}</td>
                        </tr>
                        <tr>
                            <td id="itemId">Devoluciones Efectivo</td>
                            <td>{datos.totales[2].devolucionesEfectivo}</td>
                            <td></td>
                            <td>{datos.totales[2].cantidad}</td>
                        </tr>
                        <tr>
                            <td id="itemId">Devoluciones Paypal</td>
                            <td>{datos.totales[3].devolucionesPayPal}</td>
                            <td></td>
                            <td>{datos.totales[3].cantidad}</td>
                        </tr>
                    </Col> : null}
                  
              </div>
            </div>
          </main>
        </div>
      </Styles>
    </>
  );
}

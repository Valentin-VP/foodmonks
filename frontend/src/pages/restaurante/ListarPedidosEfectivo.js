import React, { useEffect, useRef, useState } from "react";
import styled from "styled-components";
import { actualizarEstadoPedido, actualizarEstadoUsuario } from "../../services/Requests";
import { ModalItem } from "../../components/ModalItem"
import { Noti } from "../../components/Notification";
import { Col, Modal, Row, Table } from "react-bootstrap";

const Styles = styled.div`
  .lista{
    padding-top: 35px;
  }
  .form-floating {
    margin-bottom: 15px;
  }
  h1 {
    color: #e87121;
    font-weight: bold;
    text-align: center;
    font-size: 30px;
    font-family: "Poppins", sans-serif;
  }
  table {
    background-color: #FFFFFF;
    text-align: center;
    font-family: "Poppins", sans-serif;
    border-collapse: collapse;
    border: 3px solid #FEFEFE;
    width: 100%;
  }

  td, tr {
    border: 1px solid #eee;
    padding: 8px;

    &:hover{
      background-color: #FFFFF5;
    }
  }

  .text-center {
    position: relative;
  }

  .form-floating {
    margin-bottom: 15px;
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

export default function ListadoPedidosEfectivo() {
    const [data, setData] = useState([]);
    const [test, setTest] = useState(false);
    const [modal, setModal] = useState({show: false, item: []});
    const fetch = () => {
      //let a = [{lol: "1", asd: "asdasd"}, {lol: "2", asd: "vbbv"}, {lol: "3", asd: "ff"}];
      //console.log(a.map((item) => (Object.assign(item, {visible: false}))));
      /*obtenerPedidosSinFinalizarEfectivo().then((response)=>{
        if (response.status===200){
          setData(response.data);
        }else{
          Noti(response.data);
        }
      }).catch((error)=>{
        Noti(error.message);
      })*/
      //setData([...data, {tipoUser: "restaurante", nombreRestaurante: "asd", estado : "bloqueado"}]);
    }
    const updateState = (item) => {
      console.log(item);
      actualizarEstadoPedido("FINALIZADO", item.id).then((response)=>{        
        if (response.status===200){
          Noti("El estado del pedido ha sido cambiado.");
          fetch();
        }else{
          Noti(response.data);
        }
      }).catch((error)=>{
        Noti(error.response.data);
      })
    }
    //useEffect(() => {
    //    
    //})

    return (
    <>
      <Styles>
        <div className="container-lg">
          <main className="lista">
            <h1 className="text-center h5 mb-3 fw-normal">Cobrar Pagos Efectivo</h1>
            <div className="form-floating">
              <div className="table-responsive justify-content-center">
                    <table className="table table-hover">
                    <tbody>
                      <Row>
                        <Col>
                          <tr>
                            <td>ID Pedido: item.id</td>
                            <td>Nombre: item.nombre</td>
                            <td>Fecha Confirmación: item.fechaHoraProcesado</td>
                            <td>Fecha Entrega: item.fechaHoraEntrega</td>
                            <td>Total: $item.total</td>
                            <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(setModal({item: [], show:true}))}>
                              Cobrar Pago
                            </button>}</td>
                            <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>{setTest(!test)}}>
                              +
                            </button>}</td>
                          </tr>
                        </Col>
                      </Row>
                        {test &&
                        <Row>
                          <Col>
                            <tr>
                                <td>Menú: item.menus.nombre</td>
                                <td>Precio: $item.menus.precio</td>
                                <td>Descuento: item.menus.multiplicadorPromocion %</td>
                                <td>Cantidad: item.menus.cantidad</td>
                                <td>Total Parcial: $item.total</td>
                            </tr>
                            <tr>
                                <td>Menú: item.menus.nombre</td>
                                <td>Precio: $item.menus.precio</td>
                                <td>Descuento: item.menus.multiplicadorPromocion %</td>
                                <td>Cantidad: item.menus.cantidad</td>
                                <td>Total Parcial: $item.total</td>
                            </tr>
                          </Col>
                        </Row>
                        }
                      {data.map((item, index) => {
                          return (
                            <>
                            <Row>
                              <Col>
                                <tr key={item.id}>
                                  <td>ID Pedido: {item.id}</td>
                                  <td>Nombre: {item.nombre}</td>
                                  <td>Fecha Confirmación: {item.fechaHoraProcesado}</td>
                                  <td>Fecha Entrega: {item.fechaHoraEntrega}</td>
                                  <td>Total: ${item.total}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(setModal({item: [], show:true}))}>
                                    Cobrar Pago
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(item.visible = !item.visible)}>
                                    +
                                  </button>}</td>
                                </tr>
                              </Col>
                            </Row>
                              {item.visible && 
                                <Row>
                                  <Col>
                                    {(data.menus((item, index) => {
                                    return (
                                      <>
                                        <tr key={index}>
                                            <td>Menú: {item.nombre}</td>
                                            <td>Precio: ${item.precio}</td>
                                            <td>Descuento: {item.multiplicadorPromocion} %</td>
                                            <td>Cantidad: {item.cantidad}</td>
                                            <td>Total Parcial: ${item.total}</td>
                                        </tr>
                                      </>
                                    )}))}
                                  </Col>
                                </Row>
                              }
                            </>
                        )})}
                    </tbody>
                    </table>
                  
              </div>
            </div>
          </main>
          <ModalItem
            titulo="Gestión de Pedidos"
            cuerpo="¿Confirmar el cobro del pedido en efectivo? Esto no tiene vuelta atrás."
            visible={modal.show}
            onAceptar={()=>
              {updateState(modal.item);
              setModal({...modal, show:false})}
            }
            onCancelar={()=>
              {alert("Cerrar"); setModal({item:[], show:false})}
            }
          ></ModalItem>
        </div>
      </Styles>
    </>
    )
}
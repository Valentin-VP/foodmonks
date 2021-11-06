import React, { useEffect, useRef, useState } from "react";
import styled from "styled-components";
import { actualizarEstadoPedidoPendientes, obtenerPedidosSinConfirmar } from "../../services/Requests";
import { Noti } from "../../components/Notification";
import { Col, Container, Modal, Row } from "react-bootstrap";
import { ModalItem } from "../../components/ModalItem";

const Styles = styled.div`
  .lista{
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
    padding: 6px;
    width: 8%;
    &:hover{
      background-color: #FFFFF5;
    }
  }

  .row, .col{
    
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

export default function ListadoPedidosPendientes(abierto) {
    const [data, setData] = useState([]);
    const [modal, setModal] = useState({
      show: false,
      item: [],
      estado: "",}
    );
    const [inputMinutos, setInputMinutos] = useState({
      minutos: "60",
    })
    const fetch = () => {
      //let a = [{lol: "1", asd: "asdasd"}, {lol: "2", asd: "vbbv"}, {lol: "3", asd: "ff"}];
      //console.log(a.map((item) => (Object.assign(item, {visible: false}))));
      obtenerPedidosSinConfirmar().then((response)=>{
        if (response.status===200){
          response.data.map((item)=>(Object.assign(item, {visible: false})));
          setData(response.data);
        }else{
          Noti(response.data);
        }
      }).catch((error)=>{
        Noti(error.message);
      })
      //setData([...data, {tipoUser: "restaurante", nombreRestaurante: "asd", estado : "bloqueado"}]);
    }
    const updateState = (item, estado, minutos) => {
        if (!minutos)
          minutos = "60";
        console.log(item);
        actualizarEstadoPedidoPendientes(estado, item.id, minutos).then((response)=>{        
          if (response.status===200){
            Noti("El estado del pedido ha sido cambiado.");
            fetch();
          }else{
            Noti(response.data);
          }
        }).catch((error)=>{
          Noti(error.response.data);
        });
    }

    const onConfirmar = (e, item) =>{
        e.preventDefault();
        setModal({item: item, show:true, estado: "CONFIRMADO"})
    }

    const onRechazar = (e, item) =>{
      e.preventDefault();
      setModal({item: item, show:true, estado: "RECHAZADO"})
    }

    const handleChange = (e) => {
      e.persist();
      setInputMinutos((values) => ({
        ...values,
        [e.target.name]: e.target.value,
      }));
    };

    useEffect(() => {
        fetch();
    }, [])

    return (
    <>
      <Styles>
        <div className="container-lg">
          <main className="lista">
            <h1 className="text-center h5 mb-3 fw-normal">Pedidos Pendientes</h1>
            <div className="form-floating">
              <div className="table-responsive justify-content-center">
                    <table className="table table-hover">
                    <tbody>
                              {/* <Col>
                                <tr>
                                  <td>ID Pedido: 1</td>
                                  <td>Nombre: Peddo1</td>
                                  <td>Dirección: Avenida Italia 1234 esq. Constituyente</td>
                                  <td>Cliente: Nombre Apellido</td>
                                  <td>Medio de Pago: Efectivo</td>
                                  <td>Total: $1234.00</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onConfirmar(e, {id : "1"}))}>
                                    Confirmar
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onRechazar(e, {id: "1"}))}>
                                    Rechazar
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button">
                                    +
                                  </button>}</td>
                                </tr>
                              </Col>
                              <Col>
                                    <tr>
                                        <td>
                                            <img
                                                src={"https://media.istockphoto.com/vectors/creative-hamburger-logo-design-symbol-vector-illustration-vector-id1156464773?k=20&m=1156464773&s=170667a&w=0&h=AcKSZuETET89SF-Liid0mAWTL5w6YQCIxeynD8J01Lk="}
                                                alt="productimg"
                                                width="150"
                                                hight="150"
                                            />
                                        </td>
                                        <td>Menú: Nombre1</td>
                                        <td>Precio: $1234.00</td>
                                        <td>Descuento: 0 %</td>
                                        <td>Total Parcial: $1234.00</td>
                                    </tr>
                              </Col>
                              <Col>
                                <tr>
                                  <td>ID Pedido: 2</td>
                                  <td>Nombre: Peddo2</td>
                                  <td>Dirección: Avenida Italia 1234 esq. Constituyente</td>
                                  <td>Cliente: Nombre Apellido</td>
                                  <td>Medio de Pago: Efectivo</td>
                                  <td>Total: $1234.00</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onConfirmar(e, {id : "2"}))}>
                                    Confirmar
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onRechazar(e, {id: "2"}))}>
                                    Rechazar
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button">
                                    +
                                  </button>}</td>
                                </tr>
                              </Col>
                              <Col>
                                    <tr>
                                        <td>
                                            <img
                                                src={"https://media.istockphoto.com/vectors/creative-hamburger-logo-design-symbol-vector-illustration-vector-id1156464773?k=20&m=1156464773&s=170667a&w=0&h=AcKSZuETET89SF-Liid0mAWTL5w6YQCIxeynD8J01Lk="}
                                                alt="productimg"
                                                width="150"
                                                hight="150"
                                            />
                                        </td>
                                        <td>Menú: Nombre1</td>
                                        <td>Precio: $1234.00</td>
                                        <td>Descuento: 0 %</td>
                                        <td>Total Parcial: $1234.00</td>
                                    </tr>
                              </Col> */}
                      {data ? data.map((item) => {
                          return (
                            <>
                              <Col>
                                <tr key={item.id}>
                                  <td>ID Pedido: {item.id}</td>
                                  <td>Nombre: {item.nombre}</td>
                                  <td>Dirección: {item.direccion}</td>
                                  <td>Cliente: {item.nombreApellidoCliente}</td>
                                  <td>Medio de Pago: {item.medioPago}</td>
                                  <td>Total: ${item.total}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onConfirmar(e, item))} disabled={!abierto}>
                                    Confirmar
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onRechazar(e, item))} disabled={!abierto}>
                                    Rechazar
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(item.visible = !item.visible)}>
                                    +
                                  </button>}</td>
                                </tr>
                              </Col>
                              {item.visible && 
                                <Col>
                                  {(data.menus ? data.menus.map((menu, menuindex) => {
                                  return (
                                    <>
                                      <tr key={menuindex}>
                                          <td>
                                              <img
                                                  src={menu.imagen}
                                                  alt="productimg"
                                                  width="150"
                                                  hight="150"
                                              />
                                          </td>
                                          <td>Menú: {menu.menu}</td>
                                          <td>Precio: ${menu.precio}</td>
                                          <td>Descuento: {menu.multiplicadorPromocion} %</td>
                                          <td>Total Parcial: ${menu.total}</td>
                                          {/* <td>Cantidad: ${item.cantidad}</td> */}
                                      </tr>
                                    </>
                                  )}) : null)}
                                </Col>
                              }
                            </>
                        )}) : null}
                    </tbody>
                    </table>
                  {!data.length > 0 && <h5 className="text-center h5 mb-3 fw-normal">No hay pedidos pendientes</h5>}
              </div>
            </div>
          </main>
          <ModalItem
            titulo="Gestión de Pedidos"
            cuerpo={
            <>
              <div>¿Seguro que {modal.estado==="RECHAZADO" ? "rechazas" : "confirmas"} este pedido? Esto no tiene vuelta atrás.</div>
              {modal.estado==="CONFIRMADO" ?
                <div className="form-floating">
                  <input
                    className="form-control"
                    type="number"
                    name="minutos"
                    id="minutos"
                    value={inputMinutos.minutos}
                    placeholder="60"
                    min="1"
                    max="1000"
                    step="1"
                    onChange={handleChange}
                  />
                  <label htmlFor="floatingInput">T. Estimado (min.)</label>
                </div> : null}
            </>
            }
            visible={modal.show}
            onAceptar={()=>
              {updateState(modal.item, modal.estado, inputMinutos.minutos);
              setModal({item:[], estado: "", show:false})}
            }
            onCancelar={()=>
              {alert("Cerrar");
              setModal({item:[], estado: "", show:false})}
            }
          ></ModalItem>
        </div>
      </Styles>
    </>
    )
}
import React, { useEffect, useRef, useState } from "react";
import styled from "styled-components";
import { actualizarEstadoPedidoPendientes, obtenerPedidosSinConfirmar } from "../../services/Requests";
import { Noti } from "../../components/Notification";
import { Col, Modal, Row } from "react-bootstrap";
import Button from "@restart/ui/esm/Button";

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

  img {
    height: 6rem;
    border-radius: 5px;
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

  #minutos {
    width: 80px;
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
  }
`;

export default function ListadoPedidosPendientes() {
    const [data, setData] = useState([]);
    const [modal, setModal] = useState({
      show: false,
      item: [],
      estado: "",}
    );
    const [inputMinutos, setInputMinutos] = useState({
      show: false,
      minutos: "",
    })
    const fetch = () => {
      //let a = [{lol: "1", asd: "asdasd"}, {lol: "2", asd: "vbbv"}, {lol: "3", asd: "ff"}];
      //console.log(a.map((item) => (Object.assign(item, {visible: false}))));
      obtenerPedidosSinConfirmar().then((response)=>{
        if (response.status===200){
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
          minutos = "0";
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
        if (!inputMinutos.minutos){
            setInputMinutos({...inputMinutos, show: true})
        }else{
            setModal({item: item, show:true, estado: "CONFIRMADO"})
        }
    }

    const onRechazar = (e, item) =>{
      e.preventDefault();
      if (inputMinutos.minutos){
          setInputMinutos({minutos: "", show: false})
      }
      setModal({item: item, show:true, estado: "RECHAZADO"})
  }

    const handleChange = (e) => {
      e.persist();
      setInputMinutos((values) => ({
        ...values,
        [e.target.name]: e.target.value,
      }));
    };

    const ModalItem = ({titulo, cuerpo, visible, onCancelar, onAceptar}) => {
      return(
        <>
          <Modal show={visible} onHide={onCancelar}>
            <Modal.Header closeButton>
              <Modal.Title>{titulo}</Modal.Title>
            </Modal.Header>
            <Modal.Body>{cuerpo}</Modal.Body>
            <Modal.Footer>
              <button className="btn btn-sm btn-secondary" type="button"  onClick={onCancelar}>
                Cancelar
              </button>
              <button className="btn btn-sm btn-danger" type="button" onClick={onAceptar}>
                Aceptar
              </button>
            </Modal.Footer>
          </Modal>
        </>
      )};

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
                            <Row>
                              <Col>
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
                                  {inputMinutos.show ? (<td>{
                                    <>
                                    <input
                                      className="form-control"
                                      type="number"
                                      name="minutos"
                                      id="minutos"
                                      value={inputMinutos.minutos}
                                      placeholder="0"
                                      onChange={handleChange}
                                    />
                                    
                                  </>}</td>) : null}
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onRechazar(e, {id: "1"}))}>
                                    Rechazar
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button">
                                    +
                                  </button>}</td>
                                </tr>
                              </Col>
                            </Row>
                            <Row>
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
                                        {/* <td>Cantidad: ${item.cantidad}</td> */}
                                    </tr>
                              </Col>
                            </Row>
                            <Row>
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
                                  {inputMinutos.show ? (<td>{
                                    <>
                                    <input
                                      className="form-control"
                                      type="number"
                                      name="minutos"
                                      id="minutos"
                                      value={inputMinutos.minutos}
                                      placeholder="0"
                                      onChange={handleChange}
                                    />
                                  </>}</td>) : null}
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onRechazar(e, {id: "2"}))}>
                                    Rechazar
                                  </button>}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button">
                                    +
                                  </button>}</td>
                                </tr>
                              </Col>
                            </Row>
                            <Row>
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
                                        {/* <td>Cantidad: ${item.cantidad}</td> */}
                                    </tr>
                              </Col>
                            </Row>
                      {data ? data.map((item, index) => {
                          return (
                            <>
                            <Row>
                              <Col>
                                <tr key={item.id}>
                                  <td>ID Pedido: {item.id}</td>
                                  <td>Nombre: {item.nombre}</td>
                                  <td>Dirección: {item.direccion}</td>
                                  <td>Cliente: {item.nombreApellidoCliente}</td>
                                  <td>Medio de Pago: {item.medioPago}</td>
                                  <td>Total: ${item.total}</td>
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onConfirmar(e, item))}>
                                    Confirmar
                                  </button>}</td>
                                  {inputMinutos.show ? (<td>{
                                    <div className="form-floating">
                                    <input
                                      className="form-control"
                                      type="number"
                                      name="minutos"
                                      id="minutos"
                                      value={inputMinutos.minutos}
                                      placeholder="0"
                                      onChange={handleChange}
                                    />
                                    <label htmlFor="floatingInput">T. Estimado (min.)</label>
                                  </div>}</td>) : null}
                                  <td>{<button className="btn btn-sm btn-secondary" type="button" onClick={e=>(onRechazar(e, item))}>
                                    Rechazar
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
                                </Row>
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
            cuerpo={"¿Seguro que " + (modal.estado==="RECHAZADO" ? "rechazas" : "confirmas") + " este pedido? Esto no tiene vuelta atrás."}
            visible={modal.show}
            onAceptar={()=>
              {updateState(modal.item, modal.estado, inputMinutos.minutos);
              setModal({item:[], estado: "", show:false});
              setInputMinutos({minutos: "", show: false})}
            }
            onCancelar={()=>
              {alert("Cerrar");
              setModal({item:[], estado: "", show:false});
              setInputMinutos({minutos: "", show: false})}
            }
          ></ModalItem>
        </div>
      </Styles>
    </>
    )
}
import React, { useEffect, useRef, useState } from "react";
import { Button, Table } from "react-bootstrap";
import styled from "styled-components";
import { actualizarEstadoUsuario, setEstadoUsuarioEliminado } from "../../services/Requests";
import { Noti } from "../../components/Notification"

const Styles = styled.div`
  h1 {
    text-align: center;
  }
  table{
    background-color: #FFFFFF;
  }
`;

export default function ListadoRegistrados({data, fetchFunc}) {

    const updateState = (item) => {
      console.log(item);
      const estado = item.estado==="BLOQUEADO" ? "DESBLOQUEAR" : (item.estado==="CERRADO" || item.estado==="ABIERTO" || item.estado==="ACTIVO") ? "BLOQUEAR" : null;
      //// actualizarEstadoUsuario(item).then((response)=>{
      actualizarEstadoUsuario(estado, item.correo).then((response)=>{        
        if (response.status===200){
          fetchFunc();
        }else{
          Noti(response.data);
        }
      }).catch((error)=>{
        Noti(error.message);
      })
    }

    /*const deleteItem = (item) => {
      console.log(item);
      // setEstadoUsuarioEliminado(item.correo).then((response)=>{
      actualizarEstadoUsuario("eliminado", item.correo).then((response)=>{
        if (response.status===200){
          fetchFunc();
        }else{
          alert(response.status);
        }
      }).catch((error)=>{
        alert(error);
      })
    }*/

    //useEffect(() => {
    //    
    //})

    return (
    <>
        <Styles>
            <div className="table-responsive justify-content-center" id="list">
            <table className="table table-hover">
            <tbody>
              {data.map((item, index) => {
                  return (
                    <>
                      <tr key={item.index}>
                        {item.rol==="RESTAURANTE" ? <td>Restaurante</td> : <td>Cliente</td>}
                        <td>Email: {item.correo}</td>
                        <td>Fecha Registro: {item.fechaRegistro}</td>
                        <td>Nombre: {item.rol==="CLIENTE" ? item.nombre : item.nombreRestaurante}</td>
                        {item.rol==="RESTAURANTE" ? <td>Descripción: {item.descripcion}</td> : <td>Apellido: {item.apellido}</td>}
                        {item.rol==="RESTAURANTE" && <td>RUT: {item.rut}</td>}
                        {item.rol==="RESTAURANTE" && <td>Dirección: {item.direccion}</td>}
                        {item.rol==="RESTAURANTE" && <td>Teléfono: {item.telefono}</td>}
                        {item.rol==="CLIENTE" && <td colSpan="3"></td>}
                        <td>Calificación: {item.calificacion}</td>
                        <td>Estado: {item.estado}</td>
                        <td>{<button className="btn btn-sm btn-secondary" disabled={item.estado==="eliminado"} type="button" onClick={e=>(updateState(item))}>
                          {item.estado==="bloqueado" ? "Desbloquear" : "Bloquear"}
                        </button>}</td>
                        <td>{<button className="btn btn-sm btn-danger" disabled={item.estado !== "bloqueado" || item.estado==="eliminado"} type="button" onClick={e=>(updateState(item))}>
                          Eliminar
                        </button>}</td>
                      </tr>
                    </>
                )})}
            </tbody>
          </table>
            </div>
        </Styles>
    </>
    )
}
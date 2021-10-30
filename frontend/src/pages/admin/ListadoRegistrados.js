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
          Noti("El estado del usuario ha sido cambiado.");
          fetchFunc();
          
        }else{
          Noti(response.data);
        }
      }).catch((error)=>{
        Noti(error.response.data);
      })
    }

    const updateStateEliminar = (item) => {
      console.log(item);
      //// actualizarEstadoUsuario(item).then((response)=>{
      actualizarEstadoUsuario("ELIMINAR", item.correo).then((response)=>{        
        if (response.status===200){
          fetchFunc();
          Noti("El estado del usuario ha sido cambiado.");
        }else{
          Noti(response.data);
        }
      }).catch((error)=>{
        Noti(error.response.data);
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
              {data.map((item) => {
                  return (
                    <>
                      <tr key={item.correo}>
                        {item.rol==="RESTAURANTE" ? <td>Restaurante</td> : item.rol==="CLIENTE" ? <td>Cliente</td> : <td>Admin</td>}
                        <td>Email: {item.correo}</td>
                        <td>Fecha Registro: {item.fechaRegistro}</td>
                        <td>Nombre: {item.nombre}</td>
                        {item.rol==="RESTAURANTE" ? <td>RUT: {item.RUT}</td> : <td>Apellido: {item.apellido}</td>}
                        {/*item.rol==="RESTAURANTE" && <td>RUT: {item.RUT}</td>*/}
                        {/*item.rol==="RESTAURANTE" && <td>Dirección: {item.direccion}</td>*/}
                        {item.rol==="RESTAURANTE" && <td>Teléfono: {item.telefono}</td>}
                        {item.rol==="CLIENTE"  ? <td colSpan="1"></td> : (item.rol==="ADMIN" ? <td colSpan="5"></td> : null)}
                        {item.rol!=="ADMIN" && <td>Calificación: {item.calificacion}</td>}
                        {item.rol!=="ADMIN" && <td>Estado: {item.estado}</td>}
                        {item.rol!=="ADMIN" && <td>{<button className="btn btn-sm btn-secondary" disabled={item.estado==="ELIMINADO"} type="button" onClick={e=>(updateState(item))}>
                          {item.estado==="BLOQUEADO" ? "Desbloquear" : "Bloquear"}
                        </button>}</td>}
                        {item.rol!=="ADMIN" && <td>{<button className="btn btn-sm btn-danger" disabled={item.estado !== "BLOQUEADO" || item.estado==="ELIMINADO"} type="button" onClick={e=>(updateStateEliminar(item))}>
                          Eliminar
                        </button>}</td>}
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
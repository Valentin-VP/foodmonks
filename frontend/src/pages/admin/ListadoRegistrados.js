import React, { useEffect, useRef, useState } from "react";
import { Button, Table } from "react-bootstrap";
import styled from "styled-components";
import { actualizarEstadoUsuario, setEstadoUsuarioEliminado } from "../../services/Requests";

const Styles = styled.div`
  h1 {
    text-align: center;
  }
  table{
    background-color: #FFFFFF;
  }
`;

export default function ListadoRegistrados({data, fetchFunc}) {
    console.log(data);
    const updateState = (item) => {
      console.log(item);
      // actualizarEstadoUsuario(item).then((response)=>{
      //   if (response.status===200){
      //     fetchFunc();
      //   }else{
      //     alert(response.status);
      //   }
      // }).catch((error)=>{
      //   alert(error);
      // })
    }

    const deleteItem = (item) => {
      console.log(item);
      // setEstadoUsuarioEliminado(item.correo).then((response)=>{
      //   if (response.status===200){
      //     fetchFunc();
      //   }else{
      //     alert(response.status);
      //   }
      // }).catch((error)=>{
      //   alert(error);
      // })
    }

    //useEffect(() => {
    //    
    //})

    return (
    <>
        <Styles>
            <div className="table-responsive" id="list">
            <table className="table table-hover">
            <thead>
              <tr>
                <th>Tipo</th>
                <th>Email</th>
                <th>Nombre</th>
                <th>Apellido</th>
                <th>Fecha de Registro</th>
                <th>Restaurante</th>
                <th>RUT</th>
                <th>Direccion</th>
                <th>Telefono</th>
                <th>Descripcion</th>
                <th>Calificacion</th>
                <th>Estado</th>
                <th></th>
                <th></th>
              </tr>
            </thead>
            <tbody>
                    <tr>
                        <td>item.tipoUser</td>
                        <td>item.correo</td>
                        <td>item.nombre</td>
                        <td>item.apellido</td>
                        <td>item.fechareg</td>
                        <td>item.fechareg</td>
                        <td>item.fechareg</td>
                        <td>item.fechareg</td>
                        <td>item.fechareg</td>
                        <td>item.fechareg</td>
                        <td>item.fechareg</td>
                        <td>item.fechareg</td>
                        <td><button className="btn btn-sm btn-secondary" type="button" onClick={e=>(fetchFunc())}>  </button></td>
                        <td><button className="btn btn-sm btn-danger" type="button" onClick={e=>(alert(e.target))}>  </button></td>
                    </tr>
              {data.map((item) => {
                  return (
                    <>
                      <tr key={item.correo}>
                        <td>{item.tipoUser}</td>
                        <td>{item.correo}</td>
                        <td>{item.nombre}</td>
                        <td>{item.apellido}</td>
                        <td>{item.fechareg}</td>
                        <td>{item.tipoUser==="restaurante" && item.rest}</td>
                        <td>{item.tipoUser==="restaurante" && item.rut}</td>
                        <td>{item.tipoUser==="restaurante" && item.direccion}</td>
                        <td>{item.tipoUser==="restaurante" && item.telefono}</td>
                        <td>{item.tipoUser==="restaurante" && item.desc}</td>
                        <td>{item.tipoUser!=="admin" && item.calificacion}</td>
                        <td>{item.tipoUser!=="admin" && item.estado}</td>
                        <td><button className="btn btn-sm btn-secondary" disabled={item.estado==="eliminado"} type="button" onClick={e=>(updateState(item))}>
                          {item.estado==="bloqueado" ? "Desbloquear" : "Bloquear"}
                        </button></td>
                        <td><button className="btn btn-sm btn-danger" disabled={item.estado !== "bloqueado" || item.estado==="eliminado"} type="button" onClick={e=>(deleteItem(item))}>
                          ED
                        </button></td>
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
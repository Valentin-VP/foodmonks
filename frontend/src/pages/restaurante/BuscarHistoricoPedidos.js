import { React, Fragment, useState, useEffect, useRef } from "react";
import styled from "styled-components";
import { fetchRestaurantesBusqueda, obtenerPedidosHistorico } from "../../services/Requests";
import { Noti } from "../../components/Notification"
import ListadoHistoricoPedidos from "./ListadoHistoricoPedidos";
import DatePicker from "react-datepicker";

const Styles = styled.div`
  .form{
    padding-top: 35px;
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
    border-radius: 10px;
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

  input {
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
  }

  .form-check-input{
    &:hover {
      border-color: #2080FF;
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
  }
  #fecha{
    height: 58px;
  }
`;

export default function BuscarHistoricoPedidos() {
  const [data, setData] = useState([]);
  //const [isLoading, setIsLoading] = useState(false);
  //const [loaded, setLoaded] = useState(false);
  //const [error, setError] = useState(false);
  const [values, setValues] = useState({
    estadoPedido: "",
    medioPago: "",
    minTotal: "",
    maxTotal: "",
    ordenamiento: "",
  });

  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const onChangeDate = (dates) => {
    const [start, end] = dates;
    setStartDate(start);
    setEndDate(end);
  };

  let estadoPedido = [
    { nombre: "(Devuelto y Finalizado)", value: "" },
    { nombre: "Devuelto", value: "DEVUELTO" },
    { nombre: "Finalizado", value: "FINALIZADO"},
  ];

  let medioPago = [
    { nombre: "(Cualquiera)", value: "" },
    { nombre: "PayPal", value: "PAYPAL" },
    { nombre: "Efectivo", value: "EFECTIVO"},
  ];

  let ordenamiento = [
    { nombre: "(Ninguno)", value: "" },
    { nombre: "Precio (asc.)", value: "asc" },
    { nombre: "Precio (desc.)", value: "desc"},
  ];

  const fetch = (page) => {
    let p = page ? page - 1 : 0;
    console.log(p);
    obtenerPedidosHistorico(values, startDate, endDate, p).then((response)=>{
      if (response.status===200){
        //console.log(response.data);
        setData(response.data);
      }else{
        Noti(response.data);
      }
    }).catch((error)=>{
      Noti(error.response.data);
    })
  }

  const onPageChange = (page) => {
    fetch(page);
  }

  const handleChange = (e) => {
    e.persist();
    setValues((values) => ({
      ...values,
      [e.target.name]: e.target.type === 'checkbox' ? e.target.checked : e.target.value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    fetch();
  };

  const onVisible = (id) => {
    let items = [...data.pedidos];
    //de paso le pregunto si tiene menus (normalmente deberia tener), sino tiene no hago nada
    items.map((i)=>{
      if (i.id===id && i.menus)
        i.visible = !i.visible;
      return i
    });
    console.log(items);
    setData({...data, pedidos: items});
  }

  return (
    <Styles>
      <Fragment>
        <div className="container-lg">
          <main className="form">
            <form id="inputs" onSubmit={handleSubmit}>
              <div class="row align-items-center">

                  <div class="col-lg">
                      <div className="form-floating">
                          <input 
                              name="minTotal"
                              className="form-control"
                              type="number"
                              onChange={handleChange}
                              id="minTotal"
                              min="0"
                              max="100000"
                              value={values.minTotal}>
                          </input>
                          <label htmlFor="minTotal">Total [</label>
                      </div>
                      <div className="form-floating">
                          <input 
                              name="maxTotal"
                              className="form-control"
                              type="number"
                              onChange={handleChange}
                              id="maxTotal"
                              min="0"
                              max="100000"
                              value={values.maxTotal}>
                          </input>
                          <label htmlFor="maxTotal">Total ]</label>
                      </div>
                  </div>
                  <div className="col-lg">
                      <div className="form-floating">
                          <DatePicker
                            id="fecha"
                            name="fecha"
                            className="form-control"
                            selected={startDate}
                            onChange={onChangeDate}
                            startDate={startDate}
                            endDate={endDate}
                            selectsRange
                            dateFormat="yyyy-MM-dd"
                            placeholderText="Fechas Entrega"
                          />
                      </div>
                      <div className="form-floating">
                          <select 
                              name="ordenamiento"
                              className="form-select"
                              onChange={handleChange}
                              id="ordenamiento">
                              {ordenamiento.map((item)=>(
                                <option key={item.nombre} value={item.value}>{item.nombre}</option>
                              ))}
                          </select>
                          <label htmlFor="ordenamiento">Ordenamiento</label>
                      </div>
                  </div>
                  <div class="col-lg">
                      <div className="form-floating">
                          <select 
                              name="estadoPedido"
                              className="form-select"
                              onChange={handleChange}
                              id="estadoPedido">
                              {estadoPedido.map((item)=>(
                                <option key={item.nombre} value={item.value}>{item.nombre}</option>
                              ))}
                          </select>
                          <label htmlFor="estadoPedido">Estado</label>
                      </div>
                      <div className="form-floating">
                          <select 
                              name="medioPago"
                              className="form-select"
                              onChange={handleChange}
                              id="medioPago">
                              {medioPago.map((item)=>(
                                <option key={item.nombre} value={item.value}>{item.nombre}</option>
                              ))}
                          </select>
                          <label htmlFor="medioPago">Medio de pago</label>
                      </div>
                  </div>
              </div>

              <button className="w-100 btn btn-md btn-primary" type="submit">
                Buscar
              </button>
            </form>
              <div className="form-floating">
                {/*Espacio para alguna otra cosa?¿?*/}
              </div>

              <div className="form-floating">
                <div class="row align-items-center">
                  <div class="col-md">
                    {<ListadoHistoricoPedidos datos={data} cantidadPages={data.totalPages} onPageChange={onPageChange} onVisible={onVisible}/>}
                  </div>
                </div>
              </div>
          </main>
        </div>
      </Fragment>
    </Styles>
  );
}
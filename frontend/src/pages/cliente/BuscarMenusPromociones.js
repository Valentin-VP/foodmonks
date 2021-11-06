import { React, Fragment, useState } from "react";
import styled from "styled-components";
import { Layout } from "../../components/Layout";
import { fetchMenusPromos } from "../../services/Requests";
import ListadoMenusPromociones from "./ListarMenusPromociones";
import { PortadaRestaurante } from "../../components/PortadaRestaurante";
import { Noti } from "../../components/Notification"

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

`;

export default function BuscarMenusPromociones() {
  const [listar, setListar] = useState(false);
  const [data, setData] = useState([]);
  const [values, setValues] = useState({
    categoria: "",
    precioInicial: "",
    precioFinal: "",
  });

  let categoria = [
    { nombre: "(Cualquiera)", value: "" },
    { nombre: "Pizzas", value: "PIZZAS" },
    { nombre: "Hamburguesas", value: "HAMBURGUESAS"},
    { nombre: "Bebidas", value: "BEBIDAS" },
    { nombre: "Combos", value: "COMBOS" },
    { nombre: "Minutas", value: "MINUTAS" },
    { nombre: "Postres", value: "POSTRES" },
    { nombre: "Pastas", value: "PASTAS" },
    { nombre: "Comida Arabe", value: "COMIDAARABE" },
    { nombre: "Sushi", value: "SUSHI" },
    { nombre: "Otros", value: "OTROS" },
  ];

  const props = {
    nombre: "nombreRestaurante",
    calificacion: "5",
    logo: "https://firebasestorage.googleapis.com/v0/b/foodmonks-70c28.appspot.com/o/menus%2Fsin_imagen.png?alt=media&to"
  }

  const handleChange = (e) => {
    e.persist();
    setValues((values) => ({
      ...values,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    fetchMenusPromos(values).then((response)=>{
        if (response.status===200){
          console.log(response.data);
          setListar(true);
          setData(response.data);
        }else{
          Noti(response.data);
        }
    }).catch((error)=>{
        Noti(error.response.data);
    })
  };

  return (
    <Styles>
      <Fragment>
          <PortadaRestaurante props={props} />
          <div className="container-lg">
            <main className="form">
              <form id="inputs" onSubmit={handleSubmit}>
                <div class="row align-items-center">
                    <div class="col-md-4">
                        <div className="form-floating">
                            <select 
                                name="categoria"
                                className="form-select"
                                onChange={handleChange}
                                id="categoria">
                                {categoria.map((item)=>(
                                  <option key={item.nombre} value={item.value}>{item.nombre}</option>
                                ))}
                            </select>
                            <label for="categoria">Categoría</label>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div className="form-floating">
                            <input 
                                name="precioInicial"
                                className="form-control"
                                onChange={handleChange}
                                id="precioInicial"
                                value={values.precioInicial}>
                            </input>
                            <label for="precioInicial">Precio Inicial</label>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div className="form-floating">
                            <input 
                                name="precioFinal"
                                className="form-control"
                                onChange={handleChange}
                                id="precioFinal"
                                value={values.precioFinal}>
                            </input>
                            <label for="precioFinal">Precio Final</label>
                        </div>
                    </div>
                </div>

                <button className="w-100 btn btn-md btn-primary" type="submit">
                  Buscar
                </button>
              </form>

                <div className="form-floating">
                  <div class="row align-items-center">
                    <div class="col-md">
                      {listar && <ListadoMenusPromociones data={data} />}
                    </div>
                  </div>
                </div>
            </main>
          </div>
      </Fragment>
    </Styles>
  );
}
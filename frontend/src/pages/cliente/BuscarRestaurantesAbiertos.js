import { React, useState, useEffect } from "react";
import { Container, InputGroup } from "react-bootstrap";
import styled from "styled-components";
import food from "../../assets/food2.jpg";
import { Layout } from "../../components/Layout";
import ListadoRestaurantesAbiertos from "./ListadoRestaurantesAbiertos";
import { fetchUserData } from "../../services/Requests";
import { Loading } from "../../components/Loading";

const Styles = styled.div`
  .portada {
    display: flex;
    background-image: url(${food}) ;
    background-size: cover;
    background-color: #E5E5E5;
    height: 25rem;
    justify-content: center;
    align-items: center;
    margin-bottom: 40px;
    }


    #nombre{
        border: none;
        border-radius: 30px;
        &:focus{
            box-shadow: 0 0 0 .25rem rgba(232, 113, 33,.25);
        }
    }

    #categoria {
      border: none;
      margin-left: 10px;
      border-radius: 30px;
      max-width: 20%;
      &:focus{
          box-shadow: 0 0 0 .25rem rgba(232, 113, 33,.25);
      }
    }

    #direcciones {
      border: none;
      margin-left: 10px;
      border-radius: 30px;
      max-width: 20%;
      &:focus{
          box-shadow: 0 0 0 .25rem rgba(232, 113, 33,.25);
      }
    }

    #cText{
      margin-top: 10px;
      color: white;
      margin-left: 20px;
      text-shadow: 0px 0px 20px rgba(0,0,0,1);
      input{
        &:hover{
          border-color: #E87121;
        }
      }
    }
    
    #boton{
        width: 150px;
        color: white;
        background-color: #E87121;
        border: none;
        margin-left: 10px;
        border-radius: 30px;
        &:focus{
            box-shadow: 0 0 0 .25rem rgba(232, 113, 33,.25);
        }
    }
  }   

  #grupo{
    border-radius: 5px;
    height: 50px;
  }

  #buscador{
  }

  .form {
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

  .form-check-input {
    &:hover {
      border-color: #2080ff;
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
  }
`;

export default function BuscarRestaurantesAbiertos() {
  const [cliente, setCliente] = useState();

  useEffect(() => {
    fetchInfoCliente();
    console.log(cliente);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const [values, setValues] = useState({
    categoria: "",
    nombre: "",
    calificacion: false,
    idDireccion: "",
  });

  const fetchInfoCliente = () => {
    fetchUserData().then((response) => {
      setCliente(response.data);
    });
  };

  let categoria = [
    { nombre: "Pizzas", value: "PIZZAS" },
    { nombre: "Hamburguesas", value: "HAMBURGUESAS" },
    { nombre: "Bebidas", value: "BEBIDAS" },
    { nombre: "Combos", value: "COMBOS" },
    { nombre: "Minutas", value: "MINUTAS" },
    { nombre: "Postres", value: "POSTRES" },
    { nombre: "Pastas", value: "PASTAS" },
    { nombre: "Comida Arabe", value: "COMIDAARABE" },
    { nombre: "Sushi", value: "SUSHI" },
    { nombre: "Otros", value: "OTROS" },
  ];

  const handleChange = (e) => {
    e.persist();
    setValues((values) => ({
      ...values,
      [e.target.name]:
        e.target.type === "checkbox" ? e.target.checked : e.target.value,
    }));
    console.log(e.target);
    console.log(e.target.value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    sessionStorage.setItem("restaurantes-categoria", values.categoria);
    sessionStorage.setItem("restaurantes-nombre", values.nombre);
    sessionStorage.setItem("restaurantes-calificacion", values.calificacion);
    sessionStorage.setItem("cliente-direccion", values.idDireccion);
    sessionStorage.setItem("cliente-calle", cliente.direcciones.calle);
    sessionStorage.setItem("cliente-numero", cliente.direcciones.numero);
    window.location.reload();
  };

  return (
    <Styles>
      <form id="inputs" onSubmit={handleSubmit}>
        <Container fluid className="portada">
          <Container id="buscador">
            <InputGroup id="grupo">
              <input
                name="nombre"
                id="nombre"
                type="text"
                className="form-control"
                placeholder="Nombre"
                onChange={handleChange}
              />
              <select
                name="categoria"
                className="form-select"
                onChange={handleChange}
                id="categoria"
                defaultValue={"DEFAULT"}
              >
                <option value="DEFAULT" disabled>
                  Categoria
                </option>
                {categoria.map((item) => (
                  <option key={item.nombre} value={item.value}>
                    {item.nombre}
                  </option>
                ))}
              </select>
              <button
                id="boton"
                type="submit"
                className="btn btn-secondary"
                type="submit"
              >
                Buscar
              </button>
            </InputGroup>
            <div className="checkbox">
              <label id="cText">
                <input
                  name="calificacion"
                  className="form-check-input"
                  type="checkbox"
                  checked={values.calificacion}
                  onChange={handleChange}
                  id="calificacion"
                />{" "}
                Ordenar por Calificación
              </label>
            </div>
            <select
              name="direcciones"
              className="mt-2 form-select"
              onChange={handleChange}
              required
              id="direcciones"
              defaultValue={"DEFAULT"}
            >
              <option value="DEFAULT" disabled>
                Direccion
              </option>
              {cliente.direcciones.map((item) => (
                <option key={item.id} value={item.id}>
                  {item.calle} {item.numero}
                </option>
              ))}
            </select>
          </Container>
        </Container>
        <Layout>
          <h2>Restaurantes</h2>
          <div className="container-lg">
            <div className="row align-items-center">
              <div className="col-md">{<ListadoRestaurantesAbiertos />}</div>
            </div>
          </div>
        </Layout>
      </form>
    </Styles>
  );
}

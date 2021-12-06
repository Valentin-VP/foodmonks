import { React, useState, useEffect } from "react";
import { Container, InputGroup } from "react-bootstrap";
import styled from "styled-components";
import food from "../../assets/food2.jpg";
import { Layout } from "../../components/Layout";
import ListadoRestaurantesAbiertos from "./ListadoRestaurantesAbiertos";
import { fetchUserData } from "../../services/Requests";
import { Loading } from "../../components/Loading";
import { Noti } from "../../components/Notification";

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
      max-width: 15%;
      &:focus{
          box-shadow: 0 0 0 .25rem rgba(232, 113, 33,.25);
      }
    }

    #direcciones {
      border: none;
      margin-left: 10px;
      border-radius: 30px;
      max-width: 15%;
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
  const [cargando, setCargando] = useState(true);

  const [values, setValues] = useState({
    categoria: "",
    nombre: "",
    calificacion: false,
    idDireccion: "",
  });

  useEffect(() => {
    values.idDireccion = sessionStorage.getItem("cliente-direccion");
    fetchInfoCliente();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetchInfoCliente = () => {
    fetchUserData().then((response) => {
      setCliente(response.data);
      setCargando(false);
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
  };

  const setCalleNumero = (id) => {
    var iterador = 0;
    console.log(cliente);
    cliente.direcciones.map((dir) => {
      console.log(dir);
      console.log(id);
      console.log(iterador);
      if (dir.id == id) {
        sessionStorage.setItem(
          "cliente-calle",
          cliente.direcciones[iterador].calle
        );
        sessionStorage.setItem(
          "cliente-numero",
          cliente.direcciones[iterador].numero
        );
      } else {
        iterador++;
      }
      return null;
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    sessionStorage.setItem(
      "cliente-direccion",
      document.getElementById("direcciones").value
    );
    if (document.getElementById("direcciones").value === "") {
      Noti("Debe seleccionar una direccion");
      return null;
    }
    setCalleNumero(document.getElementById("direcciones").value);
    sessionStorage.setItem("restaurantes-categoria", values.categoria);
    sessionStorage.setItem("restaurantes-nombre", values.nombre);
    sessionStorage.setItem("restaurantes-calificacion", values.calificacion);
    window.location.reload();
  };

  if (cargando) {
    return <Loading />;
  }

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
              <select
                name="idDireccion"
                className="form-select"
                id="direcciones"
              >
                <option value="">Direccion</option>
                {cliente.direcciones.map((item) => (
                  <option key={item.id} value={item.id}>
                    {item.calle} {item.numero}
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
                Ordenar por calificación
              </label>
            </div>
          </Container>
        </Container>
        <Layout>
          <h2>Restaurantes</h2>
          <div className="container-lg">
            <div className="row align-items-center">
              {values.idDireccion === null ? (
                <h5 className="text-center h5 mb-3 fw-normal">
                  Elija una direccion para ver restaurantes abiertos cerca de su
                  zona.
                </h5>
              ) : (
                <div className="col-md">{<ListadoRestaurantesAbiertos />}</div>
              )}
            </div>
          </div>
        </Layout>
      </form>
    </Styles>
  );
}

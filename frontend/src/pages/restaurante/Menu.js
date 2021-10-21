import { React, useState, useEffect } from "react";
import { Layout } from "../../components/Layout";
import { Button, InputGroup } from "react-bootstrap";
import styled from "styled-components";
import { fetchMenus, eliminarMenu } from "../../services/Requests";

const Styles = styled.div`
  #cabecera {
    padding-top: 20px;
    padding-bottom: 30px;
  }

  #titulo {
    text-decoration: none;
    font-size: 30px;
    font-family: "Poppins", sans-serif;
    color: #e87121;
    padding-right: 30px;
  }

  #alta {
    width: 50px;
    background-color: #009933;
    border: none;
    border-radius: 5px;
  }

  h1 {
    margin-top: 20px;
  }
  img {
    height: 6rem;
    border-radius: 5px;
  }
  .miBoton {
    color: white;
    background-color: #e87121;
    border: none;
    margin-bottom: 5px;
    margin-left: -10px;
    &:focus {
      box-shadow: 0 0 0 0.25rem rgba(232, 113, 33, 0.25);
    }
    &:hover {
      background-color: #da6416;
    }
    $:active {
      background-color: black !important;
    }
  }

  table {
  }

  table,
  tbody,
  tr,
  td {
    background-color: white;
  }
`;

function Menu() {
  const [menus, setMenus] = useState();
  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    fetchMenus().then((response) => {
      console.log("paso por fetch");
      setMenus(response.data);
      setLoading(false);
    });
  }, []);

  const onEliminar = (id) => {
    eliminarMenu(id).then((response) => {
      console.log(response.data);
      window.location.reload();
    });
  };

  const onModificar = (id) => {
    sessionStorage.setItem("menuId", id);
  };

  if (isLoading) {
    return <div className="App">Cargando...</div>;
  }

  return (
    <Styles>
      <Layout>
        <InputGroup id="cabecera">
          <h2 id="titulo">Mis Menús</h2>
          <Button id="alta" variant="success" href="/altaMenu">
            {"+"}
          </Button>
        </InputGroup>
        <div className="row justify-content-center">
          <div className="col-12">
            <table className="table table-light table-hover m-0">
              <tbody>
                {menus.map((menu, index) => {
                  return (
                    <tr key={index}>
                      <td>
                        <img
                          src={menu.imagen}
                          alt="productimg"
                          width="150"
                          hight="150"
                        />
                      </td>
                      <td>{menu.nombre}</td>
                      <td>${menu.price}</td>
                      {menu.multiplicadorPromocion !== 0 ? (
                        <td>descuento: {menu.multiplicadorPromocion}%</td>
                      ) : null}
                      <td>Descripcion: {menu.descripcion}</td>
                      <td>Categoria: {menu.categoria}</td>
                      <td>
                        <div className="row">
                          <Button
                            className="miBoton"
                            onClick={() => {
                              onEliminar(menu.id);
                            }}
                          >
                            Eliminar
                          </Button>
                        </div>
                        <div className="row">
                          <Button
                            className="miBoton"
                            href="/modificarMenu"
                            onClick={() => {
                              onModificar(menu.id);
                            }}
                          >
                            Modificar
                          </Button>
                        </div>
                        <div className="row">
                          <Button className="miBoton">Promocionar</Button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </Layout>
    </Styles>
  );
}

export default Menu;

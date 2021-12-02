import React, { useState, useEffect } from "react";
import { Layout } from "../../components/Layout";
import { Button, Form } from "react-bootstrap";
import styled from "styled-components";
import { Loading } from "../../components/Loading";
import {
  cambiarEstadoRestaurante,
  listarRestaurantesPorEstado,
} from "../../services/Requests";
import RestauranteCard from "./RestauranteCardRA";
import Modal, { ModalProvider } from "styled-react-modal";
import { getMenusFromRestaurante } from "../../services/Requests";
import { Noti, NotiError } from "../../components/Notification";

const StyledModal = Modal.styled`
  border-radius: 5px;
  padding: 1.5%;
  width: 50%;
  align-items: center;
  justify-content: center;
  background-color: white;
  overflow-y: auto;
  max-height: 80%;
  

  img {
    object-fit: cover;
  }

  .cuerpo{
    margin-bottom: 15px;
  }
  .abajo{
    text-align: right;
  }
  Button {
    margin-left: 5px;
  }

  h2 {
    max-width: 100%;
  }

  .tImg {
    object-fit: cover;
  }

  .my-table{ 
    max-width: 100%;
  }

  p{
    margin: 0;
  }

  .lowMargin{
    margin: 0;
    padding: 0;
  }
`;

const Styles = styled.div`
  .titulo {
    padding-top: 15px;
    text-align: center;
    color: #e88121;
    font-weight: bold;
    text-align: center;
    font-size: 30px;
    font-family: "Poppins", sans-serif;
  }

  .column {
    float: left;
    width: 18.75rem;
    padding: 0 10px;
    margin-bottom: 5%;
  }
  @media screen and (max-width: 700px) {
    .column {
      width: 100%;
      display: block;
      margin-bottom: 20px;
    }
  }
`;

export default function Home() {
  const [restaurantes, setRestaurantes] = useState();
  const [isLoading, setLoading] = useState(true);
  const [restauranteSeleccionado, setRestSelected] = useState();
  const [menus, setMenus] = useState();
  // para el modal -----------------------------------------------------------------------------------------------
  const [isOpen, setIsOpen] = useState(false);
  const [modalIsLoading, setMLoading] = useState(true);

  const toggleModal = (restaurante) => {
    setRestSelected(restaurante);
    getMenusFromRestaurante(restaurante.correo)
      .then((response) => {
        setMenus(response.data);
        setIsOpen(!isOpen);
        setMLoading(false);
      })
      .catch((error) => {
        NotiError(error.response.data);
      });
  };
  //termina para el modal ---------------------------------------------------------------------------------------

  useEffect(() => {
    listarRestaurantesPorEstado("PENDIENTE")
      .then((response) => {
        setRestaurantes(response.data);
        setLoading(false);
      })
      .catch((error) => {
        NotiError(error.response.data.detailMessage);
      });
  }, []);

  const onUpdate = (estado) => {
    if (document.getElementById("mensaje").value === "") {
      return null;
    }
    const data = {
      comentariosCambioEstado: document.getElementById("mensaje").value,
    };

    cambiarEstadoRestaurante(restauranteSeleccionado.correo, estado, data)
      .then((response) => {
        window.location.reload();
      })
      .catch((error) => {
        NotiError(error.response.data.detailMessage);
      });
  };

  if (isLoading) {
    return <Loading />;
  }

  return (
    <Styles>
      <ModalProvider>
        <Layout>
          <h2 className="titulo">Restaurantes con registros pendientes</h2>
          <div className="table-responsive justify-content-center" id="list">
            {restaurantes.map((item, index) => {
              return (
                <div
                  className="column mt-5"
                  onClick={() => toggleModal(item)}
                  key={index}
                >
                  <RestauranteCard
                    correo={item.correo}
                    imagen={item.imagen}
                    nombre={item.nombreRestaurante}
                    telefono={item.telefono}
                    item={item}
                  />
                </div>
              );
            })}
          </div>
        </Layout>
        {!modalIsLoading ? (
          <StyledModal
            isOpen={isOpen}
            onBackgroundClick={() => setIsOpen(!isOpen)}
            onEscapeKeydown={() => setIsOpen(!isOpen)}
          >
            <h2>{restauranteSeleccionado.nombreRestaurante}</h2>
            <hr />
            {console.log(restauranteSeleccionado)}
            <h4>Info</h4>
            <p>Correo:{restauranteSeleccionado.correo}</p>
            <hr className="lowMargin" />
            <p>Paypal: {restauranteSeleccionado.cuentaPaypal}</p>
            <hr className="lowMargin" />
            <p>Descripción: {restauranteSeleccionado.descripcion}</p>
            <hr className="lowMargin" />
            <p>Rut: {restauranteSeleccionado.rut}</p>
            <hr className="lowMargin" />
            <p>Teléfono: {restauranteSeleccionado.telefono}</p>
            <hr className="lowMargin" />
            <p>
              Dirección: {restauranteSeleccionado.direcciones[0].calle}{" "}
              {restauranteSeleccionado.direcciones[0].numero}
            </p>
            <hr className="lowMargin" />
            <p>Registro: {restauranteSeleccionado.fechaRegistro}</p>
            <h5 className="mt-2">Menus</h5>
            <div className="cuerpo">
              <div className="table-wrapper-scroll-y my-custom-scrollbar">
                <table className="table table-bordered table-striped mb-0 my-table">
                  <thead>
                    <tr>
                      <th scope="col">#</th>
                      <th scope="col">Nombre</th>
                      <th scope="col">Precio</th>
                      <th scope="col">Descripcion</th>
                      <th scope="col">Categoría</th>
                      <th scope="col">Imágen</th>
                    </tr>
                  </thead>
                  <tbody>
                    {menus.map((item, index) => {
                      return (
                        <tr key={index}>
                          <th scope="row">{item.id}</th>
                          <td>{item.nombre}</td>
                          <td>{item.price}</td>
                          <td>{item.descripcion}</td>
                          <td>{item.categoria}</td>
                          <td>
                            <img
                              className="tImg"
                              src={item.imagen}
                              alt="menu"
                              border="2"
                              height="50"
                              width="50"
                            ></img>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            </div>
            <hr />
            <Form>
              <div className="form-floating">
                <input
                  className="form-control mb-2"
                  type="text"
                  name="mensaje"
                  id="mensaje"
                  placeholder="Mensaje"
                  required
                />
                <label htmlFor="floatingInput">Mensaje</label>
              </div>
              <div className="abajo">
                <Button
                  variant="success"
                  type="submit"
                  onClick={() => onUpdate("CERRADO")}
                >
                  Aceptar Registro
                </Button>
                <Button
                  variant="danger"
                  type="submit"
                  onClick={() => onUpdate("RECHAZADO")}
                >
                  Rechazar Registro
                </Button>
              </div>
            </Form>
          </StyledModal>
        ) : null}
      </ModalProvider>
    </Styles>
  );
}

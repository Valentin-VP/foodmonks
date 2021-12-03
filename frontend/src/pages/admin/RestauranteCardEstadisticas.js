import React, { useState } from "react";
import styled from "styled-components";
import { Button } from "react-bootstrap";
import Modal, { ModalProvider } from "styled-react-modal";
import { estadisticasVentasRestaurante } from "../../services/Requests";
import { NotiError } from "../../components/Notification";
import { Line } from "react-chartjs-2";

const StyledModal = Modal.styled`
  border-radius: 5px;
  padding: 1.5%;
  width: 60%;
  align-items: center;
  justify-content: center;
  background-color: white;
  overflow-y:inherit !important;

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
`;

const Styles = styled.div`
  .card {
    &:active {
      transform: scale(0.95);
    }
  }
  img {
    object-fit: cover;
    border-radius: 3px 3px 0px 0px;
    height: 15rem;
    position: relative;
    top: 0;
    left: 0;
  }
  .btn-primary {
    color: white;
    background-color: #e87121;
    border: none;
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
`;

const RestauranteCard = (props) => {
  const [ventas, setVentas] = useState(null);
  // para el modal -----------------------------------------------------------------------------------------------
  const [isOpen, setIsOpen] = useState(false);
  const [modalIsLoading, setMLoading] = useState(true);
  let year = sessionStorage.getItem("fechaStat");

  const toggleModal = (restaurante) => {
    estadisticasVentasRestaurante(restaurante, year)
      .then((response) => {
        console.log(response.data);
        var ventasRestaurante = [];
        response.data.ventas.forEach((mes) => {
          ventasRestaurante.push(mes.cantidad);
        });
        setVentas(ventasRestaurante);
        setIsOpen(!isOpen);
        setMLoading(false);
      })
      .catch((error) => {
        NotiError(error.response.data);
      });
  };
  //termina para el modal ---------------------------------------------------------------------------------------

  return (
    <Styles>
      <ModalProvider>
        <div className="card" onClick={() => toggleModal(props.correo)}>
          <img src={props.imagen} alt="restauranteimg" />
          <div className="card-body">
            <h5 className="card-title">{props.nombre}</h5>
            <h5 className="card-subtitle">Teléfono: {props.telefono}</h5>
            <p className="card-text">{props.calificacion}⭐</p>
          </div>
        </div>

        {!modalIsLoading ? (
          <StyledModal
            isOpen={isOpen}
            onBackgroundClick={() => setIsOpen(!isOpen)}
            onEscapeKeydown={() => setIsOpen(!isOpen)}
          >
            <h2>{props.nombre}</h2>
            <hr />
            <h5>Ventas {year}</h5>
            <div className="cuerpo">
              <Line
                data={{
                  labels: [
                    "Enero",
                    "Febrero",
                    "Marzo",
                    "Abril",
                    "Mayo",
                    "Junio",
                    "Julio",
                    "Agosto",
                    "Septiembre",
                    "Octubre",
                    "Noviembre",
                    "Diciembre",
                  ],
                  datasets: [
                    {
                      label: "Ventas",
                      data: ventas,
                      backgroundColor: [
                        "rgba(255, 99, 132, 0.2)",
                        "rgba(54, 162, 235, 0.2)",
                        "rgba(255, 206, 86, 0.2)",
                        "rgba(75, 192, 192, 0.2)",
                        "rgba(153, 102, 255, 0.2)",
                        "rgba(255, 159, 64, 0.2)",
                      ],
                      borderColor: [
                        "rgba(255, 99, 132, 1)",
                        "rgba(54, 162, 235, 1)",
                        "rgba(255, 206, 86, 1)",
                        "rgba(75, 192, 192, 1)",
                        "rgba(153, 102, 255, 1)",
                        "rgba(255, 159, 64, 1)",
                      ],
                      borderWidth: 2,
                    },
                  ],
                }}
              />
            </div>
            <hr />
            <div className="abajo">
              <Button
                variant="danger"
                type="submit"
                onClick={() => setIsOpen(false)}
              >
                Cerrar
              </Button>
            </div>
          </StyledModal>
        ) : null}
      </ModalProvider>
    </Styles>
  );
};

export default RestauranteCard;

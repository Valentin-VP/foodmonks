import React, { useEffect, useState } from "react";
import styled from "styled-components";
import { Line, Bar } from "react-chartjs-2";
import { Layout } from "../../components/Layout";
import {
  estadisticasUsuariosTotales,
  estadisticasUsuariosRegistrados,
  estadisticasPedidosRegistrados,
} from "../../services/Requests";
import { NotiError } from "../../components/Notification";
import { Loading } from "../../components/Loading";
import ListadoRestaurantes from "./ListadoRestaurantes";

const Styles = styled.div`
  .titulo {
    padding-top: 1rem;
    color: #e87121;
    font-family: "Poppins", sans-serif;
    font-weight: bold;
    text-align: center;
    text-decoration-underline: true;
  }

  .row {
    margin-top: 20px;
  }

  #bar {
    margin-top: 50px;
    margin-bottom: 50px;
  }

  .myColumn {
    max-width: 50%;
    margin-bottom: 2rem;
  }

  .gTitle {
    font-family: "Poppins", sans-serif;
  }

  #anio,
  #anio2 {
    width: 5rem;
  }
`;

export default function Estadisticas() {
  const [usuariosRegistrados, setUsuariosRegistrados] = useState(null);
  const [usuarios, setUsuarios] = useState(null);
  const [pedidos, setPedidos] = useState(null);
  const [fecha, setFecha] = useState(2021);
  const [fecha2, setFecha2] = useState(2021);
  sessionStorage.setItem("fechaStat", fecha2);

  const onChange = () => {
    setFecha(document.getElementById("anio").value);
  };

  const onChange2 = () => {
    setFecha2(document.getElementById("anio2").value)
    sessionStorage.setItem("fechaStat", fecha2);
  };

  useEffect(() => {
    estadisticasUsuariosTotales()
      .then((response) => {
        setUsuarios(response.data);
      })
      .catch((error) => {
        NotiError(error.response.data);
      });

    estadisticasUsuariosRegistrados()
      .then((response) => {
        var clientesRegistrados = [];
        var restaurantesRegistrados = [];
        response.data.forEach((mes) => {
          clientesRegistrados.push(mes.clientes);
          restaurantesRegistrados.push(mes.restaurantes);
        });
        var data = {
          clientes: clientesRegistrados,
          restaurantes: restaurantesRegistrados,
        };
        setUsuariosRegistrados(data);
      })
      .catch((error) => {
        NotiError(error.response.data);
      });

    estadisticasPedidosRegistrados(fecha)
      .then((response) => {
        var pedidos = [];
        response.data.pedidosRegistrados.forEach((mes) => {
          pedidos.push(mes.cantidad);
        });
        setPedidos(pedidos);
      })
      .catch((error) => {
        NotiError(error.response);
      });
  }, [fecha]);

  if (usuarios === null || usuariosRegistrados === null || pedidos === null) {
    return <Loading />;
  }

  return (
    <Styles>
      <Layout>
        <h1 className="titulo">Estadisticas</h1>
        <div className="row">
          <div className="column myColumn">
            <h3 className="gTitle text-center mb-2">Registro de usuarios</h3>
            <Bar
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
                    label: "Clientes",
                    data: usuariosRegistrados.clientes,
                    backgroundColor: ["rgba(153, 102, 255, 0.2)"],
                    borderColor: ["rgba(153, 102, 255, 1)"],
                    borderWidth: 1,
                  },
                  {
                    label: "Restaurantes",
                    data: usuariosRegistrados.restaurantes,
                    backgroundColor: ["rgba(54, 162, 235, 0.2)"],
                    borderColor: ["rgba(54, 162, 235, 1)"],
                    borderWidth: 1,
                  },
                ],
              }}
            />
          </div>
          <div className="column myColumn">
            <h3 className="gTitle text-center">Total de usuarios</h3>
            <Bar
              data={{
                labels: [""],
                datasets: [
                  {
                    label: "Clientes",
                    data: [usuarios.clientes],
                    backgroundColor: [
                      "rgba(153, 102, 255, 0.2)",
                      "rgba(54, 162, 235, 0.2)",
                    ],
                    borderColor: [
                      "rgba(153, 102, 255, 1)",
                      "rgba(54, 162, 235, 1)",
                    ],
                    borderWidth: 1,
                  },
                  {
                    label: "Restaurantes",
                    data: [usuarios.restaurantes],
                    backgroundColor: ["rgba(54, 162, 235, 0.2)"],
                    borderColor: ["rgba(54, 162, 235, 1)"],
                    borderWidth: 1,
                  },
                ],
              }}
              height={400}
              width={600}
            />
          </div>
          <div className="column">
            <h3 className="gTitle text-center mb-2">
              Pedidos del año{" "}
              <input
                id="anio"
                type="number"
                min="1990"
                max="2099"
                step="1"
                value={fecha}
                onChange={onChange}
                name="anio"
              />
            </h3>
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
                    label: "Pedidios",
                    data: pedidos,
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
        </div>
        <h3 className="gTitle text-center mt-5">Restaurantes registrados</h3>
        <h4> Buscar para el año <input
                id="anio2"
                type="number"
                min="1990"
                max="2099"
                step="1"
                value={fecha2}
                onChange={onChange2}
              /></h4>
        <ListadoRestaurantes />
      </Layout>
    </Styles>
  );
}

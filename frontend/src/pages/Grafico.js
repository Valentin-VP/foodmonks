import React from "react";
import styled from "styled-components";
import { Line, Bar } from "react-chartjs-2";
import { Layout } from "../components/Layout";

const Styles = styled.div`
  h1 {
    font-family: "Poppins", sans-serif;
    text-align: center;
    text-decoration-underline: true;
  }

  #line {
    margin-top: 20px;
  }
  #bar {
    margin-top: 50px;
    margin-bottom: 50px;
  }
`;

export const Grafico = () => (
  <Styles>
    <Layout>
      <h1>Estadisticas</h1>
      <div id="line">
        <Line
          data={{
            labels: ["Abril", "Mayo", "Junio", "Julio", "Agosto", "Setiembre"],
            datasets: [
              {
                label: "Ventas de El Tío Bistró En los ultimos 6 meses",
                data: [120, 190, 30, 50, 20, 30],
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
                borderWidth: 1,
              },
              // {
              //   label: 'Quantity',
              //   data: [47, 52, 67, 58, 9, 50],
              //   backgroundColor: 'orange',
              //   borderColor: 'red',
              // },
            ],
          }}
          height={400}
          width={600}
          options={{
            maintainAspectRatio: false,
            scales: {
              yAxes: [
                {
                  ticks: {
                    beginAtZero: true,
                  },
                },
              ],
            },
            legend: {
              labels: {
                fontSize: 25,
              },
            },
          }}
        />
      </div>
      <div id="bar">
        <Bar
          data={{
            labels: [
              "El Tío Bistró",
              "Primuseum",
              "Cafe Gourmand",
              "Ashot Shawarma",
              "Alquimista Montevideo",
              "Es Mercat",
            ],
            datasets: [
              {
                label: "Restaurantes por calificación",
                data: [5, 4.8, 4.2, 3.9, 3.9, 2.2],
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
                borderWidth: 1,
              },
              // {
              //   label: 'Quantity',
              //   data: [47, 52, 67, 58, 9, 50],
              //   backgroundColor: 'orange',
              //   borderColor: 'red',
              // },
            ],
          }}
          height={400}
          width={600}
          options={{
            maintainAspectRatio: false,
            scales: {
              yAxes: [
                {
                  ticks: {
                    beginAtZero: true,
                  },
                },
              ],
            },
            legend: {
              labels: {
                fontSize: 25,
              },
            },
          }}
        />
      </div>
    </Layout>
  </Styles>
);

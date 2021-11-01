import React from "react";
import styled from "styled-components";

const Styles = styled.div`
  .text-center{
      position: absolute;
      left: 50%;
      top 45%;
      -webkit-transform: translate(-50%, -50%);
      transform: translate(-50%, -50%);
  }
  .loader {
    margin: auto;
    border: 16px solid #f3f3f3;
    border-radius: 50%;
    border-top: 16px solid #e87121;
    width: 120px;
    height: 120px;
    -webkit-animation: spin 2s linear infinite;
    animation: spin 2s linear infinite;
  }
  @-webkit-keyframes spin {
    0% {
      -webkit-transform: rotate(0deg);
    }
    100% {
      -webkit-transform: rotate(360deg);
    }
  }

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
`;

export const Loading = () => (
  <Styles>
    <React.Fragment>
      <div className="text-center">
        <h1>Cargando</h1>
        <div className="loader"></div>
      </div>
    </React.Fragment>
  </Styles>
);

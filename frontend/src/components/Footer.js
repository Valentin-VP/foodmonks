import React from "react";
import styled from "styled-components";

const Styles = styled.div`
  .footer {
    background-color: #e87121;
    font-family: "Poppins", sans-serif;
    position: absolute;
    bottom: 0;
    width: 100%;
    height: 3.5rem;
  }
`;

export const Footer = () => (
  <Styles>
    <React.Fragment>
      <footer className="footer py-3">
        <div className="container">
          <span className="text-muted">foodmonksoficial@gmail.com</span>
        </div>
      </footer>
    </React.Fragment>
  </Styles>
);

import React from "react";
import styled from "styled-components";
import { CgMail } from "react-icons/cg";

const Styles = styled.div`
  .footer {
    background-color: #e87121;
    font-family: "Poppins", sans-serif;
    position: absolute;
    bottom: 0;
    width: 100%;
    height: 3.5rem;
    color: #f2f2f2;
  }
`;

export const Footer = () => (
  <Styles>
    <React.Fragment>
      <footer className="footer py-3">
        <div className="container">
          <span>
            <CgMail color="white" fontSize="1.5rem"/>
            {" "}foodmonksoficial@gmail.com
          </span>
        </div>
      </footer>
    </React.Fragment>
  </Styles>
);

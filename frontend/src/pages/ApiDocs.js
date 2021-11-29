import React from "react";
import styled from "styled-components";
import "rapidoc";

const Styles = styled.div`
  rapi-doc {
    font-family: sans-serif;
    text-align: center;
  }
`;

export default function ApiDocs() {
  const url = process.env.REACT_APP_BACKEND_URL_BASE + "v3/apidocs";
  return (
    <Styles>
        <rapi-doc
        style={{ height: "100vh", width: "100%" }}
        spec-url={url}
        render-style="read"
        />
    </Styles>
  );
}
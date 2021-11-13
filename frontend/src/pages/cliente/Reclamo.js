import React, { useState } from "react";
import styled from "styled-components";
import { Form, FloatingLabel } from "react-bootstrap";
import { Layout } from "../../components/Layout";

const Styles = styled.div`

  * {
    margin: 0;
    box-sizing: border-box;
  }

  #page-container {
    background-image: url("https://images.pexels.com/photos/6419720/pexels-photo-6419720.jpeg?auto=compress&cs=tinysrgb&dpr=3&h=750&w=1260");
    filter: blur(6px);
    background-position: center;
    background-repeat: no-repeat;
    background-size: cover;
    margin-bottom: -3.5rem;
  }

  h4 {
    margin-bottom: 20px;
  }

  .form-mail {

    position: absolute;
    left: 50%;
    top 45%;
    -webkit-transform: translate(-50%, -50%);
    transform: translate(-50%, -50%);
    width: 400px;
    background: white;
    padding: 30px;
    margin: auto;
    border-radius: 5px;
    box-shadow: 7px 13px 37px #000;


    Button {
      width: 100%;
      color: white;
      background-color: #e87121;
      border: none;
      padding: 15px;
      margin-top: 15px;
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
  }

  .form-floating {
    margin-bottom: 13px;
  }

  #descripcion {
    size: 30;
  }

  #asunto {
    maxlength: 15;
  }
`;

function Reclamo() {
  const [mail, setMail] = useState({
    motivo: "",
    razon: "",
    descripcion: "",
  });
  const [componente, setComponente] = useState(null);

  let motivos = [
    { nombre: "N/A", valor: "" },
    { nombre: "", valor: "" },
    { nombre: "", valor: "" },
    { nombre: "", valor: "" },
  ];

  let razones = [
    { nombre: "N/A", valor: "" },
    { nombre: "", valor: "" },
    { nombre: "", valor: "" },
    { nombre: "", valor: "" },
  ];

  const handleChange = (e) => {
    e.persist();
    setMail((mail) => ({
      ...mail,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = () => {
    console.log("submit");
  };

  return (
    <Styles>
      <div id="page-container"></div>
      <section className="form-mail">
        <Layout>
          <h4> Reclamo </h4>
          <Form>
            <div>
              <input
                className="form-control"
                type="text"
                name="asunto"
                id="asunto"
                onChange={handleChange}
              />
              <label htmlFor="floatingInput"> Asunto </label>
            </div>
            <div>
              <input
                className="form-control"
                type="text"
                name="descripcion"
                id="descripcion"
                onChange={handleChange}
              />
              <label htmlFor="floatingInput"> Descripcion </label>
            </div>
            {componente}
            <Button id="submit" onClick={handleSubmit}>
              Enviar
            </Button>
          </Form>
        </Layout>
      </section>
    </Styles>
  );
}

export default Reclamo;

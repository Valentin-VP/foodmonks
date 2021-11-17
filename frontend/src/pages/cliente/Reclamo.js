import React, { useState } from "react";
import styled from "styled-components";
import { Form, Button, Alert } from "react-bootstrap";
import { Layout } from "../../components/Layout";
import { realizarReclamo } from "../../services/Requests";

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
    margin-bottom: 10px;
    width: 100%;
    height: 150px;
    border-radius: 5px;
    resize: none;
  }

  #asunto {
    border-radius: 5px;
    resize: none;
  }
`;

function Reclamo() {
  const [mail, setMail] = useState({
    pedidoId: sessionStorage.getItem("pedidoId"),
    razon: "",
    comentario: "",
  });
  const [success, setSuccess] = useState(null);

  const handleChange = (e) => {
    e.persist();
    setMail((mail) => ({
      ...mail,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    realizarReclamo(mail)
      .then((response) => {
        document.getElementById("submit").disabled = true;
        console.log("entro al then");
        setSuccess(
          <Alert variant="success">Promocion creada con exito!</Alert>
        );
        console.log(response);
        sessionStorage.removeItem("pedidoId");
        setTimeout(() => {
          window.location.replace("/listarPedidos"); //o como se llame el listar pedidos realizados de un cliente
        }, 3000);
      })
      .catch((error) => {
        console.log(error.response.data);
        setSuccess(
          <Alert variant="danger">{error.response.data.detailMessage}</Alert>
        );
      });
  };

  return (
    <Styles>
      <div id="page-container"></div>
      <section className="form-mail">
        <Layout>
          <h4> Reclamo </h4>
          <Form onSubmit={handleSubmit}>
            <div>
              <label htmlFor="floatingInput"> Asunto </label>
              <input
                className="form-control"
                type="text"
                name="asunto"
                id="asunto"
                onChange={handleChange}
                required
              />
            </div>
            <br />
            <div>
              <label htmlFor="floatingInput"> Descripcion </label>
              <textarea
                className="form-control"
                type="text"
                name="descripcion"
                id="descripcion"
                onChange={handleChange}
                required
              />
            </div>
            {success}
            <Button type="submit" id="submit">
              Enviar
            </Button>
          </Form>
        </Layout>
      </section>
    </Styles>
  );
}

export default Reclamo;

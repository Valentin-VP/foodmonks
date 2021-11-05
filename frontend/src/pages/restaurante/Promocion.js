import React, {useState, useEffect} from "react";
import styled from "styled-components";
import { Layout } from "../../components/Layout";
import { Button } from "react-bootstrap";
import { fetchPromos, eliminarMenu } from "../../services/Requests";

const Styles = styled.div`

  #titulo {
    padding-top: 20px;
    padding-bottom: 30px;
    text-decoration: none;
    font-size: 30px;
    font-family: "Poppins", sans-serif;
    color: #e87121;
    padding-right: 30px;
  }

  #alta {
    width: 50px;
    background-color: #009933;
    border: none;
    border-radius: 5px;
  }

  h1 {
    margin-top: 20px;
  }
  img {
    height: 6rem;
    border-radius: 5px;
  }
  .miBoton {
    color: white;
    background-color: #e87121;
    border: none;
    margin-bottom: 5px;
    margin-left: -10px;
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

  table {
  }

  table,
  tbody,
  tr,
  td {
    background-color: white;
  }
`;

function Promocion() {
    const [promos, setPromos] = useState();
    const [isLoading, setLoading] = useState(true);

    useEffect(() => {
        fetchPromos().then((response) => {
            console.log("paso por fetch");
            setPromos(response.data);
            setLoading(false);
        });
    }, []);

    const onEliminar = (id) => {
        eliminarMenu(id).then((response) => {
            console.log(response.data);
            window.location.reload();
        });
    };

    const onModificar = (id) => {
        sessionStorage.setItem("menuId", id);
    };

    if (isLoading) {
        return <div className="App">Cargando...</div>;
    }

    return(
        <Styles>
            <Layout>
                <h2 id="titulo">Mis Promos</h2>
                <div className="row justify-content-center">
                    <div className="col-12">
                        <table className="table table-light table-hover m-0">
                        <tbody>
                            {promos.map((promo, index) => {
                                return (
                                    <tr key={index}>
                                    <td>
                                        <img
                                        src={promo.imagen}
                                        alt="productimg"
                                        width="150"
                                        hight="150"
                                        />
                                    </td>
                                    <td>{promo.nombre}</td>
                                    <td>${(promo.price - (promo.price*(promo.multiplicadorPromocion/100)))}</td>
                                    {promo.multiplicadorPromocion !== 0 ? (
                                        <td>descuento: {promo.multiplicadorPromocion}%</td>
                                    ) : null}
                                    <td>Descripcion: {promo.descripcion}</td>
                                    <td>Categoria: {promo.categoria}</td>
                                    <td>
                                        <div className="row">
                                        <Button
                                            className="miBoton"
                                            onClick={() => {
                                                onEliminar(promo.id);
                                            }}
                                        >
                                            Eliminar
                                        </Button>
                                        </div>
                                        <div className="row">
                                        <Button
                                            className="miBoton"
                                            href="/modificarMenu"
                                            onClick={() => {
                                                onModificar(promo.id);
                                            }}
                                        >
                                            Modificar
                                        </Button>
                                        </div>
                                    </td>
                                    </tr>
                                );
                                })}
                        </tbody>
                        </table>
                    </div>
                </div>
            </Layout>
        </Styles>
    );
}

export default Promocion;
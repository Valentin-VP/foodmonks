import { React, useState, useEffect } from "react";
import { Layout } from "../../components/Layout";
import { Button, InputGroup } from "react-bootstrap";
import styled from "styled-components";
import { fetchUsuarios, actualizarEstadoUsuario } from "../../services/Requests";

const Styles = styled.div`
  
`;

function Usuario() {
    const [usuarios, setUsuarios] = useState();
    const [isLoading, setLoading] = useState(true);

    useEffect(() => {
        fetchUsuarios().then((response) => {
            console.log("paso por fetch");
            setUsuarios(response.data);
            setLoading(false);
        });
    }, []);

    const onEliminar = (correo) => {
        eliminarMenu(correo).then((response) => {
          console.log(response.data);
          window.location.reload();
        });
    };

    const onBloquear = (correo) => {
        actualizarEstadoUsuario("BLOQUEADO", correo).then((response) => {
            console.log("pasa por bloquear usuario");
            window.location.reload();
        });
    };

    if (isLoading) {
        return <div className="App">Cargando...</div>;
    }
    return(
        <Styles>
            <Layout>
            <div className="row justify-content-center">
            <div className="col-12">
            <table className="table table-light table-hover m-0">
              <tbody>
                {usuarios.map((usuario, index) => {
                  return (
                    <tr key={index}>
                      <td>
                        <img
                          src={usuario.imagen}
                          alt="productimg"
                          width="150"
                          hight="150"
                        />
                      </td>
                      {/* cada parte hay que preguntar por el rol del usuario, no se muestra lo mismo para todos los usuarios */}
                      <td>{usuario.nombre}</td>
                      <td>${usuario.apellido}</td>
                      <td>
                        <div className="row">
                          <Button
                            className="miBoton"
                            onClick={() => {
                              onEliminar(usuario.correo);
                            }}
                          >
                            Eliminar
                          </Button>
                        </div>
                        <div className="row">
                          <Button
                            className="miBoton"
                            onClick={() => {
                              onBloquear(usuario.correo);
                            }}
                          >
                            Bloquear
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
    )
}

export default Usuario;

import React, { useState, useEffect } from "react";
import styled from "styled-components";
import { Layout } from "../../components/Layout";
import RestauranteCard from "../../components/RestauranteCard";
import { fetchRestaurantesBusqueda } from "../../services/Requests";
import { Noti } from "../../components/Notification";
import { Loading } from "../../components/Loading";

const Styles = styled.div`
  h1 {
    text-align: center;
  }
  table {
    background-color: white;
  }
  .column {
    float: left;
    width: 300px;
    padding: 0 10px;
    margin-bottom: 5%;
  }
  @media screen and (max-width: 700px) {
    .column {
      width: 100%;
      display: block;
      margin-bottom: 20px;
    }
  }
  #scroll {
    background: "transparent";
    height: 6rem;
  }
`;

const perPage = 8;
const types = {
  start: "START",
  loaded: "LOADED",
};

const reducer = (state, action) => {
  switch (action.type) {
    case types.start:
      return { ...state, loading: true };
    case types.loaded:
      return {
        ...state,
        loading: false,
        data: [...state.data, ...action.newData],
        more: action.newData.length === perPage,
        after: state.after + action.newData.length,
      };
    default:
      throw new Error("Don't understand action");
  }
};

const MyContext = React.createContext();

function MyProvider({ children }) {
  const [datos, setDatos] = useState([]);
  const [cargando, isLoading] = useState(true);
  const values = {
    categoria: "",
    nombre: "",
    calificacion: false,
    idDireccion: "",
  };

  useEffect(() => {
    values.categoria = sessionStorage.getItem("restaurantes-categoria");
    values.nombre = sessionStorage.getItem("restaurantes-nombre");
    values.calificacion = sessionStorage.getItem("restaurantes-calificacion");
    values.idDireccion = sessionStorage.getItem("cliente-direccion");
    if (values.categoria === null) {
      values.categoria = "";
    }
    if (values.nombre === null) {
      values.nombre = "";
    }
    if (values.calificacion === null) {
      values.calificacion = false;
    }
    fetch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetch = () => {
    fetchRestaurantesBusqueda(values)
      .then((response) => {
        if (response.status === 200) {
          console.log(response);
          setDatos(response.data);
          isLoading(false);
        } else {
          Noti(response.data);
        }
      })
      .catch((error) => {
        Noti(error.response);
      });
  };

  const [state, dispatch] = React.useReducer(reducer, {
    loading: false,
    more: true,
    data: [],
    after: 0,
  });
  const { loading, data, after, more } = state;

  const load = () => {
    dispatch({ type: types.start });

    setTimeout(() => {
      const newData = datos.slice(after, after + perPage);
      dispatch({ type: types.loaded, newData });
    }, 300);
  };

  return (
    <MyContext.Provider value={{ cargando, loading, data, more, load }}>
      {children}
    </MyContext.Provider>
  );
}

function ListadoRestaurantesAbiertos() {
  const { data, loading, more, load, cargando } = React.useContext(MyContext);
  const loader = React.useRef(load);
  const observer = React.useRef(
    new IntersectionObserver(
      (entries) => {
        const first = entries[0];
        if (first.isIntersecting) {
          loader.current();
        }
      },
      { threshold: 1 }
    )
  );
  const [element, setElement] = React.useState(null);

  React.useEffect(() => {
    loader.current = load;
  }, [load]);

  React.useEffect(() => {
    const currentElement = element;
    const currentObserver = observer.current;

    if (currentElement) {
      currentObserver.observe(currentElement);
    }

    return () => {
      if (currentElement) {
        currentObserver.unobserve(currentElement);
      }
    };
  }, [element]);

  if (cargando) {
    return (
      <div className="App">
        <Loading />
      </div>
    );
  }
  return (
    <>
      <Styles>
        <Layout>
          <br />
          {console.log(sessionStorage.getItem("cliente-calle"))}
          <h3>
            resultados de la busqueda para la direccion:{" "}
            {sessionStorage.getItem("cliente-calle")}{" "}
            {sessionStorage.getItem("cliente-numero")}
          </h3>
          {sessionStorage.getItem("restaurantes-categoria") ? (
            <h3>
              resultados de la busqueda por categoria:
              {sessionStorage.getItem("restaurantes-categoria")}
            </h3>
          ) : null}
          {sessionStorage.getItem("restaurantes-nombre") ? (
            <h3>
              resultados de la busqueda por nombre:
              {sessionStorage.getItem("restaurantes-nombre")}
            </h3>
          ) : null}
          {sessionStorage.getItem("restaurantes-calificacion") ? (
            <h3>ordenado por calificacion</h3>
          ) : null}
          <br />
          <div className="table-responsive justify-content-center" id="list">
            <table className="table table-hover m-0">
              <tbody>
                {/* <tr>
                      <td>
                        <img
                          src="https://d1csarkz8obe9u.cloudfront.net/posterpreviews/restaurant-logo-design-template-b281aeadaa832c28badd72c1f6c5caad_screen.jpg?ts=1595421543"
                          alt="restimg"
                          width="150"
                          hight="150"
                        />
                      </td>
                      <td>Restaurante</td>
                      <td>Teléfono: 1234785967</td>
                      <td>Calificación: 5.0</td>
                    </tr> */}
                {data.map((item, index) => {
                  return (
                    <div className="column" key={index}>
                      <RestauranteCard
                        correo={item.correo}
                        imagen={item.imagen}
                        nombre={item.nombreRestaurante}
                        telefono={item.telefono}
                        calificacion={item.calificacion}
                        item={item}
                      />
                    </div>
                  );
                })}
              </tbody>
            </table>
            {loading && more && <Loading />}
            {!loading && more && <div ref={setElement} id="scroll"></div>}
          </div>
        </Layout>
      </Styles>
    </>
  );
}

// eslint-disable-next-line import/no-anonymous-default-export
export default () => {
  return (
    <MyProvider>
      <ListadoRestaurantesAbiertos />
    </MyProvider>
  );
};

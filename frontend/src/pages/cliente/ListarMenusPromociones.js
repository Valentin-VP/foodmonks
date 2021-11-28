import React, { useState, useEffect } from "react";
import styled from "styled-components";
import { Layout } from "../../components/Layout";
import ItemCard from "../../components/itemCard";
import { fetchMenusPromos } from "../../services/Requests";
import { Noti } from "../../components/Notification";
import { Loading } from "../../components/Loading";

const Styles = styled.div`
  .column {
    float: left;
    width: 18.75rem;
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

const ordenarProductos = (prods) => {
  var productos = [];
  productos = prods;
  productos.map((producto, index) => {
    if (producto.multiplicadorPromocion !== 0) {
      productos.splice(index, 1);
      productos.unshift(producto);
    }
  });
  return productos;
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
    precioInicial: "",
    precioFinal: "",
  };

  useEffect(() => {
    values.categoria = sessionStorage.getItem("values-categoria");
    values.precioInicial = sessionStorage.getItem("values-precioInicial");
    values.precioFinal = sessionStorage.getItem("values-precioFinal");
    if (values.categoria === null) {
      values.categoria = "";
    }
    if (values.precioInicial === null) {
      values.precioInicial = "";
    }
    if (values.precioFinal === null) {
      values.precioFinal = "";
    }
    fetch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetch = () => {
    fetchMenusPromos(values)
      .then((response) => {
        if (response.status === 200) {
          console.log(response.data);
          setDatos(ordenarProductos(response.data));
          isLoading(false);
        } else {
          Noti(response.data);
        }
      })
      .catch((error) => {
        Noti(error.response.data);
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

function ListadoMenusPromociones() {
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
    <Styles>
      <Layout>
        <h2> Productos </h2>
        <br />
        {sessionStorage.getItem("values-categoria") ? (
          <h3>
            {" "}
            resultados de la busqueda por categoria:{" "}
            {sessionStorage.getItem("values-categoria")}{" "}
          </h3>
        ) : (
          <h3> resultados de la busqueda</h3>
        )}
        {sessionStorage.getItem("values-precioInicial") ? (
          <h3>
            {" "}
            resultados de la busqueda por precio entre: $
            {sessionStorage.getItem("values-precioInicial")} y $
            {sessionStorage.getItem("values-precioFinal")}
          </h3>
        ) : null}
        <br />
        <div className="row justify-content-left">
          {data.map((item, index) => {
            return (
              <div className="column" key={index}>
                <ItemCard
                  img={item.imagen}
                  title={item.nombre}
                  desc={item.multiplicadorPromocion}
                  price={item.price}
                  item={item}
                />
              </div>
            );
          })}
          {loading && more && <Loading />}
          {!loading && more && <div ref={setElement} id="scroll"></div>}
        </div>
      </Layout>
    </Styles>
  );
}

// eslint-disable-next-line import/no-anonymous-default-export
export default () => {
  return (
    <MyProvider>
      <ListadoMenusPromociones />
    </MyProvider>
  );
};

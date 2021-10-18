import { React } from "react";
import { Layout } from "../../components/Layout";
import MenuCard from "./MenuCard";
import styled from "styled-components";
import imgPrueba from "../../assets/productos/hamburguesa.jpg";
import { fetchMenus } from "../../services/Requests";

const Styles = styled.div`
  #titulo {
    text-decoration: none;
    font-size: 30px;
    font-family: "Poppins", sans-serif;
    color: #e87121;
    padding-top: 20px;
    padding-bottom: 20px;
  }

  .column {
    padding-bottom: 35px;
  }
`;

//las llamadas de a axios van afuera de la funcion
let menus = [];
fetchMenus().then((response) => {
  menus = response.data;
});

function Menu() {
  return (
    <Styles>
      <Layout>
        {/* {console.log(menus)} */}
        <h2 id="titulo">Mis Menús</h2>
        <button className="btn-success">
          {" + "}
        </button>
        <div className="row justify-content-center">
          {menus.map((menu) => {
            return (
              <div className="column">
                <MenuCard
                  key={menu.id}
                  id={menu.id}
                  img={imgPrueba} //menu.imagen
                  nombre={menu.nombre}
                  descripcion={menu.descripcion}
                  price={menu.price}
                  multiplicador={menu.multiplicadorPromocion}
                  categoria={menu.categoria}
                />
              </div>
            );
          })}
        </div>
      </Layout>
    </Styles>
  );
}

export default Menu;

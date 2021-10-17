import React, {useState} from "react";
import { Layout } from "../../components/Layout";
import MenuCard from "./MenuCard";
import styled from "styled-components";
import imgPrueba from "../../assets/productos/hamburguesa.jpg";
import { fetchMenus } from "../../services/Requests";
// import ItemCard from "../../components/itemCard";

const Styles = styled.div`
  #titulo {
    text-decoration: none;
    font-size: 30px;
    font-family: "Poppins", sans-serif;
    color: #e87121;
    padding-top: 20px;
    padding-bottom: 20px;
  }
`;

const [menus, setMenus] = useState([]);

const onInit = () => (
  fetchMenus().then((response) => {
    console.log(response);
    setMenus(response);
  })
);

function Menu() { 
  onInit();
  <Styles>
    <React.Fragment>
      <Layout>
        <h2 id="titulo">Mis Menús</h2>
        <div className="row justify-content-center">
          <div className="column">
          {menus.map(menu =>(
            <MenuCard
            key={menu.id}
            id="1"//menu.id
            imagen={imgPrueba}//menu.imagen
            nombre="prueba"//menu.nombre
            descripcion="descripcion de prueba"//menu.descripcion
            price="50"//menu.precio
            multiplicador="5"//menu.multiplicadorPromocion
            categoria="comida"//menu.categoria
          />
          ))}
          </div>
        </div>
      </Layout>
    </React.Fragment>
  </Styles>
}

export default Menu();

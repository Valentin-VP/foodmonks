import { React } from "react";
import { Layout } from "../../components/Layout";
import MenuCard from "./MenuCard";
import styled from "styled-components";
import imgPrueba from "../../assets/productos/hamburguesa.jpg";
import { fetchMenus } from "../../services/Requests";
import { render } from "@testing-library/react";

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

function Menu() {
  let menus = []
  fetchMenus().then((response) => {
    console.log(response.data);
    menus = response.data;
    console.log(menus[0]);
  });

  return (
    <Styles>
      <Layout>
        {console.log(menus)}
        <h2 id="titulo">Mis Menús</h2>
        <div className="row justify-content-center">
          <div className="column">
            {menus.map((menu) => {
              console.log("sgsdgbdshdsn");
                <MenuCard
                  key={menu.id}
                  id={menu.id}
                  imagen={imgPrueba} //menu.imagen
                  nombre={menu.nombre}
                  descripcion={menu.descripcion}
                  price={menu.precio}
                  multiplicador={menu.multiplicadorPromocion}
                  categoria={menu.categoria}
                />
            })}
          </div>
        </div>
      </Layout>
    </Styles>
  );
}

export default Menu;

// const Example = () => {
//   // Puedes usar Hooks aquí!
//   const [menus, setMenus] = useState();
//   fetchMenus().then((response) => {
//     console.log(response);
//     setMenus(response);
//   });
// };

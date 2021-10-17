import React from "react";
import { getMenuInfo } from "../../services/Requests";

const [menu, setMenu] = useState([]);

const onInit = (menuId) => (
  getMenuInfo(menuId).then((response) => {
    console.log(response);
    setMenu(response);
  })
);

function ModificarMenu(props) {
    onInit(props);
    <div>
        <h2>Pagina en construccion</h2>
    </div>
}

export default ModificarMenu();
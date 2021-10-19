import { React, useState } from "react";
import { getMenuInfo } from "../../services/Requests";

// let menu;
// getMenuInfo(menuId).then((response) => {
//   menu = response.data;
// });

function ModificarMenu() {
  const [menuId, setMenuId] = useState();
  setMenuId(sessionStorage.getItem("menuId"));
  console.log(menuId);
  return (
    <div>
      <h2>Pagina en construccion</h2>
    </div>
  );
}

export default ModificarMenu;

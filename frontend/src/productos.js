import pizzaImg from "./assets/productos/pizza.jpg";
import hamburguesaImg from "./assets/productos/hamburguesa.jpg";
import donaImg from "./assets/productos/dona.jpg";
import pescadoImg from "./assets/productos/pescado.jpg";

const prods = {
  productData: [
    {
      id: 1,
      img: pizzaImg,
      title: "Pizza a la pala",
      desc: "Varios sabores a elección",
      price: 310,
    },
    {
      id: 2,
      img: hamburguesaImg,
      title: "Hamburguesa",
      desc: "Incluye",
      price: 240,
    },
    {
      id: 3,
      img: donaImg,
      title: "Dona",
      desc: "Chocolate o vainilla",
      price: 80,
    },
    {
      id: 4,
      img: pescadoImg,
      title: "Pescado",
      desc: "descripcion",
      price: 350,
    },
  ],
};

export default prods;

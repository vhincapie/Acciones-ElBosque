import React from "react";

const BotonAgregarTarjeta = ({ onClick }) => {
  return <button
  type="button"
  onClick={onClick}
  className="bg-black text-white text-center py-2 w-32 rounded-2xl cursor-pointer shadow hover:bg-indigo-500 transition-colors"
>
  Agregar cuenta
</button>;
};

export default BotonAgregarTarjeta;


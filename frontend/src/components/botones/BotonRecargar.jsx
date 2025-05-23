import React from "react";

const BotonRecargar = ({onClick}) => {
  return (
    <button
      type="button"
      onClick={onClick}
      className="bg-black text-white text-center py-2 w-32 rounded-2xl cursor-pointer shadow hover:bg-indigo-500 transition-colors"
    >
      Depositar
    </button>
  );
};

export default BotonRecargar;

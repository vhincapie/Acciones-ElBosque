import React from 'react';

const BotonEditar = ({ onClick }) => {
  return (
    <button
      type="button"
      onClick={onClick}
      className="bg-black text-white text-center py-2 w-32 rounded-2xl cursor-pointer shadow hover:bg-indigo-500 transition-colors"
    >
      Editar
    </button>
  );
};

export default BotonEditar;

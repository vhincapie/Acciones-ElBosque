import React from "react";
import { Link } from "react-router";

const LinkRegistro = () => {
  return (
    <div className="bg-black text-white px-6 py-2 w-32 rounded-2xl  hover:bg-indigo-500" >
      <Link to={"/signup"}>Registrarse</Link>
    </div>
  );
};

export default LinkRegistro;

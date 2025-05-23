import React from "react";
import { FaTimesCircle } from "react-icons/fa";
import { Link } from "react-router-dom";

const CancelPage = () => {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-900 px-6">
      <div className="bg-white shadow-xl rounded-3xl p-10 max-w-lg text-center border border-red-200">
        <FaTimesCircle className="text-red-500 text-6xl mb-4 mx-auto" />
        <h1 className="text-3xl font-bold text-red-600 mb-2">
          Suscripción cancelada
        </h1>
        <p className="text-gray-700 mb-6">
          Tu proceso de pago fue cancelado o no se completó correctamente.
        </p>
        <Link
          to={"/usuario/perfil"}
          className="inline-block bg-red-500 hover:bg-red-600 text-white font-semibold px-6 py-3 rounded-xl transition duration-200"
        >
          Volver a intentar
        </Link>
      </div>
    </div>
  );
};

export default CancelPage;

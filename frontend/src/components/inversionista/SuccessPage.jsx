import React, { useEffect, useState } from "react";
import axios from "axios";
import { FaCheckCircle, FaTimesCircle } from "react-icons/fa";
import { Link } from "react-router-dom";

const SuccessPage = () => {
  const [mensaje, setMensaje] = useState("Procesando tu suscripción...");
  const [error, setError] = useState(false);

  useEffect(() => {
    activarSuscripcion();
  }, []);

  const activarSuscripcion = async () => {
    try {
      const email = localStorage.getItem("email");
      const tipoPlan = localStorage.getItem("tipoPlan");

      if (!email || !tipoPlan) {
        throw new Error("Faltan datos de suscripción en localStorage.");
      }

      await axios.post(
        "http://localhost:8080/api/suscripcion/v1/webhook",
        {
          data: {
            object: {
              customer_email: email,
              tipo_plan: tipoPlan,
            },
          },
        },
        {
          withCredentials: true,
        }
      );

      setMensaje("¡Felicidades! Tu suscripción premium ha sido activada.");
      localStorage.removeItem("email");
      localStorage.removeItem("tipoPlan");
    } catch (error) {
      console.error("Error al activar la suscripción", error);
      setError(true);
      setMensaje(
        "Hubo un problema activando tu suscripción. Por favor, contáctanos."
      );
    }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-900 px-6">
      <div className="bg-white shadow-xl rounded-3xl p-10 max-w-lg text-center border border-gray-200">
        {error ? (
          <FaTimesCircle className="text-red-500 text-6xl mb-4 mx-auto" />
        ) : (
          <FaCheckCircle className="text-green-500 text-6xl mb-4 mx-auto animate-pulse" />
        )}
        <h1
          className={`text-2xl font-semibold mb-4 ${
            error ? "text-red-600" : "text-green-700"
          }`}
        >
          {mensaje}
        </h1>
        <Link
          to={"/usuario"}
          className="inline-block mt-6 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold px-6 py-3 rounded-xl transition duration-200"
        >
          Volver al inicio
        </Link>
      </div>
    </div>
  );
};

export default SuccessPage;

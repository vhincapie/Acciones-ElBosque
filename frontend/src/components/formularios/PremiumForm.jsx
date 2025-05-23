import React, { useEffect, useState } from "react";
import {
  obtenerEstadoSuscripcion,
  crearSesionCheckout,
  verificarPremium,
} from "../../hooks/premiumApi";

const PremiumForm = ({ userData }) => {
  const [suscripcion, setSuscripcion] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      const estado = await obtenerEstadoSuscripcion();
      if (estado) {
        const datos = await verificarPremium();
        setSuscripcion(datos);
      } else {
        setSuscripcion(null);
      }
    } catch (error) {
      console.error("Error al cargar datos de suscripción", error);
      setSuscripcion(null);
    } finally {
      setLoading(false);
    }
  };

  const handleSuscribirse = async (tipoPlan) => {
    try {
      localStorage.setItem("email", userData.email);
      localStorage.setItem("tipoPlan", tipoPlan);
      const url = await crearSesionCheckout(tipoPlan);
      window.location.href = url;
    } catch (error) {
      console.error("Error al iniciar sesión de pago", error);
    }
  };

  if (loading) {
    return (
      <div className="text-gray-500 text-sm">
        Cargando información de suscripción...
      </div>
    );
  }

  return (
    <div className="bg-white shadow-md rounded-xl p-8 max-w-3xl mx-auto space-y-6 border border-gray-200">
      <h2 className="text-3xl font-bold text-gray-900">Suscripción Premium</h2>

      {!suscripcion ? (
        <>
          <p className="text-gray-600 text-sm">
            Aún no tienes una suscripción activa. Elige un plan para acceder a
            beneficios exclusivos.
          </p>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mt-4">
            <div
              onClick={() => handleSuscribirse("mensual")}
              className="cursor-pointer border border-indigo-300 hover:border-indigo-600 hover:shadow-lg transition p-6 rounded-lg text-center bg-indigo-50 hover:bg-indigo-100"
            >
              <h3 className="text-lg font-semibold text-indigo-700 mb-2">
                Plan Mensual
              </h3>
              <p className="text-sm text-indigo-600">
                Suscripción mensual con renovación automática.
              </p>
            </div>

            <div
              onClick={() => handleSuscribirse("anual")}
              className="cursor-pointer border border-emerald-300 hover:border-emerald-600 hover:shadow-lg transition p-6 rounded-lg text-center bg-emerald-50 hover:bg-emerald-100"
            >
              <h3 className="text-lg font-semibold text-emerald-700 mb-2">
                Plan Anual
              </h3>
              <p className="text-sm text-emerald-600">
                Ahorra más con un solo pago anual.
              </p>
            </div>
          </div>
        </>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-gray-700 text-sm">
          <div>
            <span className="block text-gray-500 font-medium">
              Tipo de Plan
            </span>
            <p className="font-semibold text-gray-800">
              {suscripcion.tipoPlan || "No asignado"}
            </p>
          </div>
          <div>
            <span className="block text-gray-500 font-medium">Estado</span>
            <p
              className={`font-semibold ${
                suscripcion.activo ? "text-green-600" : "text-red-500"
              }`}
            >
              {suscripcion.activo ? "Activo" : "No activo"}
            </p>
          </div>
          <div className="sm:col-span-2 break-words">
            <span className="block text-gray-500 font-medium">Customer ID</span>
            <p className="font-mono text-xs text-gray-800">
              {suscripcion.stripeCustomerId || "No disponible"}
            </p>
          </div>
          <div className="sm:col-span-2 break-words">
            <span className="block text-gray-500 font-medium">
              Subscription ID
            </span>
            <p className="font-mono text-xs text-gray-800">
              {suscripcion.stripeSubscriptionId || "No disponible"}
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

export default PremiumForm;

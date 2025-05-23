import React, { useEffect, useState } from "react";
import BotonAgregarTarjeta from "../botones/BotonAgregarTarjeta";
import TarjetaForm from "../modales/TarjetaForm";
import axios from "axios";
import BotonRecargar from "../botones/BotonRecargar";
import RecargaForm from "../modales/RecargaForm";

const TarjetasAsociadas = ({ userData }) => {
  const [showModal, setShowModal] = useState(false);
  const [showRecargaModal, setShowRecargaModal] = useState(false);
  const [cuentas, setCuentas] = useState([]);

  const fetchUserProfile = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/api/alpaca/v1/ach_relationships",
        { withCredentials: true }
      );
      setCuentas(response.data);
    } catch (error) {
      console.error("Error al cargar el perfil del usuario:", error);
    }
  };
  useEffect(() => {
    fetchUserProfile();
  }, []);

  const eliminarCuenta = (index) => {
    const nuevasCuentas = [...cuentas];
    nuevasCuentas.splice(index, 1);
    setCuentas(nuevasCuentas);
  };

  if (!userData) {
    return <div className="text-gray-500">Cargando cuentas...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between flex-wrap gap-4">
        <h2 className="text-2xl font-semibold text-gray-800">
          Cuentas Asociadas
        </h2>
        {cuentas.length === 0 && (
          <BotonAgregarTarjeta onClick={() => setShowModal(true)} />
        )}
      </div>

      <div className="overflow-x-auto">
        <table className="min-w-full border rounded-xl shadow-sm bg-white hidden lg:table">
          <thead className="bg-gray-100">
            <tr>
              <th className="py-3 px-6 text-left text-sm font-semibold text-gray-700">
                Nombre de la Cuenta
              </th>
              <th className="py-3 px-6 text-left text-sm font-semibold text-gray-700">
                Banco
              </th>
              <th className="py-3 px-6 text-left text-sm font-semibold text-gray-700">
                Número de Cuenta
              </th>
              <th className="py-3 px-6 text-left text-sm font-semibold text-gray-700">
                Tipo de Cuenta
              </th>
              <th className="py-3 px-6 text-center text-sm  font-semibold text-gray-700">
                Acciones
              </th>
            </tr>
          </thead>
          <tbody>
            {cuentas.map((cuenta, index) => (
              <tr key={index} className="border-t hover:bg-gray-50">
                <td className="py-4 px-6 text-sm text-gray-700">
                  {cuenta.account_owner_name}
                </td>
                <td className="py-4 px-6 text-sm text-gray-700">
                  {cuenta.nickname}
                </td>
                <td className="py-4 px-6 text-sm text-gray-700">
                  ****{cuenta.bank_account_number?.slice(-4)}
                </td>
                <td className="py-4 px-6 text-sm text-gray-700">
                  {cuenta.bank_account_type === "SAVINGS"
                    ? "Ahorros"
                    : cuenta.bank_account_type === "CHECKING"
                    ? "Corriente"
                    : cuenta.bank_account_type}
                </td>
                <td className="py-4 px-6 ml-20">
                  <BotonRecargar onClick={() => setShowRecargaModal(true)} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="lg:hidden space-y-4">
        {cuentas.map((cuenta, index) => (
          <div key={index} className="border rounded-lg p-4 shadow-sm bg-white">
            <p className="text-sm text-gray-600">
              <span className="font-semibold">Nombre:</span>{" "}
              {cuenta.account_owner_name}
            </p>
            <p className="text-sm text-gray-600">
              <span className="font-semibold">Banco:</span> {cuenta.nickname}
            </p>
            <p className="text-sm text-gray-600">
              <span className="font-semibold">Número:</span> ****
              {cuenta.bank_account_number?.slice(-4)}
            </p>
            <p className="text-sm text-gray-600">
              <span className="font-semibold">Tipo:</span>{" "}
              {cuenta.bank_account_type === "SAVINGS"
                ? "Ahorros"
                : cuenta.bank_account_type === "CHECKING"
                ? "Corriente"
                : cuenta.bank_account_type}
            </p>
            <div className="mt-4 text-center">
              <BotonRecargar onClick={() => setShowRecargaModal(true)} />
            </div>
          </div>
        ))}
      </div>

      <TarjetaForm
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        onRegistroExitoso={() => {
          fetchUserProfile();
          setShowModal(false);
        }}
      />
      <RecargaForm
        isOpen={showRecargaModal}
        onClose={() => setShowRecargaModal(false)}
        onRecargaExitosa={() => {
          setShowRecargaModal(false);
        }}
      />
    </div>
  );
};

export default TarjetasAsociadas;

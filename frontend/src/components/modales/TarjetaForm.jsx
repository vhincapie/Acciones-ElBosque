import axios from "axios";
import React, { useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const TarjetaForm = ({ isOpen, onClose, onRegistroExitoso }) => {
  const [userData, setUserData] = useState({
    account_owner_name: "",
    bank_account_type: "",
    nickname: "",
    bank_account_number: "",
    bank_routing_number: "",
  });

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUserData({ ...userData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const camposObligatorios = [
      "account_owner_name",
      "bank_account_type",
      "nickname",
      "bank_account_number",
    ];

    for (const campo of camposObligatorios) {
      if (!userData[campo]) {
        toast.error("Por favor completa todos los campos requeridos");
        return;
      }
    }

    try {
      const tarjetaTransacciones = {
        ...userData,
        bank_routing_number: "123103716",
      };
      await axios.post(
        "http://localhost:8080/api/alpaca/v1/ach_relationships",
        tarjetaTransacciones,
        { withCredentials: true }
      );
      toast.success("Cuenta bancaria registrada con éxito");
      onRegistroExitoso();
    } catch (error) {
      console.error("Error al crear cuenta bancaria", error);
      //toast.error("Ocurrió un error al registrar la cuenta bancaria");
    }
  };

  return (
    <div
      className="fixed inset-0 flex items-center justify-center z-50"
      style={{ backgroundColor: "rgba(0, 0, 0, 0.3)" }}
    >
      <ToastContainer position="top-center" />
      <div className="bg-white rounded-xl p-8 w-full max-w-md shadow-2xl relative">
        <button
          onClick={onClose}
          className="absolute top-3 right-3 text-gray-500 hover:text-red-600 text-xl font-bold"
        >
          ×
        </button>

        <form onSubmit={handleSubmit} className="space-y-6">
          <h2 className="text-2xl font-semibold text-gray-800">
            Agregar cuenta bancaria
          </h2>

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Nombre de la Cuenta
              </label>
              <input
                type="text"
                name="account_owner_name"
                value={userData.account_owner_name}
                onChange={handleChange}
                className="w-full border text-black border-gray-300 rounded-md px-4 py-2"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Número de Cuenta
              </label>
              <input
                type="text"
                name="bank_account_number"
                value={userData.bank_account_number}
                onChange={handleChange}
                className="w-full border text-black border-gray-300 rounded-md px-4 py-2"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Tipo de cuenta
              </label>
              <select
                name="bank_account_type"
                value={userData.bank_account_type}
                onChange={handleChange}
                className="text-black w-full border border-gray-300 rounded-md px-4 py-2"
                required
              >
                <option value="">Selecciona tipo de cuenta</option>
                <option value="SAVINGS">Ahorros</option>
                <option value="CHECKING">Corriente</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Banco
              </label>
              <select
                name="nickname"
                value={userData.nickname}
                onChange={handleChange}
                className="text-black w-full border border-gray-300 rounded-md px-4 py-2"
                required
              >
                <option value="">Selecciona un banco</option>
                <option value="Bancolombia">Bancolombia</option>
                <option value="Banco de Bogota">Banco de Bogotá</option>
                <option value="Banco Caja Social">Banco Caja Social</option>
                <option value="Banco BBVA">Banco BBVA</option>
                <option value="Davivienda">Davivienda</option>
              </select>
            </div>
          </div>

          <button
            type="submit"
            className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 rounded-md transition duration-200"
          >
            Guardar
          </button>
        </form>
      </div>
    </div>
  );
};

export default TarjetaForm;

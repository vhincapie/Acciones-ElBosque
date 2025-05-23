import axios from "axios";
import React, { useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const RecargaForm = ({ isOpen, onClose, onRecargaExitosa, children }) => {
  const [userData, setUserData] = useState({
    transfer_type: "",
    amount: null,
    direction: "",
    timing: "",
  });

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUserData({ ...userData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const camposObligatorios = ["amount"];
    for (const campo of camposObligatorios) {
      if (!userData[campo]) {
        toast.error("Por favor ingrese un valor positivo mayor a cero");
        return;
      }
    }

    if(userData.amount>50000){
      toast.error("No se puede depositar mas de U$50000")
      return
    }

    try {
      const depositoCuenta = {
        ...userData,
        transfer_type: "ach",
        direction: "INCOMING",
        timing: "immediate",
      };
      await axios.post(
        "http://localhost:8080/api/alpaca/v1/transfers",
        depositoCuenta,
        { withCredentials: true }
      );
      toast.success("Depósito con éxito. Debe esperar 10 minutos para que se vea reflejada");
      if (onRecargaExitosa) onRecargaExitosa();
    } catch (error) {
      console.error("Error al depositar en cuenta", error);
      if (error.response && error.response.status === 500) {
        const mensaje = error.response.data?.message || "";
        const match = mensaje.match(/el dia (\d{4}-\d{2}-\d{2})/);
        const dia = match ? match[1] : "hoy";
        toast.error(`Ya se realizó un depósito el día ${dia}, debe esperar 24 horas para hacer un nuevo depósito`);
      } else {
        toast.error("Ocurrió un error inesperado al realizar el depósito.");
      }
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
            Depositar monto en la cuenta
          </h2>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              ¿Cuánto va a depositar?
            </label>
            <input
              type="number"
              name="amount"
              value={userData.amount || ""}
              onChange={handleChange}
              className="w-full border text-black border-gray-300 rounded-md px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              min="1"
              max="50000"
              required
            />
          </div>

          <button
            type="submit"
            className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 rounded-md transition duration-200"
          >
            Depositar
          </button>
        </form>
      </div>
    </div>
  );
};

export default RecargaForm;

import React, { useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import BotonRecuperar from "../botones/BotonRecuperar";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const ResetForm = () => {
  const [usuario, setUsuario] = useState({ email: "" });
  const navigate = useNavigate()

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUsuario({ ...usuario, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post(
        `http://localhost:8080/api/auth/v1/recover?email=${usuario.email}`
      );
      toast.success('Correo de recuperación enviado. Revisa tu bandeja de entrada.');
      setTimeout(() => {
        navigate("/")
      }, 2500);
    } catch (error) {
      if (error.response?.status === 400) {
        toast.error("Correo no registrado.");
      } else if (error.response?.status === 403) {
        toast.error("Acceso bloqueado por política de seguridad (CORS).");
      } else {
        toast.error("Error del servidor. Intenta más tarde.");
      }
    }
  };  

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900">
      <ToastContainer position="top-center" />
      <div className="bg-white p-10 rounded-xl shadow-xl w-full max-w-md">
        <h2 className="text-3xl font-bold text-center text-gray-800 mb-8">
          Recuperación de contraseña
        </h2>
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Correo electrónico
            </label>
            <input
              type="text"
              name="email"
              value={usuario.email}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-md px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              placeholder="Ingresa tu correo electrónico"
              required
            />
          </div>
          <div className="pt-4">
            <BotonRecuperar />
          </div>
        </form>
      </div>
    </div>
  );
};

export default ResetForm;

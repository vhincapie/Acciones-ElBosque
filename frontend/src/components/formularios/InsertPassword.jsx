import axios from 'axios';
import React, { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { ToastContainer, toast } from 'react-toastify';
import BotonResetPass from '../botones/BotonResetPass';

const InsertPassword = () => {
  const [usuario, setUsuario] = useState({ password: '' });
  const [searchParams] = useSearchParams();
  const navigate = useNavigate()
  const token = searchParams.get('token');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUsuario({ ...usuario, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post(
        `http://localhost:8080/api/auth/v1/reset-password?token=${token}&newPassword=${usuario.password}`
      );
      toast.success('Contraseña actualizada correctamente');
      setUsuario({ password: '' });
      setTimeout(()=>{
        navigate('/login')
      }, 2000)
    } catch (error) {
      console.error('Error al recuperar contraseña', error);
      toast.error('Hubo un error al actualizar la contraseña');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900">
      <ToastContainer position="top-center" />
      <div className="bg-white p-10 rounded-xl shadow-xl w-full max-w-md">
        <h2 className="text-3xl font-bold text-center text-gray-800 mb-8">
          Ingresa nueva contraseña
        </h2>
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Nueva Contraseña
            </label>
            <input
              type="password"
              name="password"
              value={usuario.password}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-md px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              placeholder="Ingresa tu nueva contraseña"
              required
            />
          </div>
          <div className="pt-4">
            <BotonResetPass />
          </div>
        </form>
      </div>
    </div>
  );
};

export default InsertPassword;

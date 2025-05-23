import React, { useEffect, useState } from "react";
import BotonInicioSesion from "../botones/BotonInicioSesion";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import axios from "axios";
import { login } from "../../hooks/authSlice";
import { fetchStocks } from "../../hooks/stocks";
import { setStocks, clearStocks } from "../../hooks/marketSlice";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const LoginForm = () => {
  const params = new URLSearchParams(window.location.search);
  const pagoExitoso = params.get("success");
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [usuario, setUsuario] = useState({ username: "", password: "" });
  const [intentos, setIntentos] = useState(
    parseInt(sessionStorage.getItem("intentosFallidos")) || 0
  );

  useEffect(() => {
    if (pagoExitoso === "true") {
      activarPremium();
    }
  }, []);

  const activarPremium = async () => {
    try {
      const email = sessionStorage.getItem("email");
      if (!email) {
        toast.error("No se encontró el usuario en sesión.");
        return;
      }

      await axios.post("http://localhost:8080/api/stripe/webhook", {
        email: email,
      });

      const cuentasGuardadas =
        JSON.parse(localStorage.getItem("cuentas")) || [];
      const cuentasActualizadas = cuentasGuardadas.map((cuenta) => {
        if (cuenta.email_address === email) {
          const randomCust = Math.floor(100 + Math.random() * 900);
          const randomSub = Math.floor(100 + Math.random() * 900);
          return {
            ...cuenta,
            activo: true,
            tipo_plan: sessionStorage.getItem("tipo_plan") || "Mensual",
            customer: `cust_test${randomCust}`,
            subscription: `sub_test${randomSub}`,
          };
        }
        return cuenta;
      });

      localStorage.setItem("cuentas", JSON.stringify(cuentasActualizadas));
      toast.success("¡Suscripción premium activada!");
    } catch (error) {
      console.error("Error activando cuenta premium:", error);
      toast.error("Error activando premium.");
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUsuario({ ...usuario, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(
        "http://localhost:8080/api/auth/v1/login",
        usuario,
        { withCredentials: true }
      );

      sessionStorage.setItem("username", response.data.username);
      sessionStorage.setItem("nombre", response.data.nombre);
      dispatch(login({ username: response.data.username }));

      dispatch(clearStocks());
      if (response.data.username) {
        navigate(`/usuario`);
      }

      const nuevosStocks = await fetchStocks();
      dispatch(setStocks(nuevosStocks));
      sessionStorage.removeItem("intentosFallidos");
    } catch (error) {
      console.error("Error al iniciar sesión", error);
      const nuevosIntentos = intentos + 1;
      setIntentos(nuevosIntentos);
      sessionStorage.setItem("intentosFallidos", nuevosIntentos);
      toast.error("Usuario o contraseña incorrectos");

      if (nuevosIntentos >= 3) {
        toast.warn("Se debe restablecer la contraseña");
        setTimeout(() => {
          navigate("/recuperar-pass");
        }, 2000);
      }      
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900">
      <ToastContainer position="top-center" />
      <div className="bg-white mx-6 p-10 rounded-xl shadow-xl w-full max-w-md">
        <h2 className="text-3xl font-bold text-center text-gray-800 mb-8">
          Iniciar Sesión
        </h2>
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Usuario
            </label>
            <input
              type="text"
              name="username"
              value={usuario.username}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-md px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              placeholder="Ingresa tu nombre de usuario"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Contraseña
            </label>
            <input
              type="password"
              name="password"
              value={usuario.password}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-md px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              placeholder="••••••••"
              required
            />
          </div>

          <div className="text-left text-sm">
            <Link
              to={"/recuperar-pass"}
              className="text-indigo-600 hover:underline"
            >
              ¿Olvidaste tu contraseña?
            </Link>
          </div>

          <div className="pt-4 justify-items-end">
            <BotonInicioSesion />
          </div>
          <p className="text-sm text-center mt-4">
            ¿No tienes cuenta?{" "}
            <Link to={"/signup"} className="text-blue-600 hover:underline">
              Registrate
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
};

export default LoginForm;

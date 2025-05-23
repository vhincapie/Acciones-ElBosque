import React, { useEffect, useState } from "react";
import { useDispatch } from "react-redux";
import { login } from "../../hooks/authSlice";
import PanelNavegacion from "./PanelNavegacion";
import axios from "axios";

const Principal = () => {
  const [nombre, setNombre] = useState("");
  const [username, setUsername] = useState("");
  const [userData, setUserData] = useState(null);
  const dispatch = useDispatch();

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await axios.get("http://localhost:8080/api/auth/v1/panel", {
          withCredentials: true,
        });
        setUserData(res.data);
        dispatch(login({ nombre: res.data.nombre, username: res.data.username, activo: res.data.activo }));
      } catch (error) {
        console.error("❌ Error al cargar datos del usuario:", error);
      }
    };
    const nombreGuardado = sessionStorage.getItem("nombre");
    const usernameGuardado = sessionStorage.getItem("username");

    if (nombreGuardado && usernameGuardado) {
      setNombre(nombreGuardado);
      setUsername(usernameGuardado);
      dispatch(
        login({
          nombre: nombreGuardado,
          username: usernameGuardado,
        })
      );
    } fetchUser();
  }, [dispatch]);

  return (
    <div className="min-h-screen bg-gray-900 px-8 py-10 text-white">
      <h1 className="text-4xl font-bold mb-6">Bienvenido, {nombre}</h1>
      <div className="bg-white shadow-lg rounded-xl p-6 text-gray-800">
        <PanelNavegacion userData={userData} />
      </div>
    </div>
  );
};

export default Principal;

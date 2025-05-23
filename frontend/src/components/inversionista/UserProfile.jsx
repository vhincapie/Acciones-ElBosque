import React, { useEffect, useState } from "react";
import axios from "axios";
import PanelPerfil from "./PanelPerfil";

const UserProfile = () => {
  const [userData, setUserData] = useState(null);

  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/auth/v1/panel",
          { withCredentials: true }
        );
        setUserData(response.data);
      } catch (error) {
        console.error("Error al cargar el perfil del usuario:", error);
      }
    };

    fetchUserProfile();
  }, []);

  if (!userData) {
    return (
      <div className="flex justify-center items-center h-[80vh] text-gray-500">
        Cargando perfil...
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-900 px-8 py-10 text-white">
        <h1 className="text-3xl font-bold mb-6">
          Bienvenido, {userData.nombre}
        </h1>
        <PanelPerfil userData={userData} />
      </div>
  );
};

export default UserProfile;

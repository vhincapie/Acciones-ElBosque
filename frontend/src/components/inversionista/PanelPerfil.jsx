import { useState } from "react";
import DatosUsuario from "./DatosUsuario";
import PremiumForm from "../formularios/PremiumForm";
import TarjetasAsociadas from "./TarjetasAsociadas";

const tabs = ["Datos de usuario", "Mis cuentas", "Configuración"];

export default function PanelPerfil({ userData }) {
  const [activeTab, setActiveTab] = useState("Datos de usuario");

  const renderPanel = () => {
    switch (activeTab) {
      case "Datos de usuario":
        return <DatosUsuario userData={userData} />;
      case "Mis cuentas":
        return <TarjetasAsociadas userData={userData} />;
      case "Configuración":
        return <PremiumForm userData={userData} />;
      default:
        return null;
    }
  };

  return (
    <div className="bg-white min-h-[500px] rounded-xl shadow-md p-6">
      <div className="flex border-b border-gray-200 mb-6 space-x-6">
        {tabs.map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`text-sm font-medium pb-2 border-b-2 transition-all duration-200 ${
              tab === activeTab
                ? "border-blue-600 text-blue-600"
                : "border-transparent text-gray-500 hover:text-blue-500"
            }`}
          >
            {tab}
          </button>
        ))}
      </div>
      <div>{renderPanel()}</div>
    </div>
  );
}

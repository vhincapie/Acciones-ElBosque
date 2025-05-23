import { useState } from "react";
import DatosMercado from "./DatosMercado";
import Watchlist from "./Watchlist";
import DashboardWatchlist from "./DashboardWatchlist";
import Ordenes from "./Ordenes";
import Portafolio from "./Portafolio";

export default function PanelNavegacion({ userData }) {
  const tabs = [
    "Dashboard",
    "Ordenes",
    "Portafolio",
    ...(userData?.activo ? ["Watchlist", "Watchlist Dashboard"] : [])
  ];
  const [activeTab, setActiveTab] = useState("Dashboard");

  const renderPanel = () => {
    switch (activeTab) {
      case "Dashboard":
        return (
          <div className="p-6 animate-fade-in">
            <DatosMercado />
          </div>
        );
      case "Ordenes":
        return (
          <div className="p-6 animate-fade-in">
            <Ordenes />
          </div>
        );
      case "Portafolio":
        return (
          <div className="p-6 animate-fade-in">
            <Portafolio />
          </div>
        );
      case "Watchlist":
        return (
          <div className="p-6 animate-fade-in">
            <Watchlist />
          </div>
        );
      case "Watchlist Dashboard":
        return (
          <div className="p-6 animate-fade-in">
            <DashboardWatchlist />
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="bg-gradient-to-b from-gray-100 to-gray-200 min-h-screen font-sans">
      <header className="shadow-md bg-white">
        <nav className="flex items-center justify-between px-6 py-4">
          <div className="flex space-x-1">
            {tabs.map((tab) => (
              <div
                key={tab}
                onClick={() => setActiveTab(tab)}
                className={`cursor-pointer px-4 py-2 font-medium text-sm skew-x-[-20deg] transform transition-all duration-200
                  ${
                    tab === activeTab
                      ? "bg-blue-700 text-white shadow-md"
                      : "bg-gray-300 hover:bg-blue-400 text-gray-800"
                  }`}
              >
                <span className="block skew-x-[20deg]">{tab}</span>
              </div>
            ))}
          </div>
        </nav>
      </header>

      <main className="mx-6 my-4 bg-white rounded-lg shadow-md min-h-[100px] transition-all duration-300">
        {renderPanel()}
      </main>
    </div>
  );
}

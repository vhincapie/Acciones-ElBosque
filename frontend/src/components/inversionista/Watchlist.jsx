import React, { useState, useEffect } from "react";
import { watchlistApi } from "../../hooks/watchlistApi";

const WatchlistSearch = () => {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [loadingWatchlist, setLoadingWatchlist] = useState(true);
  const [watchlist, setWatchlist] = useState([]);

  useEffect(() => {
    cargarWatchlist();
  }, []);

  const cargarWatchlist = async () => {
    try {
      setLoadingWatchlist(true);
      const res = await watchlistApi.getAll();
      setWatchlist(res.data);
    } catch (err) {
      alert("Error al cargar tu watchlist.");
    } finally {
      setLoadingWatchlist(false);
    }
  };

  const buscar = async () => {
    if (!query.trim()) return;

    try {
      setLoading(true);
      const res = await watchlistApi.searchStocks(query);
      setResults(res.data);
    } catch (err) {
      alert("Error al buscar acciones.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const agregarAlWatchlist = async (stock) => {
    const minInput = prompt("Precio mínimo de alerta (puede dejarse vacío):");
    const maxInput = prompt("Precio máximo de alerta (puede dejarse vacío):");

    const min = minInput.trim() === "" ? null : parseFloat(minInput);
    const max = maxInput.trim() === "" ? null : parseFloat(maxInput);

    if (min !== null && (isNaN(min) || min <= 0)) {
      alert("⚠️ El precio mínimo debe ser un número mayor a cero.");
      return;
    }

    if (max !== null && (isNaN(max) || max <= 0)) {
      alert("⚠️ El precio máximo debe ser un número mayor a cero.");
      return;
    }

    try {
      await watchlistApi.create({
        symbol: stock.symbol,
        companyName: stock.description,
        alertMinPrice: min,
        alertMaxPrice: max,
      });
      alert("Agregado a tu watchlist con alertas.");
      cargarWatchlist();
    } catch (err) {
      const mensaje = err?.response?.data?.mensaje;
      if (mensaje === "Este símbolo ya está registrado en tu watchlist.") {
        alert("⚠️ Esta acción ya fue agregada a tu watchlist.");
      } else {
        alert(mensaje || "No se pudo agregar al watchlist.");
      }
    }
  };

  const actualizarAlerta = async (id) => {
    const minInput = prompt("Nuevo precio mínimo:");
    const maxInput = prompt("Nuevo precio máximo:");

    const min = minInput.trim() === "" ? null : parseFloat(minInput);
    const max = maxInput.trim() === "" ? null : parseFloat(maxInput);

    if (min !== null && (isNaN(min) || min <= 0)) {
      alert("⚠️ El precio mínimo debe ser un número mayor a cero.");
      return;
    }

    if (max !== null && (isNaN(max) || max <= 0)) {
      alert("⚠️ El precio máximo debe ser un número mayor a cero.");
      return;
    }

    try {
      await watchlistApi.updateAlerts(id, min, max);
      alert("Alerta actualizada.");
      cargarWatchlist();
    } catch (err) {
      alert("Error al actualizar alerta.");
    }
  };

  const eliminar = async (id) => {
    try {
      await watchlistApi.delete(id);
      alert("Eliminado del watchlist.");
      cargarWatchlist();
    } catch (err) {
      alert("Error al eliminar.");
    }
  };

  return (
    <div className="flex flex-col lg:flex-row gap-10 px-2 py-10 bg-gray-50 min-h-screen">
      <div className="w-full lg:w-1/2 bg-white rounded-2xl shadow-lg p-6">
        
          <h2 className="text-2xl font-bold text-gray-800 mb-4">Buscar acciones</h2>

          <div className="flex gap-3 mb-5">
            <input
              type="text"
              placeholder="Buscar por nombre o símbolo (ej: Tesla)"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="flex-1 border border-gray-300 p-3 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
            <button onClick={buscar} className="bg-blue-700 hover:bg-blue-800 text-white px-6 py-2 rounded-md font-semibold transition">
              Buscar
            </button>
          </div>

          {loading && (
            <div className="flex items-center gap-3 text-gray-500 mb-3">
              <svg className="animate-spin h-5 w-5 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"></path>
              </svg>
              <p>Buscando acciones...</p>
            </div>
          )}

          {results.length > 0 && (
            <ul className="space-y-4">
              {results.map((stock) => {
                const yaAgregada = watchlist.some((item) => item.symbol === stock.symbol);
                return (
                  <li key={stock.symbol} className="border border-gray-200 rounded-xl p-4 bg-gray-50 flex justify-between items-center hover:shadow-sm transition">
                    <div>
                      <p className="text-lg font-semibold text-gray-800">{stock.symbol}</p>
                      <p className="text-sm text-gray-600">{stock.description}</p>
                      <p className="text-sm text-gray-500">Precio actual: ${stock.currentPrice.toFixed(2)}</p>
                    </div>
                    <button
                      onClick={() => agregarAlWatchlist(stock)}
                      disabled={yaAgregada}
                      className={`px-4 py-2 rounded-md font-medium text-white transition ${
                        yaAgregada ? "bg-gray-400 cursor-not-allowed" : "bg-emerald-600 hover:bg-emerald-700"
                      }`}
                    >
                      {yaAgregada ? "Agregada" : "Agregar"}
                    </button>
                  </li>
                );
              })}
            </ul>
          )}
        </div>
        <div className="w-full lg:w-1/2 bg-white rounded-2xl shadow-lg p-6">
          <h2 className="text-2xl font-bold text-gray-800 mb-4">Tu Watchlist</h2>

          {loadingWatchlist ? (
            <div className="flex items-center gap-3 text-gray-500">
              <svg className="animate-spin h-5 w-5 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"></path>
              </svg>
              <p>Cargando tu watchlist...</p>
            </div>
          ) : watchlist.length === 0 ? (
            <p className="text-gray-500">Aún no tienes acciones en tu lista.</p>
          ) : (
            <ul className="space-y-4">
              {watchlist.map((item) => (
                <li key={item.id} className="border border-gray-200 p-4 rounded-xl bg-gray-50 flex justify-between items-center hover:shadow-sm transition">
                  <div>
                    <p className="text-lg font-semibold text-gray-800">{item.symbol}</p>
                    <p className="text-sm text-gray-600">{item.companyName}</p>
                    <p className="text-sm text-gray-500">
                      Alerta: ${item.alertMinPrice ?? "-"} - ${item.alertMaxPrice ?? "-"}
                    </p>
                  </div>
                  <div className="flex gap-2">
                    <button onClick={() => actualizarAlerta(item.id)} className="bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded-md font-medium">
                      Editar
                    </button>
                    <button onClick={() => eliminar(item.id)} className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md font-medium">
                      Eliminar
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    
  );
};

export default WatchlistSearch;

import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  LineChart,
  Line,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import {
  fetchWatchlist,
  fetchMarketStatusThunk,
} from "../../hooks/watchlistSlice";

export default function DashboardWatchlist() {
  const dispatch = useDispatch();
  const username = useSelector((state) => state.auth.username);
  const { watchlist, marketStatus, loading, error } = useSelector(
    (state) => state.watchlist
  );

  const [selectedSymbol, setSelectedSymbol] = useState(null);
  const [stockHistory, setStockHistory] = useState({});
  const [selectedHistory, setSelectedHistory] = useState([]);
  const [refreshKey, setRefreshKey] = useState(0);

  const TWO_MINUTES = 2 * 60 * 1000;
  const STORAGE_KEY = `stockHistoryWatchlist_${username}`;
  const STORAGE_DATE_KEY = `watchlistHistoryDate_${username}`;

  const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    return `${date.getHours().toString().padStart(2, "0")}:${date
      .getMinutes()
      .toString()
      .padStart(2, "0")}`;
  };


  useEffect(() => {
    if (username) {
      dispatch(fetchWatchlist());
      dispatch(fetchMarketStatusThunk());
    }
  }, [dispatch, username]);

  useEffect(() => {
    setStockHistory({});
    setSelectedHistory([]);
    setSelectedSymbol(null);
  }, [username]);


  useEffect(() => {
    if (!username || watchlist.length === 0) return;

    const today = new Date().toISOString().split("T")[0];
    const lastSavedDate = localStorage.getItem(STORAGE_DATE_KEY);

    if (lastSavedDate !== today) {
      localStorage.removeItem(STORAGE_KEY);
      localStorage.setItem(STORAGE_DATE_KEY, today);
      setStockHistory({});
    } else {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) {
        setStockHistory(JSON.parse(saved));
      }
    }
  }, [username, watchlist]);

  
  useEffect(() => {
    const interval = setInterval(() => {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) {
        setStockHistory(JSON.parse(saved));
        setRefreshKey((r) => r + 1);
      }
    }, TWO_MINUTES);
    return () => clearInterval(interval);
  }, [username]);


  useEffect(() => {
    if (watchlist.length > 0) {
      setSelectedSymbol(watchlist[0].symbol);
    } else {
      setSelectedSymbol(null);
    }
  }, [watchlist, username]);

  
  useEffect(() => {
    if (!selectedSymbol) return;

    const historial = stockHistory[selectedSymbol] || [];
    const stockActual = watchlist.find((s) => s.symbol === selectedSymbol);

    if (stockActual) {
      const now = Date.now();
      const last = historial.at(-1);
      const esReciente = last && now - last.timestamp < 30 * 1000;
      const historyFinal = esReciente
        ? historial
        : [...historial, { price: stockActual.currentPrice, timestamp: now }];
      setSelectedHistory(historyFinal);
    } else {
      setSelectedHistory([...historial]);
    }
  }, [refreshKey, stockHistory, selectedSymbol, watchlist]);

  const isEmpty = watchlist.length === 0;
  const isReady = selectedHistory.length > 0;

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold text-gray-800">
          Seguimiento de tu Watchlist
        </h1>
        <div
          className={`flex items-center gap-2 px-4 py-1 rounded-full text-white font-semibold text-sm shadow-md ${
            marketStatus?.abierto ? "bg-green-600" : "bg-red-600"
          }`}
        >
          <div className="w-2.5 h-2.5 rounded-full bg-white"></div>
          {marketStatus?.abierto ? "Mercado Abierto" : "Mercado Cerrado"}
        </div>
      </div>

      {error ? (
        <div className="text-center text-red-600">
          <p>{error}</p>
          <button
            onClick={() => dispatch(fetchWatchlist())}
            className="mt-2 px-4 py-1 bg-blue-600 text-white rounded"
          >
            Reintentar
          </button>
        </div>
      ) : loading ? (
        <div className="flex flex-col justify-center items-center h-40">
          <div className="animate-spin rounded-full h-12 w-12 border-t-4 border-b-4 border-blue-500 mb-4"></div>
          <p className="text-gray-600 font-medium">Cargando acciones...</p>
        </div>
      ) : isEmpty ? (
        <p className="text-gray-500 text-center">
          Aún no has agregado ninguna acción a tu Watchlist. <br />
          Usa la búsqueda para comenzar 📈
        </p>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {watchlist.map((stock) => {
              const historial = stockHistory[stock.symbol] || [];
              const penultimo = historial.at(-2);
              const cambio = penultimo
                ? stock.currentPrice > penultimo.price
                  ? "up"
                  : stock.currentPrice < penultimo.price
                  ? "down"
                  : "same"
                : stock.currentPrice > stock.previousClosePrice
                ? "up"
                : stock.currentPrice < stock.previousClosePrice
                ? "down"
                : "same";

              const fueraDeRangoMin =
                stock.alertMinPrice !== null &&
                stock.currentPrice < stock.alertMinPrice;
              const fueraDeRangoMax =
                stock.alertMaxPrice !== null &&
                stock.currentPrice > stock.alertMaxPrice;

              return (
                <div
                  key={stock.symbol}
                  onClick={() => setSelectedSymbol(stock.symbol)}
                  className={`cursor-pointer p-5 rounded-xl border hover:shadow-lg transition transform hover:-translate-y-1 bg-white ${
                    selectedSymbol === stock.symbol
                      ? "ring-2 ring-blue-400"
                      : ""
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <h2 className="text-xl font-bold text-gray-800">
                      {stock.symbol}
                    </h2>
                    <span
                      className={`text-md font-semibold flex items-center gap-1 ${
                        cambio === "up"
                          ? "text-green-600"
                          : cambio === "down"
                          ? "text-red-600"
                          : "text-gray-800"
                      }`}
                    >
                      {cambio === "up" && <span className="text-lg">▲</span>}
                      {cambio === "down" && <span className="text-lg">▼</span>}$
                      {stock.currentPrice.toFixed(2)}
                    </span>
                  </div>
                  <div className="mt-2 text-gray-600 text-sm">
                    <p>📈 Máx: ${stock.highPrice}</p>
                    <p>📉 Mín: ${stock.lowPrice}</p>
                    <p>⏹ Cierre ant.: ${stock.previousClosePrice}</p>
                    {fueraDeRangoMin && (
                      <p className="mt-2 text-red-600 font-semibold">
                        🚨 Precio por debajo del mínimo (${stock.alertMinPrice})
                      </p>
                    )}
                    {fueraDeRangoMax && (
                      <p className="mt-2 text-red-500 font-semibold">
                        🚨 Precio por encima del máximo (${stock.alertMaxPrice})
                      </p>
                    )}
                  </div>
                </div>
              );
            })}
          </div>

          {selectedSymbol && isReady && (
            <div className="bg-white rounded-xl shadow-lg p-6 mt-8">
              <h2 className="text-2xl font-bold mb-4 text-gray-700">
                Evolución de{" "}
                <span className="text-blue-600">{selectedSymbol}</span>
              </h2>
              <ResponsiveContainer
                key={selectedSymbol + refreshKey}
                width="100%"
                height={300}
              >
                <LineChart data={selectedHistory}>
                  <Line
                    type="monotone"
                    dataKey="price"
                    stroke="#4f46e5"
                    strokeWidth={2}
                    dot={false}
                    activeDot={{ r: 5 }}
                  />
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis
                    dataKey="timestamp"
                    type="number"
                    scale="time"
                    domain={["auto", "auto"]}
                    tickFormatter={formatTimestamp}
                    tick={{ fontSize: 12 }}
                  />
                  <YAxis domain={["auto", "auto"]} />
                  <Tooltip
                    formatter={(value) => `$${value.toFixed(2)}`}
                    labelFormatter={formatTimestamp}
                  />
                </LineChart>
              </ResponsiveContainer>
              {selectedHistory.length > 0 && (
                <p className="mt-4 text-sm text-gray-600">
                  Último precio:{" "}
                  <span className="font-semibold">
                    ${selectedHistory.at(-1)?.price.toFixed(2)}
                  </span>{" "}
                  a las{" "}
                  <span>
                    {formatTimestamp(selectedHistory.at(-1)?.timestamp)}
                  </span>
                </p>
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
}

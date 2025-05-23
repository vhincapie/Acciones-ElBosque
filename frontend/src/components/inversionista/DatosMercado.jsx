import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { fetchMarketStatus } from "../../hooks/stocks";
import {
  LineChart,
  Line,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

export default function DatosMercado() {
  const stocks = useSelector((state) => state.market.stocks);
  const [selectedSymbol, setSelectedSymbol] = useState(null);
  const [stockHistory, setStockHistory] = useState({});
  const [selectedHistory, setSelectedHistory] = useState([]);
  const [marketStatus, setMarketStatus] = useState({ abierto: false });
  const [refreshKey, setRefreshKey] = useState(0);
  const [loading, setLoading] = useState(true);

  const TWO_MINUTES = 2 * 60 * 1000;

  useEffect(() => {
    const verificarMercado = async () => {
      try {
        const estado = await fetchMarketStatus();
        setMarketStatus(estado);
      } catch {
        setMarketStatus({ abierto: false });
      }
    };

    verificarMercado();
    const id = setInterval(verificarMercado, 30 * 1000);
    return () => clearInterval(id);
  }, []);

  useEffect(() => {
    const saved = localStorage.getItem("stockHistory");
    const loaded = saved ? JSON.parse(saved) : {};
    const now = Date.now();

    const updated = { ...loaded };
    stocks.forEach((stock) => {
      const historial = updated[stock.symbol] || [];
      const lastEntry = historial.at(-1);
      if (!lastEntry || now - lastEntry.timestamp >= TWO_MINUTES) {
        updated[stock.symbol] = [
          ...historial,
          { price: stock.currentPrice, timestamp: now },
        ];
      }
    });

    setStockHistory(updated);
    setRefreshKey((r) => r + 1);
  }, [stocks]);

  useEffect(() => {
    const id = setInterval(() => {
      const saved = localStorage.getItem("stockHistory");
      if (saved) {
        setStockHistory(JSON.parse(saved));
        setRefreshKey((r) => r + 1);
      }
    }, TWO_MINUTES);
    return () => clearInterval(id);
  }, []);

  useEffect(() => {
    if (stocks.length > 0 && !selectedSymbol) {
      setSelectedSymbol(stocks[0].symbol);
    }
    if (stocks.length > 0) setLoading(false);
  }, [stocks]);

  useEffect(() => {
    const historial = stockHistory[selectedSymbol] || [];
    setSelectedHistory([...historial]);
  }, [refreshKey, stockHistory, selectedSymbol]);

  const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    const hours = date.getHours().toString().padStart(2, "0");
    const minutes = date.getMinutes().toString().padStart(2, "0");
    return `${hours}:${minutes}`;
  };

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold text-gray-800">
          Mercado en tiempo real
        </h1>
        <div
          className={`flex items-center gap-2 px-4 py-1 rounded-full text-white font-semibold text-sm shadow-md ${
            marketStatus.abierto ? "bg-green-600" : "bg-red-600"
          }`}
        >
          <div className="w-2.5 h-2.5 rounded-full bg-white"></div>
          {marketStatus.abierto ? "Mercado Abierto" : "Mercado Cerrado"}
        </div>
      </div>

      {loading ? (
        <div className="flex flex-col justify-center items-center h-40">
          <div className="animate-spin rounded-full h-12 w-12 border-t-4 border-b-4 border-blue-500 mb-4"></div>
          <p className="text-gray-600 font-medium">Cargando acciones...</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {stocks.map((stock) => {
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
                    <h2 className="text-2xl font-semibold text-gray-800">
                      {stock.symbol}
                    </h2>
                    {(() => {
                      const historial = stockHistory[stock.symbol] || [];
                      const ultimo = historial.at(-1);
                      const precio = ultimo ? ultimo.price : stock.currentPrice;

                      const penultimo = historial.at(-2);
                      const cambio = penultimo
                        ? precio > penultimo.price
                          ? "up"
                          : precio < penultimo.price
                          ? "down"
                          : "same"
                        : precio > stock.previousClosePrice
                        ? "up"
                        : precio < stock.previousClosePrice
                        ? "down"
                        : "same";

                      return (
                        <span
                          className={`text-base font-bold flex items-center gap-1 ${
                            cambio === "up"
                              ? "text-green-600"
                              : cambio === "down"
                              ? "text-red-600"
                              : "text-gray-800"
                          }`}
                        >
                          {cambio === "up" && "▲"}
                          {cambio === "down" && "▼"}${precio.toFixed(2)}
                        </span>
                      );
                    })()}
                  </div>
                  <div className="mt-2 text-gray-600 text-sm">
                    <p>📈 Máx: ${stock.highPrice}</p>
                    <p>📉 Mín: ${stock.lowPrice}</p>
                    <p>⏹ Cierre ant.: ${stock.previousClosePrice}</p>
                  </div>
                </div>
              );
            })}
          </div>

          {selectedSymbol && (
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
                    tickFormatter={(tick) => formatTimestamp(tick)}
                    tick={{ fontSize: 12 }}
                  />
                  <YAxis
                    domain={["auto", "auto"]}
                    style={{ fontSize: "0.75rem" }}
                  />
                  <Tooltip
                    formatter={(value) => `$${value.toFixed(2)}`}
                    labelFormatter={(label) => formatTimestamp(label)}
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

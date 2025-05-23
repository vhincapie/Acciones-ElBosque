import React, { useEffect, useRef, useState } from "react";
import { watchlistApi } from "../../hooks/watchlistApi";
import axios from "axios";
import { toast } from "react-toastify";

const Ordenes = () => {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState("buy");
  const [marketPrice, setMarketPrice] = useState(0);
  const [buyInDollars, setBuyInDollars] = useState(false);
  const [orderType, setOrderType] = useState("market");
  const [timeInForce, setTimeInForce] = useState("day");
  const [orderSummary, setOrderSummary] = useState(null);
  const [buyingPower, setBuyingPower] = useState(0);
  const isMounted = useRef(true);

  const [userData, setUserData] = useState({
    symbol: "",
    qty: "",
    side: "buy",
    type: "market",
    stop_price: "",
    limit_price: "",
    time_in_force: "day",
  });

  const [accionesActuales, setAccionesActuales] = useState([]);

  const calcularCostoEstimado = () => {
    const qty = parseFloat(userData.qty);
    if (buyInDollars || isNaN(qty)) return qty;

    if (orderType === "limit" || orderType === "stop_limit") {
      return qty * parseFloat(userData.limit_price || 0);
    }

    if (orderType === "stop") {
      return qty * parseFloat(userData.stop_price || 0);
    }

    return qty * marketPrice;
  };

  const estimatedCost = calcularCostoEstimado();
  const commissionRate = 0.02;
  const estimatedCommission = estimatedCost * commissionRate;
  const estimatedNetAmount =
    activeTab === "buy"
      ? estimatedCost + estimatedCommission
      : estimatedCost - estimatedCommission;

  const camposValidos = () => {
    const { symbol, qty, limit_price, stop_price, side } = userData;
    const qtyValida = !isNaN(parseFloat(qty)) && parseFloat(qty) > 0;
    const simboloValido = symbol.trim() !== "";

    if (!qtyValida || !simboloValido) return false;

    if (
      (orderType === "limit" || orderType === "stop_limit") &&
      (!limit_price || parseFloat(limit_price) <= 0)
    )
      return false;

    if (
      (orderType === "stop" || orderType === "stop_limit") &&
      (!stop_price || parseFloat(stop_price) <= 0)
    )
      return false;

    if (orderType === "stop_limit" && stop_price && limit_price) {
      const stop = parseFloat(stop_price);
      const limit = parseFloat(limit_price);
      if (
        (side === "buy" && stop > limit) ||
        (side === "sell" && stop < limit)
      ) {
        return false;
      }
    }

    if (activeTab === "buy" && estimatedNetAmount > buyingPower) {
      return false;
    }

    if (userData.side === "sell" && !poseeAccionesDelSimbolo()) {
      return false;
    }

    return true;
  };

  const obtenerMensajeInvalido = () => {
    const { symbol, qty, limit_price, stop_price, side } = userData;

    if (!symbol.trim()) return "Debe ingresar un símbolo.";
    if (!qty || isNaN(qty) || parseFloat(qty) <= 0)
      return "Debe ingresar una cantidad válida.";

    if (
      (orderType === "limit" || orderType === "stop_limit") &&
      (!limit_price || parseFloat(limit_price) <= 0)
    )
      return "Debe ingresar un precio límite válido.";

    if (
      (orderType === "stop" || orderType === "stop_limit") &&
      (!stop_price || parseFloat(stop_price) <= 0)
    )
      return "Debe ingresar un precio de activación (stop) válido.";

    if (orderType === "stop_limit" && stop_price && limit_price) {
      const stop = parseFloat(stop_price);
      const limit = parseFloat(limit_price);
      if (side === "buy" && stop > limit)
        return "En órdenes de compra, el precio Stop debe ser menor o igual al Límite.";
      if (side === "sell" && stop < limit)
        return "En órdenes de venta, el precio Stop debe ser mayor o igual al Límite.";
    }

    if (activeTab === "buy" && estimatedNetAmount > buyingPower) {
      return "No tienes saldo suficiente para realizar esta compra.";
    }

    if (userData.side === "sell" && !poseeAccionesDelSimbolo()) {
      return "No tienes acciones disponibles para venta en tu portafolio";
    }

    return "";
  };

  const actualizarSaldo = async () => {
    try {
      const res = await axios.get(
        "http://localhost:8080/api/saldo/v1/obtener",
        {
          withCredentials: true,
        }
      );
      if (res.data && res.data.buying_power) {
        setBuyingPower(parseFloat(res.data.buying_power));
      }
    } catch (err) {
      console.error("Error al actualizar el saldo:", err);
    }
  };

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/alpaca/v1/portafolio", {
        withCredentials: true,
      })
      .then((res) => {
        setAccionesActuales(res.data.actuales || []);
      })
      .catch((err) => {
        console.error("Error al obtener portafolio:", err);
      });
  }, []);

  useEffect(() => {
    let intervaloSaldo = null;
    let intervaloVerificacion = null;

    actualizarSaldo();

    const verificarYActualizar = async () => {
      try {
        const statusRes = await axios.get(
          "http://localhost:8080/api/mercado/v1/market-status",
          { withCredentials: true }
        );

        const abierto = statusRes.data.abierto;

        if (abierto && !intervaloSaldo) {
          intervaloSaldo = setInterval(actualizarSaldo, 15000);
        } else if (!abierto && intervaloSaldo) {
          clearInterval(intervaloSaldo);
          intervaloSaldo = null;
        }
      } catch (error) {
        console.error("Error al verificar el estado del mercado:", error);
      }
    };

    verificarYActualizar();
    intervaloVerificacion = setInterval(verificarYActualizar, 60000);

    return () => {
      clearInterval(intervaloSaldo);
      clearInterval(intervaloVerificacion);
    };
  }, []);

  const buscar = async () => {
    if (!query.trim()) return;
    try {
      setLoading(true);
      setOrderSummary(null);
      const res = await watchlistApi.searchStocks(query);
      setResults(res.data);
    } catch (err) {
      alert("Error al buscar acciones.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const elegirAccion = (stock) => {
    setUserData((prev) => ({
      ...prev,
      symbol: stock.symbol,
    }));
    setMarketPrice(stock.currentPrice);
  };

  const poseeAccionesDelSimbolo = () => {
    const entry = accionesActuales.find(
      (item) => item.symbol === userData.symbol
    );
    return entry && parseFloat(entry.totalQty) > 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUserData((prev) => ({ ...prev, [name]: value }));
  };

  useEffect(() => {
    return () => {
      isMounted.current = false;
      toast.dismiss();
    };
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    const confirmacion = window.confirm(
      "¿Estás seguro de realizar esta transacción?"
    );
    if (!confirmacion) {
      if (isMounted.current) toast.info("Transacción cancelada.");
      return;
    }

    const { symbol, qty, side, type, time_in_force, limit_price, stop_price } =
      userData;

    const payload = {
      symbol,
      qty: Number(qty),
      side,
      type,
      time_in_force,
    };

    if (orderType === "limit" || orderType === "stop_limit") {
      payload.limit_price = limit_price;
    }

    if (orderType === "stop" || orderType === "stop_limit") {
      payload.stop_price = stop_price;
    }

    try {
      const response = await axios.post(
        "http://localhost:8080/api/alpaca/v1/orders",
        payload,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );
      if (isMounted.current) {
        setOrderSummary(response.data);

        await actualizarSaldo();
      }
    } catch (error) {
      console.error("No se pudo efectuar la operación", error);
    }
  };

  return (
    <div className="flex flex-col lg:flex-row gap-10 px-2 py-10 bg-gray-50 min-h-screen">
      <div className="w-full lg:w-1/2 bg-white rounded-2xl shadow-lg p-6">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">
          Buscar acciones
        </h2>
        <div className="flex gap-2 mb-4">
          <input
            type="text"
            placeholder="Buscar por nombre o símbolo (ej: Tesla)"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            className="flex-1 border border-gray-300 p-3 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
          <button
            onClick={buscar}
            className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-md font-semibold"
          >
            Buscar
          </button>
        </div>

        {loading && <p className="text-gray-600">Buscando...</p>}

        {results.length > 0 && (
          <div className="max-h-[600px] overflow-y-auto pr-1">
            <ul className="space-y-4">
              {results.map((stock) => (
                <li
                  key={stock.symbol}
                  className="border border-gray-200 p-4 rounded-xl flex justify-between items-center bg-gray-50 hover:bg-gray-100 transition"
                >
                  <div>
                    <p className="text-lg font-semibold">{stock.symbol}</p>
                    <p className="text-sm text-gray-700">{stock.description}</p>
                    <p className="text-sm text-gray-500">
                      Precio actual: ${stock.currentPrice.toFixed(2)}
                    </p>
                  </div>
                  <button
                    onClick={() => elegirAccion(stock)}
                    className="px-4 py-2 rounded-md text-white bg-blue-500 hover:bg-blue-600 font-medium"
                  >
                    Elegir
                  </button>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>

      <div className="w-full lg:w-1/2 bg-white rounded-2xl shadow-lg p-6">
        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="flex border-b border-gray-200 mb-6">
            <button
              type="button"
              className={`flex-1 py-2 font-bold text-lg transition ${
                activeTab === "buy"
                  ? "text-green-600 border-b-2 border-green-600"
                  : "text-gray-500 hover:text-green-600"
              }`}
              onClick={() => {
                setActiveTab("buy");
                setUserData((prev) => ({ ...prev, side: "buy" }));
                setOrderSummary(null);
              }}
            >
              Comprar
            </button>
            <button
              type="button"
              className={`flex-1 py-2 font-bold text-lg transition ${
                activeTab === "sell"
                  ? "text-red-600 border-b-2 border-red-600"
                  : "text-gray-500 hover:text-red-600"
              }`}
              onClick={() => {
                setActiveTab("sell");
                setUserData((prev) => ({ ...prev, side: "sell" }));
                setOrderSummary(null);
              }}
            >
              Vender
            </button>
          </div>

          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Símbolo
            </label>
            <input
              type="text"
              name="symbol"
              value={userData.symbol}
              readOnly
              className="w-full border border-gray-300 px-3 py-2 rounded-md shadow-sm bg-gray-100 text-gray-600"
            />
          </div>

          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Cantidad
            </label>
            <input
              type="text"
              name="qty"
              value={userData.qty}
              onChange={handleChange}
              className="w-full border border-gray-300 px-3 py-2 rounded-md shadow-sm focus:ring-2 focus:ring-indigo-300"
            />
          </div>

          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Precio de la acción en el mercado
            </label>
            <input
              type="number"
              name="marketPrice"
              value={marketPrice}
              readOnly
              className="w-full border border-gray-300 px-3 py-2 rounded-md shadow-sm"
            />
          </div>

          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Tipo de orden
            </label>
            <select
              name="type"
              value={userData.type}
              onChange={(e) => {
                setOrderType(e.target.value);
                setUserData((prev) => ({ ...prev, type: e.target.value }));
              }}
              className="w-full border border-gray-300 px-3 py-2 rounded-md shadow-sm"
            >
              <option value="market">Market</option>
              <option value="limit">Limit</option>
              <option value="stop">Stop</option>
              <option value="stop_limit">Stop Limit</option>
            </select>
          </div>

          {(orderType === "limit" || orderType === "stop_limit") && (
            <div>
              <label className="block font-semibold text-gray-700 mb-1">
                Precio límite
              </label>
              <input
                name="limit_price"
                value={userData.limit_price || ""}
                onChange={handleChange}
                type="number"
                className="w-full border px-3 py-2 rounded-md shadow-sm"
              />
            </div>
          )}

          {(orderType === "stop" || orderType === "stop_limit") && (
            <div>
              <label className="block font-semibold text-gray-700 mb-1">
                Precio de activación (Stop)
              </label>
              <input
                name="stop_price"
                value={userData.stop_price || ""}
                onChange={handleChange}
                type="number"
                className="w-full border px-3 py-2 rounded-md shadow-sm"
              />
            </div>
          )}

          <div>
            <label className="block font-semibold text-gray-700 mb-1">
              Tiempo de vigencia
            </label>
            <select
              name="time_in_force"
              value={userData.time_in_force}
              onChange={(e) => {
                setTimeInForce(e.target.value);
                setUserData((prev) => ({
                  ...prev,
                  time_in_force: e.target.value,
                }));
              }}
              className="w-full border px-3 py-2 rounded-md shadow-sm"
            >
              <option value="day">DAY</option>
            </select>
          </div>

          {[
            {
              label: activeTab === "buy" ? "Costo estimado" : "Valor estimado",
              value: estimatedCost,
            },
            { label: "Comisión estimada (2%)", value: estimatedCommission },
            {
              label:
                activeTab === "buy"
                  ? "Total a pagar (estimado)"
                  : "Neto estimado a recibir",
              value: estimatedNetAmount,
            },
            { label: "Saldo", value: buyingPower },
          ].map(({ label, value }, idx) => (
            <div
              key={idx}
              className="flex justify-between font-medium text-gray-700"
            >
              <span>{label}</span>
              <span>${isNaN(value) ? "0.00" : value.toFixed(2)}</span>
            </div>
          ))}

          <button
            type="submit"
            disabled={!camposValidos()}
            title={!camposValidos() ? obtenerMensajeInvalido() : ""}
            className={`w-full font-bold py-2 rounded-md transition ${
              camposValidos()
                ? "bg-yellow-500 hover:bg-yellow-600 text-white"
                : "bg-gray-300 text-gray-500 cursor-not-allowed"
            }`}
          >
            Realizar orden
          </button>
        </form>

        {orderSummary && (
          <div className="mt-8 p-4 border rounded bg-gray-50">
            <h3 className="text-lg font-bold mb-3">Resumen de la orden</h3>
            <p>
              <strong>Símbolo:</strong> {orderSummary.symbol}
            </p>
            <p>
              <strong>Tipo:</strong> {orderSummary.order_type}
            </p>
            <p>
              <strong>Lado:</strong> {orderSummary.side}
            </p>
            <p>
              <strong>Cantidad:</strong> {orderSummary.qty}
            </p>
            {orderSummary.transactionAmount != null && (
              <p>
                <strong>Monto:</strong> $
                {parseFloat(orderSummary.transactionAmount).toFixed(2)}
              </p>
            )}
            {orderSummary.commissionAmount != null && (
              <p>
                <strong>Comisión:</strong> $
                {parseFloat(orderSummary.commissionAmount).toFixed(2)}
              </p>
            )}
            {orderSummary.netAmount != null && (
              <p>
                <strong>Neto:</strong> $
                {parseFloat(orderSummary.netAmount).toFixed(2)}
              </p>
            )}
            <p>
              <strong>Estado:</strong> {orderSummary.status}
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Ordenes;

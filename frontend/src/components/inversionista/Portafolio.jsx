import React, { useEffect, useState } from "react";

const API_URL = "http://localhost:8080/api/alpaca/v1/portafolio";

const Portafolio = () => {
  const [accionesActuales, setAccionesActuales] = useState([]);
  const [accionesVendidas, setAccionesVendidas] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [mensajeError, setMensajeError] = useState("");

  useEffect(() => {
    fetch(API_URL, { credentials: "include" })
      .then((res) => {
        if (!res.ok) throw new Error("No se pudo obtener el portafolio.");
        return res.json();
      })
      .then((data) => {
        setAccionesActuales(data.actuales || []);
        setAccionesVendidas(data.vendidas || []);
      })
      .catch((err) => setMensajeError(err.message))
      .finally(() => setCargando(false));
  }, []);

  if (cargando)
    return (
      <div className="text-center text-gray-600 p-4">
        Cargando portafolio...
      </div>
    );
  if (mensajeError)
    return (
      <div className="text-red-600 font-semibold p-4">
        Error: {mensajeError}
      </div>
    );

  return (
    <div className="p-6 max-w-5xl mx-auto font-sans">
      <h2 className="text-2xl font-bold mb-6 border-b pb-2">
        Resumen de Portafolio
      </h2>

      <SeccionTabla
        titulo={
          <span className="flex items-center text-green-700">
            <span className="w-3 h-3 bg-green-500 rounded-full mr-2"></span>
            Acciones en posesión
          </span>
        }
        bgColor="bg-green-50"
        borderColor="border-t-green-500"
        columnas={["Símbolo", "Cantidad", "Precio de compra"]}
        datos={accionesActuales}
        renderFila={(item) => (
          <>
            <Celda>{item.symbol}</Celda>
            <Celda>{item.totalQty}</Celda>
            <Celda>${Number(item.averageBuyPrice).toFixed(2)}</Celda>
          </>
        )}
      />

      <SeccionTabla
        titulo={
          <span className="flex items-center text-red-700">
            <span className="w-3 h-3 bg-red-500 rounded-full mr-2"></span>
            Acciones vendidas
          </span>
        }
        bgColor="bg-red-50"
        borderColor="border-t-red-500"
        columnas={["Símbolo", "Cantidad vendida", "Precio de venta"]}
        datos={accionesVendidas}
        renderFila={(item) => (
          <>
            <Celda>{item.symbol}</Celda>
            <Celda>{item.totalVendidas}</Celda>
            <Celda>${Number(item.averageSellPrice).toFixed(2)}</Celda>
          </>
        )}
      />
    </div>
  );
};

const SeccionTabla = ({
  titulo,
  columnas,
  datos,
  renderFila,
  bgColor,
  borderColor,
}) => (
  <div
    className={`bg-white rounded-lg shadow-md p-6 mb-8 border-t-4 ${borderColor}`}
  >
    <h3 className="text-lg font-semibold mb-4">{titulo}</h3>
    <div className="overflow-x-auto">
      <table className="w-full table-fixed border-collapse">
        <thead>
          <tr className={`${bgColor} text-sm text-gray-700`}>
            {columnas.map((col, i) => (
              <th
                key={i}
                className="w-1/3 px-4 py-2 border-b font-medium text-center"
              >
                {col}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {datos.length === 0 ? (
            <tr>
              <td
                colSpan={columnas.length}
                className="text-center text-gray-500 py-4"
              >
                No hay datos disponibles.
              </td>
            </tr>
          ) : (
            datos.map((item, index) => <tr key={index}>{renderFila(item)}</tr>)
          )}
        </tbody>
      </table>
    </div>
  </div>
);

const Celda = ({ children }) => (
  <td className="px-4 py-3 border-b text-center text-sm text-gray-800">
    {children}
  </td>
);

export default Portafolio;

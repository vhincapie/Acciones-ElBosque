import { useEffect } from "react";
import axios from "axios";
import { setStocks, setMarketStatus } from "./marketSlice";
import store from "./store";

export default function useStockUpdater() {
const {getState} = store
const username = getState().auth.username

  useEffect(() => {
    if(!username) return
    const fetchStocks = async () => {
      try {
        const { data: status } = await axios.get(
          "http://localhost:8080/api/mercado/v1/market-status",
          { withCredentials: true }
        );
        store.dispatch(setMarketStatus(status));

        if (!status.abierto) return;

        const { data: stocks } = await axios.get(
          "http://localhost:8080/api/mercado/v1/stock-relevant",
          { withCredentials: true }
        );
        store.dispatch(setStocks(stocks));
        guardarHistorial(stocks);
      } catch (error) {
        console.error("Error actualizando acciones:", error);
      }
    };

    fetchStocks();
    const id = setInterval(fetchStocks, 60000);
    return () => clearInterval(id);
  }, [username]);

  const guardarHistorial = (stocks) => {
    const now = Date.now();
    const MAX_HISTORY_LENGTH = 300;

    const today = new Date().toISOString().split("T")[0];
    const lastSavedDate = localStorage.getItem("lastSavedDate");

    if (lastSavedDate !== today) {
      localStorage.removeItem("stockHistory");
      localStorage.setItem("lastSavedDate", today);
    }

    const saved = localStorage.getItem("stockHistory");
    const stockHistory = saved ? JSON.parse(saved) : {};
    const updated = { ...stockHistory };

    stocks.forEach((stock) => {
      const historial = updated[stock.symbol] || [];
      const lastEntry = historial.at(-1);
      if (!lastEntry || now - lastEntry.timestamp >= 2 * 60 * 1000) {
        updated[stock.symbol] = [
          ...historial,
          { price: stock.currentPrice, timestamp: now },
        ].slice(-MAX_HISTORY_LENGTH);
      }
    });

    try {
      localStorage.setItem("stockHistory", JSON.stringify(updated));
    } catch (e) {
      console.warn("⚠️ Error guardando historial");
    }
  };
}


// hooks/useWatchlistUpdater.js
import { useEffect } from "react";
import { useSelector } from "react-redux";
import { watchlistApi } from "./watchlistApi";
import { fetchMarketStatus } from "./stocks";

const TWO_MINUTES = 2 * 60 * 1000;
const MAX_HISTORY_LENGTH = 300;

export default function useWatchlistUpdater() {
  const username = useSelector((state) => state.auth.username);

  useEffect(() => {
    if (!username) return;

    const STORAGE_KEY = `stockHistoryWatchlist_${username}`;
    const STORAGE_DATE_KEY = `watchlistHistoryDate_${username}`;

    const fetchAndStore = async () => {
      try {
        const estado = await fetchMarketStatus();
        if (!estado.abierto) return;

        const res = await watchlistApi.getAll();
        const lista = res.data;

        const now = Date.now();
        const today = new Date().toISOString().split("T")[0];
        const lastSavedDate = localStorage.getItem(STORAGE_DATE_KEY);

        if (lastSavedDate !== today) {
          localStorage.removeItem(STORAGE_KEY);
          localStorage.setItem(STORAGE_DATE_KEY, today);
        }

        const saved = localStorage.getItem(STORAGE_KEY);
        const loaded = saved ? JSON.parse(saved) : {};
        const updated = { ...loaded };

        lista.forEach((stock) => {
          const historial = updated[stock.symbol] || [];
          const lastEntry = historial.at(-1);

          if (!lastEntry || now - lastEntry.timestamp >= TWO_MINUTES) {
            updated[stock.symbol] = [
              ...historial,
              { price: stock.currentPrice, timestamp: now },
            ].slice(-MAX_HISTORY_LENGTH);
          }
        });

        localStorage.setItem(STORAGE_KEY, JSON.stringify(updated));
      } catch (e) {
        console.error("❌ Error en useWatchlistUpdater (local):", e);
      }
    };

    fetchAndStore();
    const intervalId = setInterval(fetchAndStore, TWO_MINUTES);
    return () => clearInterval(intervalId);
  }, [username]);
}

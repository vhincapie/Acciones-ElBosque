import axios from "axios";

const BASE_URL = "http://localhost:8080/api/watchlist/v1";

const axiosInstance = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});

export const watchlistApi = {
  getAll: () => axiosInstance.get("/items"),
  getById: (id) => axiosInstance.get(`/item/${id}`),
  create: (item) => axiosInstance.post("/item", item),
  update: (id, item) => axiosInstance.put(`/item/${id}`, item),
  delete: (id) => axiosInstance.delete(`/item/${id}`),
  updateAlerts: (id, min, max) =>
    axiosInstance.put(`/item/${id}/alerts`, null, {
      params: { min, max },
    }),
  getSymbolInfo: (symbol) => axiosInstance.get(`/symbol/${symbol}`),
  searchStocks: (query) => axiosInstance.get(`/search/${query}`),
};

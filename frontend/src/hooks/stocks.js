import axios from "axios";

const BASE_URL = "http://localhost:8080/api/mercado/v1";

export async function fetchStocks() {
  try {
    const response = await axios.get(`${BASE_URL}/stock-relevant`, {
      withCredentials: true,
    });
    return response.data;
  } catch (error) {
    console.error("Error obteniendo acciones relevantes:", error);
    throw error;
  }
}

export async function fetchMarketStatus() {
  try {
    const response = await axios.get(`${BASE_URL}/market-status`, {
      withCredentials: true,
    });
    return response.data;
  } catch (error) {
    console.error("Error obteniendo estado del mercado:", error);
    throw error;
  }
}

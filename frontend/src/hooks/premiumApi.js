import axios from "axios";

const API_URL = "http://localhost:8080/api/suscripcion/v1";

export const obtenerEstadoSuscripcion = async () => {
  const response = await axios.get(`${API_URL}/status`, {
    withCredentials: true,
  });
  return response.data;
};

export const crearSesionCheckout = async (tipoPlan) => {
  const response = await axios.get(`${API_URL}/checkout`, {
    params: { tipoPlan },
    withCredentials: true,
  });
  return response.data.url;
};

export const verificarPremium = async () => {
  const response = await axios.get(`${API_URL}/verificar-premium`, {
    withCredentials: true,
  });
  return response.data;
};

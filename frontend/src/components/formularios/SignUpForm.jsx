import React, { useState } from "react";
import BotonRegistro from "../botones/BotonRegistro";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-datepicker/dist/react-datepicker.css";
import DatePicker from "react-datepicker";
import { subYears } from "date-fns";

const countryCodes = {
  Colombia: "COL",
  Ecuador: "ECU",
  USA: "USA",
  Mexico: "MEX",
};

const locationData = {
  Colombia: {
    Antioquia: ["Medellin", "Bello", "Envigado"],
    Bogota: ["Bogota"],
    Atlantico: ["Barranquilla", "Soledad", "Malambo"],
  },
  Ecuador: {
    Guayas: ["Guayaquil", "Naranjal", "Balzar"],
    Pichincha: ["Quito", "Mejia", "Cayambe"],
    "Zamora Chinchipe": ["Zamora", "Yantzaza", "Chinchipe"],
  },
  USA: {
    Arizona: ["Phoenix", "Tucson", "Prescott"],
    California: ["San Francisco", "Los Angeles", "San Diego"],
    "Nueva York": ["Nueva York"],
  },
  Mexico: {
    "Baja California": ["Mexicali", "Tijuana", "Tecate"],
    "Ciudad de Mexico": ["Ciudad de Mexico"],
    Jalisco: ["Colotlan", "Chapala", "Guadalajara"],
  },
};

const SignUpForm = () => {
  const navigate = useNavigate();
  const [cuenta, setCuenta] = useState({
    given_name: "",
    family_name: "",
    date_of_birth: "",
    email_address: "",
    phone_number: "",
    street_address: "",
    country: "",
    state: "",
    city: "",
    postal_code: "",
    password: "",
    marketInterestId: "",
  });

  const [mostrarFecha, setMostrarFecha] = useState(false);

  const validarPassword = (password) =>
    /^(?=.*[A-Z])(?=.*\d).{8,}$/.test(password);

  const calcularEdad = (fecha) => {
    const hoy = new Date();
    const nacimiento = new Date(fecha);
    let edad = hoy.getFullYear() - nacimiento.getFullYear();
    const mes = hoy.getMonth() - nacimiento.getMonth();
    if (mes < 0 || (mes === 0 && hoy.getDate() < nacimiento.getDate())) edad--;
    return edad;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCuenta((prev) => ({ ...prev, [name]: value }));

    if (name === "date_of_birth") {
      setMostrarFecha(calcularEdad(value) >= 18);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (calcularEdad(cuenta.date_of_birth) < 18) {
      toast.error("Debes ser mayor de edad para registrarte");
      return;
    }

    const camposObligatorios = [
      "given_name",
      "family_name",
      "date_of_birth",
      "email_address",
      "phone_number",
      "street_address",
      "country",
      "state",
      "city",
      "password",
      "marketInterestId",
    ];

    for (const campo of camposObligatorios) {
      if (!cuenta[campo]) {
        toast.error("Por favor completa todos los campos requeridos");
        return;
      }
    }

    if (!validarPassword(cuenta.password)) {
      toast.error(
        "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número"
      );
      return;
    }

    try {
      const createAccountRequestDTO = {
        contact: {
          given_name: cuenta.given_name,
          family_name: cuenta.family_name,
          date_of_birth: cuenta.date_of_birth,
          email_address: cuenta.email_address,
          phone_number: cuenta.phone_number,
          street_address: [cuenta.street_address],
          country: countryCodes[cuenta.country],
          state: cuenta.state,
          city: cuenta.city,
          postal_code: cuenta.postal_code,
        },
        identity: {
          given_name: cuenta.given_name,
          family_name: cuenta.family_name,
          date_of_birth: cuenta.date_of_birth,
          country_of_tax_residence: countryCodes[cuenta.country],
        },
        marketInterestId: cuenta.marketInterestId,
        password: cuenta.password,
      };

      await axios.post(
        "http://localhost:8080/api/auth/v1/register",
        createAccountRequestDTO
      );
      const prev = JSON.parse(localStorage.getItem("cuentas")) || [];
      localStorage.setItem("cuentas", JSON.stringify([...prev, cuenta]));

      toast.success("✅ Cuenta registrada con éxito");
      setTimeout(() => navigate("/login"), 1500);
    } catch (error) {
      console.error("Error al crear cuenta", error);
    
      if (error.response) {
        console.log("Respuesta del servidor:", error.response.data);
    
      
        if (
          error.response.data &&
          typeof error.response.data.message === "string" &&
          error.response.data.message.toLowerCase().includes("correo")
        ) {
          toast.error("Este correo ya está en uso o registrado");
        } else if (
          typeof error.response.data === "string" &&
          error.response.data.toLowerCase().includes("correo")
        ) {
          toast.error("Este correo ya está en uso o registrado");
        } else {
          toast.error("Error al crear cuenta");
        }
      } else {
        toast.error("Error de conexión con el servidor");
      } 
    }
  };

  const campos = [
    { label: "Correo electrónico", name: "email_address", type: "email" },
    { label: "Contraseña", name: "password", type: "password" },
    { label: "Nombres", name: "given_name", type: "text" },
    { label: "Apellidos", name: "family_name", type: "text" },
    {
      label: "Fecha de nacimiento",
      name: "date_of_birth",
      type: "date",
      extra: !mostrarFecha && (
        <span className="text-red-500 text-xs mt-1">
          * Solo mayores de edad (18+)
        </span>
      ),
    },
    { label: "Número de teléfono", name: "phone_number", type: "text" },
    { label: "Dirección", name: "street_address", type: "text" },
    {
      label: "País",
      name: "country",
      type: "select",
      options: Object.keys(locationData),
    },
    {
      label: "Estado / Departamento",
      name: "state",
      type: "select",
      options: cuenta.country
        ? Object.keys(locationData[cuenta.country] || {})
        : [],
    },
    {
      label: "Ciudad / Municipio",
      name: "city",
      type: "select",
      options:
        cuenta.country && cuenta.state
          ? locationData[cuenta.country]?.[cuenta.state] || []
          : [],
    },
    { label: "Código Postal", name: "postal_code", type: "text" },
    {
      label: "Mercado de valores de interés",
      name: "marketInterestId",
      type: "select",
      options: [
        { value: "1", label: "Tecnología" },
        { value: "2", label: "Salud" },
        { value: "3", label: "Finanzas" },
        { value: "4", label: "Consumo discrecional" },
        { value: "5", label: "Consumo básico" },
        { value: "6", label: "Energía" },
        { value: "7", label: "Industriales" },
        { value: "8", label: "Materiales" },
        { value: "9", label: "Servicios públicos" },
        { value: "10", label: "Bienes raíces" },
        { value: "11", label: "Telecomunicaciones" },
      ],
    },
  ];

  const maxFecha = new Date(
    new Date().setFullYear(new Date().getFullYear() - 18)
  )
    .toISOString()
    .split("T")[0];

  return (
    <div className="min-h-screen bg-gray-900 py-12 px-4 flex justify-center">
      <ToastContainer position="top-center" />
      <form
        onSubmit={handleSubmit}
        className="bg-white w-full max-w-5xl p-8 rounded-3xl shadow-2xl border border-gray-200 space-y-6"
      >
        <h2 className="text-4xl font-bold text-center text-indigo-700 mb-6">
          Regístrate con nosotros
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 text-sm">
          {campos.map((field, idx) => (
            <div key={idx} className="flex flex-col">
              <label className="mb-1 font-medium text-gray-700">
                {field.label}
              </label>
              {field.type === "select" ? (
                <select
                  name={field.name}
                  value={cuenta[field.name]}
                  onChange={handleChange}
                  className="bg-gray-100 border border-gray-300 rounded-md px-3 py-2"
                  required
                  disabled={
                    (field.name === "state" && !cuenta.country) ||
                    (field.name === "city" &&
                      (!cuenta.country || !cuenta.state))
                  }
                >
                  <option value="">
                    Seleccione {field.label.toLowerCase()}
                  </option>
                  {field.options.map((opt) =>
                    typeof opt === "string" ? (
                      <option key={opt} value={opt}>
                        {opt}
                      </option>
                    ) : (
                      <option key={opt.value} value={opt.value}>
                        {opt.label}
                      </option>
                    )
                  )}
                </select>
              ) : field.name === "date_of_birth" ? (
                <>
                  <DatePicker
                    selected={
                      cuenta.date_of_birth
                        ? new Date(cuenta.date_of_birth)
                        : null
                    }
                    onChange={(date) => {
                      const isoDate = date.toISOString().split("T")[0];
                      setCuenta((prev) => ({ ...prev, date_of_birth: isoDate }));
                      setMostrarFecha(calcularEdad(isoDate) >= 18);
                    }}
                    dateFormat="yyyy-MM-dd"
                    maxDate={subYears(new Date(), 18)}
                    showYearDropdown
                    scrollableYearDropdown
                    yearDropdownItemNumber={100}
                    placeholderText="Seleccione fecha de nacimiento"
                    className="bg-white border border-gray-300 rounded-md px-3 py-2 text-gray-700 shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 w-full"
                  />
                  {field.extra}
                </>
              ) : (
                <>
                  <input
                    type={field.type}
                    name={field.name}
                    value={cuenta[field.name]}
                    onChange={handleChange}
                    className="bg-gray-100 border border-gray-300 rounded-md px-3 py-2"
                    required
                  />
                  {field.extra}
                </>
              )}
            </div>
          ))}
        </div>

        <div className="pt-6 text-center justify-items-end">
          <BotonRegistro />
        </div>
      </form>
    </div>
  );
};

export default SignUpForm;

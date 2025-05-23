import React from "react";

const DatosUsuario = ({ userData }) => {
  if (!userData) {
    return <div className="text-gray-600 text-center mt-8">Cargando datos de usuario...</div>;
  }

  return (
    <div className="bg-white rounded-xl shadow-md p-10 w-full">
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6 text-sm text-gray-700">
        <div>
          <h3 className="font-semibold text-gray-900">Username</h3>
          <p>{userData.username}</p>
        </div>
        <div>
          <h3 className="font-semibold text-gray-900">Email</h3>
          <p>{userData.email}</p>
        </div>
        <div>
          <h3 className="font-semibold text-gray-900">Nombre completo</h3>
          <p>{userData.nombre}</p>
        </div>
        <div>
          <h3 className="font-semibold text-gray-900">Rol</h3>
          <p>{userData.rol}</p>
        </div>
        <div>
          <h3 className="font-semibold text-gray-900">Interés de mercado</h3>
          <p>{userData.marketInterest || "No asignado"}</p>
        </div>
        <div>
          <h3 className="font-semibold text-gray-900">Cuenta premium</h3>
          <p>{userData.activo ? "Sí" : "No"}</p>
        </div>
        <div>
          <h3 className="font-semibold text-gray-900">Tipo de plan</h3>
          <p>{userData.tipoPlan || "Sin suscripción"}</p>
        </div>
        <div>
          <h3 className="font-semibold text-gray-900">Customer ID (Stripe)</h3>
          <p className="break-all">{userData.customer || "No aplica"}</p>
        </div>
        <div>
          <h3 className="font-semibold text-gray-900">Subscription ID (Stripe)</h3>
          <p className="break-all">{userData.subscription || "No aplica"}</p>
        </div>
      </div>
    </div>
  );
};

export default DatosUsuario;

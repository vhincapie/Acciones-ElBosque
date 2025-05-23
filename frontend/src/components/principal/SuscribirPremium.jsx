import React from "react";
import PremiumForm from "../formularios/PremiumForm";

const SuscribirPremium = () => {
  return (
    <div className="h-[100vh] pt-15">
      <div className="text-center my-15 text-4xl">
        <h1>
          <strong>Valores UnBosque</strong>
        </h1>
      </div>
      <div className="justify-items-center text-2xl my-10">
        <p className="text-justify">
          Hazte premium y podras personalizar las acciones de mas movimiento{" "}
          <br />
          en el mercado bursatil
          <br />
          Personaliza y crece tu dinero.
        </p>
      </div>
      <div className="py-20 flex justify-around flex-row">
        <PremiumForm />
      </div>
    </div>
  );
};

export default SuscribirPremium;

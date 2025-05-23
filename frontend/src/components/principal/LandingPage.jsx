import React from "react";
import LinkRegistro from "../botones/LinkRegistro";
import logo from '../../../public/valorLogo.png'

const LandingPage = () => {
  return (
    <div className="min-h-screen bg-gray-900 text-white px-6 py-20 flex items-center justify-center">
      <div className="max-w-6xl w-full grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
        <div>
          <h1 className="text-4xl md:text-5xl font-extrabold leading-snug mb-6">
            <span className="text-indigo-400">Invierte con</span>{" "}
            <span className="text-yellow-400">Valores UnBosque</span>
          </h1>
          <p className="text-gray-300 text-lg mb-6">
            Plataforma inteligente para trading con tecnología de vanguardia
            conectada a <strong>Alpaca Markets</strong>.
          </p>
          <ul className="text-gray-400 list-disc pl-5 space-y-2 mb-8 list-none">
            <li>✔️ Algoritmos financieros avanzados</li>
            <li>✔️ Accede a mercados globales estadounidenses</li>
            <li>✔️ Seguridad, análisis y acompañamiento</li>
          </ul>
          <LinkRegistro />
        </div>
        <div className="flex justify-center">
          <img
            src={logo}
            alt="Balanza inversión"
            className="w-80 md:w-[360px] object-contain drop-shadow-xl"
          />
        </div>
      </div>
    </div>
  );
};

export default LandingPage;

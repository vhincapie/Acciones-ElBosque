import React from "react";
import { Routes, Route } from "react-router-dom";
import Index from "./../pages/index/Index";
import InicioSesion from "./../pages/index/InicioSesion";
import Registro from "./../pages/index/Registro";
import PaginaPrincipal from "../pages/inversionista/PaginaPrincipal";
import PerfilUsuario from "../pages/inversionista/PerfilUsuario";
import Premium from "../pages/index/Premium";
import SuccessPage from "../components/inversionista/SuccessPage";
import CancelPage from "../components/inversionista/CancelPage";
import ResetPassword from "../pages/index/ResetPassword";
import NewPassword from "../pages/index/NewPassword";
 
const Main = () => {
  return (
    <main className="min-h-[100vh] bg-gradient-to-r from-[#eee99e] to-[#f0e087]">
      <Routes>
        <Route path="/" element={<Index />} />
        <Route path="/login" element={<InicioSesion />} />
        <Route path="/signup" element={<Registro />} />
        <Route path="/premium" element={<Premium />} />
        <Route path="/usuario" element={<PaginaPrincipal />} />
        <Route path="/usuario/perfil" element={<PerfilUsuario />} />
        <Route path="/success" element={<SuccessPage />} />{" "}
        <Route path="/cancel" element={<CancelPage />} />{" "}
        <Route path="/recuperar-pass" element={<ResetPassword />} />
        <Route path="/reset-password" element={<NewPassword />} />
      </Routes>
    </main>
  );
};

export default Main;

import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Menu, X } from "lucide-react";
import { useDispatch, useSelector } from "react-redux";
import { logout } from "../hooks/authSlice";
import logo from '../../public/valorLogo.png'

const Nav = () => {
  const usernameGuardado = sessionStorage.getItem("username");
  const auth = useSelector((state) => state.auth);
  const [isOpen, setIsOpen] = useState(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const handleLogout = async () => {
    try {
      dispatch(logout());
      navigate("/");
    } catch (error) {
      console.error("Error al cerrar sesion de usuario", error);
    }
  };

  return (
    <nav className="bg-black text-white flex justify-between items-center p-1">
      <div className="container flex items-center p-4 max-w-screen-x1">
        <div className="flex items-center gap-3">
          <img
            className="w-10 h-10 mr-2"
            src={logo}
            alt="Logo Valores UnBosque"
          />
        </div>
        <div className="hover:bg-gray-800 hover:text-[#eee99e]">
          {!auth.isAuthenticated ? (
            <h2>
              <Link to={"/"}>
                <strong>Valores UnBosque</strong>
              </Link>
            </h2>
          ) : (
            <h2>
              <Link to={`/usuario`}>
                <strong>Valores UnBosque</strong>
              </Link>
            </h2>
          )}
        </div>
      </div>
      <button onClick={() => setIsOpen(!isOpen)} className="lg:hidden">
        {isOpen ? <X size={24} /> : <Menu size={24} />}
      </button>
      <div
        className={`flex-col lg:flex lg:flex-row lg:items-center absolute lg:static top-16 left-0 w-full lg:w-auto z-50 bg-black transition-transform duration-300 ease-in-out ${
          isOpen ? "flex" : "hidden"
        }`}
      >
        {!auth.isAuthenticated ? (
          <>
            <div className="mx-2.5 hover:bg-gray-800 hover:text-[#eee99e]">
              <Link to={"/login"}>Login</Link>
            </div>
            <div className="hover:bg-gray-800 hover:text-[#eee99e]">
              <Link to={"/signup"}>Sign Up</Link>
            </div>
          </>
        ) : (
          <>
            <div className="hover:bg-gray-800 hover:text-[#eee99e] mr-5">
              <Link to={`/usuario/perfil`}>
                👤 {usernameGuardado}
              </Link>
            </div>
            <div className="hover:bg-gray-800 hover:text-[#eee99e]">
              <button onClick={handleLogout}>Logout</button>
            </div>
          </>
        )}
      </div>
    </nav>
  );
};

export default Nav;

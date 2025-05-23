import React from "react";
import {
  FaFacebookF,
  FaInstagram,
  FaLinkedinIn,
  FaYoutube,
  FaTiktok,
  FaWhatsapp,
} from "react-icons/fa";

const Footer = () => {
  return (
    <footer className="bg-gray-200 text-black font-bold py-5 px-6">
      <div className="max-w-7x1 mx-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-6 text-center sm:text-center md:text-center lg:text-left">
        <div>
          <h3 className="text-2xl">Desarrollo</h3>
          <ul className="space-y-1">
            <li>Transacciones</li>
            <li>Mercado de capitales</li>
            <li>Aprende</li>
          </ul>
        </div>
        <div>
          <h3 className="text-2xl">Nosotros</h3>
          <ul className="space-y-1">
            <li>Historia</li>
            <li>Contáctenos</li>
          </ul>
        </div>
        <div>
          <h3 className="text-2xl">Comunidad</h3>
          <ul className="space-y-1">
            <li>Foros económicos</li>
            <li>Blog</li>
          </ul>
        </div>
        <div className="lg:col-span-2 flex flex-col items-center lg:items-start">
          <h3 className="text-2xl mb-3">Redes</h3>
          <div className="flex flex-wrap justify-center lg:justify-start gap-4 text-2xl">
            <a
              href="https://facebook.com"
              target="_blank"
              rel="noopener noreferrer"
            >
              <FaFacebookF />
            </a>
            <a
              href="https://instagram.com"
              target="_blank"
              rel="noopener noreferrer"
            >
              <FaInstagram />
            </a>
            <a
              href="https://linkedin.com"
              target="_blank"
              rel="noopener noreferrer"
            >
              <FaLinkedinIn />
            </a>
            <a
              href="https://youtube.com"
              target="_blank"
              rel="noopener noreferrer"
            >
              <FaYoutube />
            </a>
            <a
              href="https://tiktok.com"
              target="_blank"
              rel="noopener noreferrer"
            >
              <FaTiktok />
            </a>
            <a
              href="https://wa.me/123456789"
              target="_blank"
              rel="noopener noreferrer"
            >
              <FaWhatsapp />
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;

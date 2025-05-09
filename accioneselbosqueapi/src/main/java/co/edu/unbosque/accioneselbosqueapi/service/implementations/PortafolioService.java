package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.UsuarioNoEncontradoException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.PortafolioItemDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.Order;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IOrderRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IPortafolioService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortafolioService implements IPortafolioService {

    private final IUserService userService;
    private final IAlpacaAccountRepository alpacaAccountRepository;
    private final IOrderRepository orderRepository;

    public PortafolioService(IUserService userService,
                             IAlpacaAccountRepository alpacaAccountRepository,
                             IOrderRepository orderRepository) {
        this.userService = userService;
        this.alpacaAccountRepository = alpacaAccountRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Map<String, List<PortafolioItemDTO>> getResumenAcciones() {
        AlpacaAccount cuenta = obtenerCuentaAlpacaDelUsuario();
        List<Order> ordenes = obtenerOrdenesEjecutadas(cuenta);
        Map<String, List<Order>> agrupadas = agruparOrdenesPorSimbolo(ordenes);
        return construirResumenSeparado(agrupadas);
    }


    private AlpacaAccount obtenerCuentaAlpacaDelUsuario() {
        var user = userService.getAuthenticatedUserEntity();
        return alpacaAccountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Cuenta Alpaca no encontrada."));
    }


    private List<Order> obtenerOrdenesEjecutadas(AlpacaAccount cuenta) {
        return orderRepository.findByAlpacaAccountAndStatus(cuenta, "filled");
    }


    private Map<String, List<Order>> agruparOrdenesPorSimbolo(List<Order> ordenes) {
        return ordenes.stream().collect(Collectors.groupingBy(Order::getSymbol));
    }


    private Map<String, List<PortafolioItemDTO>> construirResumenSeparado(Map<String, List<Order>> agrupadas) {
        List<PortafolioItemDTO> actuales = new ArrayList<>();
        List<PortafolioItemDTO> vendidas = new ArrayList<>();

        for (Map.Entry<String, List<Order>> entry : agrupadas.entrySet()) {
            String symbol = entry.getKey();
            List<Order> ordenes = entry.getValue();

            int totalCompradas = 0;
            BigDecimal montoComprado = BigDecimal.ZERO;

            int totalVendidas = 0;
            BigDecimal montoVendido = BigDecimal.ZERO;

            for (Order o : ordenes) {
                int qty = o.getQty().intValue();
                if ("buy".equalsIgnoreCase(o.getSide())) {
                    totalCompradas += qty;
                    montoComprado = montoComprado.add(o.getTransactionAmount());
                } else if ("sell".equalsIgnoreCase(o.getSide())) {
                    totalVendidas += qty;
                    montoVendido = montoVendido.add(o.getTransactionAmount());
                }
            }

            int cantidadActual = totalCompradas - totalVendidas;

            if (cantidadActual > 0) {
                PortafolioItemDTO dtoActual = new PortafolioItemDTO();
                dtoActual.setSymbol(symbol);
                dtoActual.setTotalQty(cantidadActual);
                dtoActual.setAverageBuyPrice(
                        totalCompradas > 0
                                ? montoComprado.divide(BigDecimal.valueOf(totalCompradas), 2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO
                );
                dtoActual.setSide("actual");
                actuales.add(dtoActual);
            }

            if (totalVendidas > 0) {
                PortafolioItemDTO dtoVendida = new PortafolioItemDTO();
                dtoVendida.setSymbol(symbol);
                dtoVendida.setTotalVendidas(totalVendidas);
                dtoVendida.setAverageSellPrice(
                        totalVendidas > 0
                                ? montoVendido.divide(BigDecimal.valueOf(totalVendidas), 2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO
                );
                dtoVendida.setSide("vendida");
                vendidas.add(dtoVendida);
            }
        }

        Map<String, List<PortafolioItemDTO>> resultado = new HashMap<>();
        resultado.put("actuales", actuales);
        resultado.put("vendidas", vendidas);
        return resultado;
    }

}

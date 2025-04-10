package co.edu.unbosque.accioneselbosqueapi.model.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "market_interest")
public class MarketInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mercado")
    private Long id;

    @Column(name = "nombre")
    private String nombre;




    public MarketInterest() {}

    public MarketInterest(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

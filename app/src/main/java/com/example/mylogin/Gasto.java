package com.example.mylogin;

public class Gasto {
    private String id;
    private String nombre;
    private Long monto;
    private String fecha;

    public Gasto(String id, String nombre, Long monto, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.monto = monto;
        this.fecha = fecha;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public Long getMonto() { return monto; }
    public String getFecha() { return fecha; }
}

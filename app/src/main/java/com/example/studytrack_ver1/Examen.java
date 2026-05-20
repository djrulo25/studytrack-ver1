package com.example.studytrack_ver1;

public class Examen {
    private String id;
    private String nombre;
    private String fecha;
    private String hora;
    private String materiaNombre;

    public Examen() {
        // Required for Firestore
    }

    public Examen(String nombre, String fecha, String hora, String materiaNombre) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.materiaNombre = materiaNombre;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getMateriaNombre() { return materiaNombre; }
    public void setMateriaNombre(String materiaNombre) { this.materiaNombre = materiaNombre; }
}

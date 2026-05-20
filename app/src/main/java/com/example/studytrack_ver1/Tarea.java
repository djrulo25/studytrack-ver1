package com.example.studytrack_ver1;

import java.util.Date;

public class Tarea {
    private String id;
    private String nombre;
    private String descripcion;
    private String fechaEntrega;
    private String materiaNombre;
    private boolean completada;

    public Tarea() {
        // Required for Firestore
    }

    public Tarea(String nombre, String descripcion, String fechaEntrega, String materiaNombre, boolean completada) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
        this.materiaNombre = materiaNombre;
        this.completada = completada;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(String fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getMateriaNombre() { return materiaNombre; }
    public void setMateriaNombre(String materiaNombre) { this.materiaNombre = materiaNombre; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
}

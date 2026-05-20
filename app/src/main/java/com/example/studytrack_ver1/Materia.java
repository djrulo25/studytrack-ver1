package com.example.studytrack_ver1;

public class Materia {
    private String id;
    private String nombre;
    private String semestre;
    private String profesor;
    private String estado;

    public Materia() {
        // Constructor vacío requerido por Firestore
    }

    public Materia(String nombre, String semestre, String profesor, String estado) {
        this.nombre = nombre;
        this.semestre = semestre;
        this.profesor = profesor;
        this.estado = estado;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }

    public String getProfesor() { return profesor; }
    public void setProfesor(String profesor) { this.profesor = profesor; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
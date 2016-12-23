package com.javier.jmreservas;

import java.io.Serializable;

public class Profesor implements Serializable {
    private int id;
    private String nombre;
    private String apellido;
    private String usuario;
    private String contrasenya;
    private String asignatura;
    private String email;
    private int valido;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getContrasenya() { return contrasenya; }
    public String getEmail() { return email; }
    public int getValido() { return valido; }

    public Profesor(int id, String nombre, String apellido, String usuario, String contrasenya, String asignatura, String email, int valido) {
        super();
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.usuario = usuario;
        this.contrasenya = contrasenya;
        this.asignatura = asignatura;
        this.email = email;
        this.valido = valido;
    }
    public Profesor(String nombre, String apellido, String usuario, String contrasenya, String asignatura, String email, int valido) {
        super();
        this.nombre = nombre;
        this.apellido = apellido;
        this.usuario = usuario;
        this.contrasenya = contrasenya;
        this.asignatura = asignatura;
        this.email = email;
        this.valido = valido;
    }

    @Override
    public String toString() {
        return  nombre + " " + apellido + '\n' + email;
    }
}

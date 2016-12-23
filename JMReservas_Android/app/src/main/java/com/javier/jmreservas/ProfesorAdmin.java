package com.javier.jmreservas;

import java.io.Serializable;

public class ProfesorAdmin implements Serializable {
    private int id;
    private String nombre;
    private String apellido;
    private String asignatura;
    private String email;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }

    public ProfesorAdmin(int id, String nombre, String apellido, String asignatura, String email) {
        super();
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.asignatura = asignatura;
        this.email = email;
    }
    public ProfesorAdmin(String nombre, String apellido, String asignatura, String email) {
        super();
        this.nombre = nombre;
        this.apellido = apellido;
        this.asignatura = asignatura;
        this.email = email;
    }

    @Override
    public String toString() {
        return  "\n" + nombre + " " + apellido + '\n' + "(" + asignatura + ")" + '\n' + email + "\n";
    }
}

package com.javier.jmreservas;

import java.io.Serializable;

public class Aula implements Serializable {
    private int numero;
    private String nombre;
    private int puestos;

    public Aula(int numero, String nombre, int puestos) {
        super();
        this.numero = numero;
        this.nombre = nombre;
        this.puestos = puestos;
    }
    public Aula(String nombre, int puestos) {
        super();
        this.nombre = nombre;
        this.puestos = puestos;
    }

    @Override
    public String toString() {
        return "\n" + nombre + "   -   Número: " + numero + "\nSitios disponibles: " + puestos+ "\n";
    }
}

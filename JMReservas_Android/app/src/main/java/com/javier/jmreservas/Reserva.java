package com.javier.jmreservas;

import java.io.Serializable;

public class Reserva implements Serializable {
    private int id;
    private int hora;
    private int dia;
    private int mes;
    private int anyo;
    private int id_aula;
    private String aula;
    private int id_profesor;
    private String profesor;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getHora() { return hora; }
    public int getDia() { return dia; }
    public int getMes() { return mes; }
    public int getAnyo() { return anyo; }
    public String getAula() { return aula; }

    public Reserva(int id, int hora, int dia, int mes, int anyo, int id_aula, String aula, int id_profesor, String profesor) {
        super();
        this.id = id;
        this.hora = hora;
        this.dia = dia;
        this.mes = mes;
        this.anyo = anyo;
        this.id_aula = id_aula;
        this.aula = aula;
        this.id_profesor = id_profesor;
        this.profesor = profesor;
    }
    public Reserva(int hora, int dia, int mes, int anyo, int id_aula, String aula, int id_profesor, String profesor) {
        super();
        this.hora = hora;
        this.dia = dia;
        this.mes = mes;
        this.anyo = anyo;
        this.id_aula = id_aula;
        this.aula = aula;
        this.id_profesor = id_profesor;
        this.profesor = profesor;
    }

    @Override
    public String toString() {
        return  "\nAula reservada: " + aula + " (" + id_aula + ")" + "\nDía: " + dia + "/" + mes + "/" + anyo + "   -->   " + hora + "º hora\nProfesor: " + profesor + "\n";
    }
}

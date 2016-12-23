package com.javier.jmreservas;

import java.io.Serializable;
import java.util.ArrayList;

public class Resultado implements Serializable {
    boolean code;
    int status;
    String message;
    ArrayList<Profesor> profesores;
    ArrayList<Aula> aulas;
    ArrayList<Reserva> reservas;
    ArrayList<ProfesorAdmin> profesoresAdmin;

    public boolean getCode() {return code;}
}

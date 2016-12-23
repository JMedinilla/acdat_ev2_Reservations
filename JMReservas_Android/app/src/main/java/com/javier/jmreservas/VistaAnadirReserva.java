package com.javier.jmreservas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Calendar;
import cz.msebera.android.httpclient.Header;

public class VistaAnadirReserva extends AppCompatActivity {

    //DIRECCION
    String URLANADIR = "https://jvmed.pw/reservas/api/add";

    //CONTROLES
    RadioButton radio1;
    RadioButton radio2;
    RadioButton radio3;
    RadioButton radio4;
    RadioButton radio5;
    RadioButton radio6;
    Button btnReservar;
    EditText tbxAnadirAula;
    DatePicker selecFecha;

    //OBJETOS
    Calendar cal;
    Calendar c;

    //Parámetros para la URL
    int idUsuario = 0; //profesor
    int numeroAula = 0; //aula
    int anyoP = 2016; //anyo
    int mesP = 0; //mes
    int diaP = 0; //dia
    int horaP = 0; //hora

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_anadir_reserva);

        //Se obtiene el ID del usuario conectado
        //--
        Intent i = getIntent();
        idUsuario = i.getIntExtra("iduserlog", 0);
        //--

        //Se instancian los controles
        radio1 = (RadioButton)findViewById(R.id.radio1);
        radio2 = (RadioButton)findViewById(R.id.radio2);
        radio3 = (RadioButton)findViewById(R.id.radio3);
        radio4 = (RadioButton)findViewById(R.id.radio4);
        radio5 = (RadioButton)findViewById(R.id.radio5);
        radio6 = (RadioButton)findViewById(R.id.radio6);
        selecFecha = (DatePicker)findViewById(R.id.selecFecha);

        //Se instancian los objetos de tipo calendario
        cal = Calendar.getInstance();
        c = Calendar.getInstance();

        //Cuando se pulsa el botón, se proceden a las comprobaciones y llamadas
        btnReservar = (Button)findViewById(R.id.btnReservar);
        btnReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Solo se permite hacer una reserva cuando se ha escrito un número de aula
                if (tbxAnadirAula.getText().toString().length() == 0) {

                    Toast.makeText(getApplicationContext(), "Tienes que introducir un número de aula", Toast.LENGTH_LONG).show();

                } else {

                    //Si la fecha seleccionada es posterior al día actual, dentro del mismo año, se continúa
                    //Para las comprobaciones se usa
                    if ((selecFecha.getYear() == c.get(Calendar.YEAR) && selecFecha.getMonth() == c.get(Calendar.MONTH) && selecFecha.getDayOfMonth() > c.get(Calendar.DAY_OF_MONTH))
                            ||
                            (selecFecha.getYear() == c.get(Calendar.YEAR) && selecFecha.getMonth() > c.get(Calendar.MONTH))) {

                        //Se modifica el primer objeto de tipo Calendar para que sea igual a la
                        //fecha seleccionada en el objeto de tipo DatePicker
                        cal.set(selecFecha.getYear(), selecFecha.getMonth(), selecFecha.getDayOfMonth());

                        //Se comprueba si la fecha seleccionada cae en sábado o en domingo
                        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                            Toast.makeText(getApplicationContext(), "Los fines de semana no hay clase, chiquillo", Toast.LENGTH_SHORT).show();
                        }
                        //En caso de ser día lectivo, se continúa
                        else {
                            //Se comprueba si el día seleccionado está dentro de los 30 días posteriores
                            //al día actual para continuar
                            if (es30Antes()) {

                                mesP = selecFecha.getMonth();
                                diaP = selecFecha.getDayOfMonth();

                                if (radio1.isChecked()) { horaP = 1; }
                                if (radio2.isChecked()) { horaP = 2; }
                                if (radio3.isChecked()) { horaP = 3; }
                                if (radio4.isChecked()) { horaP = 4; }
                                if (radio5.isChecked()) { horaP = 5; }
                                if (radio6.isChecked()) { horaP = 6; }

                                //Se comprueba con el método de días válidos, si la fecha elegida no es superior al día 24 de junio
                                if (comprobarMesDia(mesP, diaP) == 1) {

                                    //Se comprueba si el día seleccionado es lectivo
                                    if (esVacaciones(mesP, diaP) == 0) {

                                        //Después de todas las comprobaciones, se
                                        //procede a añadir la reserva
                                        anadirReserva();

                                    }
                                    //En caso contrario, se impide la reserva
                                    else {
                                        Toast.makeText(getApplicationContext(), "El día seleccionado cae en Semana Blanca, Semana Santa o en día de Andalucía", Toast.LENGTH_LONG).show();
                                    }
                                }
                                //En caso contrario, se impide la reserva
                                else {
                                    Toast.makeText(getApplicationContext(), "Solo se puede reservar hasta el 24 de junio", Toast.LENGTH_SHORT).show();
                                }

                            }
                            //En caso contrario, se impide la reserva
                            else {
                                Toast.makeText(getApplicationContext(), "Solo se puede reservar con un máximo de 30 días de antelación", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                    //Si el año seleccionado es mayor al actual, no se permite la reserva
                    else if (selecFecha.getYear() > c.get(Calendar.YEAR)) {
                        Toast.makeText(getApplicationContext(), "No se puede reservar para un año distinto del actual", Toast.LENGTH_SHORT).show();

                    }
                    //Si la fecha seleccionada es el día actual o anterior, no se permite la reserva
                    else {
                        Toast.makeText(getApplicationContext(), "No se puede añadir una reserva para hoy o un día anterior", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        //Cada vez que se escribe algo, se asigna el valor escrito al parámetro que indica el aula
        tbxAnadirAula = (EditText)findViewById(R.id.tbxAnadirAula);
        tbxAnadirAula.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NADA
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count >= 1) {
                    numeroAula = Integer.parseInt(tbxAnadirAula.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //NADA
            }
        });
    }

    //Método que se conecta a la URL para añadir la reserva
    private void anadirReserva() {
        RequestParams params = new RequestParams();
        params.put("hora", horaP);
        params.put("dia", diaP);
        params.put("mes", mesP + 1);
        params.put("anyo", anyoP);
        params.put("aula", numeroAula);
        params.put("profesor", idUsuario);

        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());

        client.post(URLANADIR, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Añadiendo . . .");
                progreso.setCancelable(false);
                progreso.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progreso.dismiss();
                Resultado result;
                Gson gson = new Gson();

                result = gson.fromJson(String.valueOf(response), Resultado.class);

                if (result != null) {
                    if (result.getCode()) {
                        Toast.makeText(getApplicationContext(), "Se ha añadido la reserva", Toast.LENGTH_SHORT).show();
                        //Cuando hace la reserva se termina la actividad
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                progreso.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progreso.dismiss();

                //Cuando se entra en este onFailure, la conexión no ha fallado, sino que ha devuelto
                //el código 409, establecido para cuando ya existe una reserva con los mismos valores
                //o el aula que se ha elegido no existe
                Toast.makeText(getApplicationContext(), "No se ha podido realizar esta reserva", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Comprueba que no existe una reserva a esta hora y que el número de aula es correcto", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progreso.dismiss();
            }
        });
    }

    //Método que indica, para un mes y un día, si es Semana Santa,
    //Semana Blanca o el día de Andalucía
    private int esVacaciones(int mesPasado, int diaPasado) {
        //0 - Lectivo
        //1 - Blanca
        //2 - Santa
        //3 - Andalucía
        int res = 0;

        if (mesPasado == 1 && (diaPasado == 22 | diaPasado == 23 | diaPasado == 24 | diaPasado == 25 | diaPasado == 26 | diaPasado == 27 | diaPasado == 28 | diaPasado == 28))
            res = 1;
        if (mesPasado == 1 && diaPasado == 29)
            res = 3;
        if (mesPasado == 2 && (diaPasado == 21 | diaPasado == 22 | diaPasado == 23 | diaPasado == 24 | diaPasado == 25 | diaPasado == 26 | diaPasado == 27 | diaPasado == 28))
            res = 2;

        return res;
    }

    //Método que indica si una fecha, dada el día actual, está dentro
    //de los límites de 30 días de antelación para poder hacer una reserva
    private boolean es30Antes() {
        Calendar diaHoy = Calendar.getInstance();

        Calendar diaElegido = Calendar.getInstance();
        diaElegido.set(selecFecha.getYear(), selecFecha.getMonth(), selecFecha.getDayOfMonth());

        long diferencia = diaElegido.getTimeInMillis() - diaHoy.getTimeInMillis();
        int diferenciaDias = (int)(diferencia / (1000*60*60*24));

        return diferenciaDias < 30;
    }

    //Método que comprueba si el día elegido es válido, poniendo como máximo
    //el día 24 de junio para poder realizar reservas
    private int comprobarMesDia(int mesPasado, int diaPasado) {
        int res;

        if (mesPasado >= 0 && mesPasado < 6) {

            if (mesPasado == 0 && (diaPasado > 0 && diaPasado < 31)) { res = 1; }
            else if (mesPasado == 1 && (diaPasado > 0 && diaPasado < 30)) { res = 1; }
            else if (mesPasado == 2 && (diaPasado > 0 && diaPasado <= 31)) { res = 1; }
            else if (mesPasado == 3 && (diaPasado > 0 && diaPasado <= 30)) { res = 1; }
            else if (mesPasado == 4 && (diaPasado > 0 && diaPasado <= 31)) { res = 1; }
            else if (mesPasado == 5 && (diaPasado > 0 && diaPasado < 25)) { res = 1; }
            else {
                res = 0;
            }
        }
        else {
            res = 0;
        }

        return res;
    }
}

package com.javier.jmreservas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import org.json.JSONObject;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

public class VistaReservasEnAula extends AppCompatActivity {

    //DIRECCION
    String URLAULRES = "https://jvmed.pw/reservas/api/aulas/";

    //CONTROLES
    ListView listaReservasEnAula;

    //OBJETOS
    ArrayList<Reserva> listaDeReservasEnAula;
    ArrayAdapter adapterDeReservasEnAula;

    //CAMPOS
    int mesReserva = 0;
    int diaReserva = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_reservas_en_aula);

        //Se obtienen el número de aula, es y día introducidos para añadirlos a la URL
        //--
        Intent in = getIntent();
        String numeroAula = in.getStringExtra("numeroaula");
        mesReserva = in.getIntExtra("mesintroducido", 0);
        diaReserva = in.getIntExtra("diaintroducido", 0);
        //--

        //Se montan las direcciones en base a los números recibidos
        if (mesReserva == 0) {
            URLAULRES = URLAULRES + numeroAula + "/res";
        }
        else if (mesReserva != 0 && diaReserva == 0) {
            URLAULRES = URLAULRES + numeroAula + "/res/m/" + mesReserva;
        }
        else if (mesReserva != 0 && diaReserva != 0) {
            URLAULRES = URLAULRES + numeroAula + "/res/m/" + mesReserva + "/d/" + diaReserva;
        }

        listaReservasEnAula = (ListView)findViewById(R.id.listaReservasEnAula);

        listaDeReservasEnAula = new ArrayList<>();
        adapterDeReservasEnAula = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDeReservasEnAula);
        listaReservasEnAula.setAdapter(adapterDeReservasEnAula);

        //Se rellena la lista con las reservas que cumplen las condiciones
        obtenerReservas();
    }

    //Método que conecta con la URL para obtener las reservas que cumplan con las condiciones
    private void obtenerReservas() {
        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());

        client.get(URLAULRES, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();

                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Obteniendo datos . . .");
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

                        listaDeReservasEnAula.clear();
                        listaDeReservasEnAula.addAll(result.reservas);
                        adapterDeReservasEnAula.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progreso.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progreso.dismiss();
            }
        });
    }
}

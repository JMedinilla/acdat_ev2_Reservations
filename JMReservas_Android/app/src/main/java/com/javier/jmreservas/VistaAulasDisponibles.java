package com.javier.jmreservas;

import android.app.ProgressDialog;
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

public class VistaAulasDisponibles extends AppCompatActivity {

    //DIREECCION
    String URLAULDIS = "https://jvmed.pw/reservas/api/aulas";

    //CONTROLES
    ListView listaAulas;

    //OBJETOS
    ArrayList<Aula> listaDeAulas;
    ArrayAdapter adapterDeAulas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_aulas_disponibles);

        listaAulas = (ListView)findViewById(R.id.listaAulas);

        listaDeAulas = new ArrayList<>();
        adapterDeAulas = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDeAulas);
        listaAulas.setAdapter(adapterDeAulas);

        //Se llama al método directamente
        obtenerAulas();
    }

    //Método que conecta con la URL para obtener todas las aulas
    //que hay registradas en la base de datos
    private void obtenerAulas() {
        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());

        client.get(URLAULDIS, new JsonHttpResponseHandler() {
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

                        //See obtienen todas las aulas registradas en la base de datos
                        listaDeAulas.clear();
                        listaDeAulas.addAll(result.aulas);
                        adapterDeAulas.notifyDataSetChanged();
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

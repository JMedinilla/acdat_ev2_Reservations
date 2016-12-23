package com.javier.jmreservas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class VistaReservas extends AppCompatActivity {

    //DIRECCIONES
    String URLAULDIS = "https://jvmed.pw/reservas/api/res/profesor/";
    String URLDEL = "https://jvmed.pw/reservas/api/eliminar/";

    //CONTROLES
    CheckBox chEliminar;
    ListView listaReservas;

    //OBJETOS
    ArrayList<Reserva> listaDeReservas;
    ArrayAdapter adapterDeReservas;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_reservas);

        //Se obtiene el ID del profesor conectado para montar la dirección
        //--
        Intent in = getIntent();
        int idprofesor = in.getIntExtra("idprofesor", 0);
        URLAULDIS = URLAULDIS + idprofesor;
        //--

        //Se instancian los controles
        listaReservas = (ListView)findViewById(R.id.listaReservas);
        chEliminar = (CheckBox)findViewById(R.id.chEliminar);

        listaDeReservas = new ArrayList<>();
        adapterDeReservas = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDeReservas);
        listaReservas.setAdapter(adapterDeReservas);

        //Se rellena la lista con sus reservas directamente
        obtenerReservas();

        listaReservas.setLongClickable(true);
        listaReservas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Si el CheckBox de eliminar está marcado, en el click largo de cada elemento se
                //llamará a la confirmación de la eliminación del elemento pulsado
                if (chEliminar.isChecked()) {
                    confirmar(listaDeReservas.get(position));
                }
                return true;
            }
        });
    }

    //Método que conecta con la URL para obtener las reservas del usuario conectado
    private void obtenerReservas() {
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

                        listaDeReservas.clear();
                        listaDeReservas.addAll(result.reservas);
                        adapterDeReservas.notifyDataSetChanged();
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

    //Método de confirmación para eliminar una reserva
    private void confirmar(Reserva rese) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Reserva res = rese;
        builder.setMessage("¿Seguro que quieres eliminar esta reserva?\n\n"
                + res.getAula() + "\n" + res.getHora()
                + "º hora\n" + res.getDia() + "/" + res.getMes() + "/" + res.getAnyo())

                .setTitle("Eliminar reserva")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        //Se pasa el ID de la reserva al método que realiza las eliminaciones
                        eliminarReserva(res.getId());
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    //Método que conecta con la URL para eliminar una reserva en base a su ID
    private void eliminarReserva(int idReser) {
        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.delete(URLDEL + idReser, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Eliminando . . .");
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
                        Toast.makeText(getApplicationContext(), "Reserva eliminada", Toast.LENGTH_SHORT).show();
                        chEliminar.setChecked(false);
                        obtenerReservas();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progreso.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progreso.dismiss();
            }
        });
    }
}

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

public class VistaEnviarCorreo extends AppCompatActivity {

    //DIRECCIONES
    String URLCOR = "https://jvmed.pw/reservas/api/profesores/admin/";
    String URLENVIAR = "https://jvmed.pw/reservas/api/mail";

    //CONTROLES
    EditText tbxAsunto;
    EditText tbxMensaje;
    EditText tbxContra;
    ListView listaCorreos;

    //OBJETOS
    ArrayList<ProfesorAdmin> listaDeCorreos;
    ArrayAdapter adapterDeCorreos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_enviar_correo);

        //Se recibe el ID y el correo del administrador conectado
        //--
        Intent in = getIntent();
        int numero = in.getIntExtra("idcorreo", 0);
        final String correoFrom = in.getStringExtra("correofrom");
        //Se monta una de las direcciones
        URLCOR = URLCOR + numero;
        //--

        //Se instancian los controles
        listaCorreos = (ListView)findViewById(R.id.listaCorreos);
        tbxAsunto = (EditText)findViewById(R.id.tbxCorreoAsunto);
        tbxMensaje = (EditText)findViewById(R.id.tbxCorreoMensaje);
        tbxContra = (EditText)findViewById(R.id.tbxCorreoContra);

        listaDeCorreos = new ArrayList<>();
        adapterDeCorreos = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDeCorreos);
        listaCorreos.setAdapter(adapterDeCorreos);

        //Se muestra directamente la lista de profesores a los que enviar un correo
        obtenerCorreos();

        listaCorreos.setLongClickable(true);
        listaCorreos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //Se obliga a escribir un título
                if (tbxAsunto.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Debes introducir un título", Toast.LENGTH_SHORT).show();
                    return false;
                }
                //Se obliga a escribir un mensaje
                else if (tbxMensaje.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Debes introducir un mensaje", Toast.LENGTH_SHORT).show();
                    return false;
                }
                //Se debe escribir la contraseña del correo asociado a la cuenta del usuario conectado
                else if (tbxContra.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Debes introducir la contraseña de tu correo electrónico", Toast.LENGTH_LONG).show();
                    return false;
                }
                //Si se han introducido los tres campos, se continúa
                else {
                    //Se crean las cadenas que se van a usar como paráemtros
                    String to = listaDeCorreos.get(position).getEmail();
                    String subject = tbxAsunto.getText().toString();
                    String message = tbxMensaje.getText().toString();
                    String password = tbxContra.getText().toString();

                    //Se crea una instancia de la clase Email, que se proporcionará para enviar los correos
                    Email email = new Email(correoFrom, password, to, subject, message);
                    confirmar(email);
                }

                return true;
            }
        });
    }

    //Cuando de alerta para confirmar la eliminación
    private void confirmar(final Email email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro que quieres enviar el correo a esta dirección?\n\n" + email.getTo())
                .setTitle("Enviar correo")
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        //Si se acepta, se envía el correo
                        enviarCorreo(email);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    //Método que conecta con la URL para enviar un correo a otro profesor
    private void enviarCorreo(Email email) {

        //Parámetros necesarios
        RequestParams params = new RequestParams();
        params.put("from", email.getFrom());
        params.put("password", email.getPassword());
        params.put("to", email.getTo());
        params.put("subject", email.getSubject());
        params.put("message", email.getMessage());

        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());

        client.post(URLENVIAR, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Enviando . . .");
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
                        Toast.makeText(getApplicationContext(), "Correo enviado correctamente", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "El envio ha fallado. Comprueba la contraseña", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "El envío ha fallado. Comprueba la contraseña", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                progreso.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progreso.dismiss();
            }
        });
    }

    //Método que conecta con la URL que obtiene los profesores y sus correos
    private void obtenerCorreos() {
        final ProgressDialog progreso = new ProgressDialog(this);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());

        client.get(URLCOR, new JsonHttpResponseHandler() {
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

                        listaDeCorreos.clear();
                        listaDeCorreos.addAll(result.profesoresAdmin);
                        adapterDeCorreos.notifyDataSetChanged();
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

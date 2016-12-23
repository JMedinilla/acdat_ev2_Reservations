package com.javier.jmreservas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    //CONTROLES
    EditText tbxUser;
    EditText tbxContra;
    Button btnConectar;

    //DIRECCIONES
    String UR = "https://jvmed.pw/reservas/api/login/";
    String URLLOGIN = "";

    //CAMPOS
    String usuario;
    String contrasenya;
    boolean usuarioValido;

    //OBJETOS
    ArrayList<Profesor> listaProfesores;
    Profesor prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuarioValido = false;
        usuario = "";
        contrasenya = "";

        //Se instancian los controles
        tbxUser = (EditText)findViewById(R.id.tbxUser);
        tbxContra = (EditText)findViewById(R.id.tbxContra);
        btnConectar = (Button)findViewById(R.id.btnConectar);
        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se obtienen las cadenas de los controles y se encripta la contraseña
                usuario = tbxUser.getText().toString();
                contrasenya = tbxContra.getText().toString();
                String encriptado = md5(contrasenya);

                //Se construye la URL a la que hay que consultar
                URLLOGIN = UR + usuario;
                obtenerUsuario(encriptado);
            }
        });
    }

    //Método que conectad con la base de datos para comprobar
    //la existencia del usuario introducido
    private void obtenerUsuario(final String encriptado) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.get(URLLOGIN, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Resultado result;
                Gson gson = new Gson();

                result = gson.fromJson(String.valueOf(response), Resultado.class);
                if (result != null) {
                    if (result.getCode()) {

                        //Se obtiene el profesor de la base de datos
                        listaProfesores = new ArrayList<>();
                        listaProfesores.clear();
                        listaProfesores.addAll(result.profesores);

                        //Se compara la cadena introducida encriptada con el campo en la base de datos
                        String lgn = listaProfesores.get(0).getContrasenya();
                        if (lgn.equals(encriptado)) {
                            usuarioValido = true;
                        }
                    }
                }

                //Si el usuario es válido, se abre la nueva vista, a la que se le envía
                //toda la información del profesor que se ha conectado, para obtener su ID
                if (usuarioValido) {
                    Toast.makeText(getApplicationContext(), "Identificación correcta", Toast.LENGTH_SHORT).show();

                    //--
                    Intent in = new Intent(getApplicationContext(), VistaPrincipal.class);
                    prf = listaProfesores.get(0);
                    in.putExtra("profesorLogeado", prf);
                    //--

                    startActivity(in);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Identificación errónea", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //NADA
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //NADA
            }
        });
    }

    //Método que devuelve la cadena correspondiente al MD5 de un texto
    //Gracias Stackoverflow
    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());

            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();

            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);

                while (h.length() < 2)
                    h = "0" + h;

                hexString.append(h);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }
}

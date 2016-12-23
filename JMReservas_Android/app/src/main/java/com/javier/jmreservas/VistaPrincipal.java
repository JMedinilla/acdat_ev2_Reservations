package com.javier.jmreservas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VistaPrincipal extends AppCompatActivity implements View.OnClickListener {

    //CONTROLES
    TextView txtNombreProfesor;
    EditText tbxVerReservasAula;
    EditText tbxVerReservasAulaMes;
    EditText tbxVerReservasAulaDia;
    Button btnVerAulasDisponibles;
    Button btnVerReservas;
    Button btnVerReservasAula;
    Button btnAnadirReserva;
    Button btnEnviarEmail;

    //OBJETOS
    Profesor profesorLogeado;

    //CAMPOS
    int mesPasado;
    int diaPasado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_principal);

        mesPasado = 0;
        diaPasado = 0;

        //Profesor que se ha conectado
        Intent in = getIntent();
        profesorLogeado = (Profesor) in.getSerializableExtra("profesorLogeado");

        //----------------------------------------------------------
        //Se instancian los controles
        iniciarControles();
        //----------------------------------------------------------

        //
    }

    private void iniciarControles() {
        txtNombreProfesor = (TextView)findViewById(R.id.txtNombreProfesor);
        txtNombreProfesor.setOnClickListener(this);
        String texto = profesorLogeado.getNombre() + " " + profesorLogeado.getApellido();
        txtNombreProfesor.setText(texto);

        //Solo se permiten ver las reservas de un aula cuando se ha escrito un núemero
        tbxVerReservasAula = (EditText)findViewById(R.id.tbxVerReservasAula);
        tbxVerReservasAula.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NADA
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    btnVerReservasAula.setEnabled(false);
                } else {
                    btnVerReservasAula.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //NADA
            }
        });

        //Solo se permite escribir un día cuando hay ya un mes escrito
        tbxVerReservasAulaMes = (EditText)findViewById(R.id.tbxVerReservasAulaMes);
        tbxVerReservasAulaMes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NADA
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    tbxVerReservasAulaDia.setText("");
                    tbxVerReservasAulaDia.setEnabled(false);
                }
                else if (Integer.parseInt(tbxVerReservasAulaMes.getText().toString()) == 0) {
                    tbxVerReservasAulaDia.setText("");
                    tbxVerReservasAulaDia.setEnabled(false);
                }
                else {
                    tbxVerReservasAulaDia.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //NADA
            }
        });
        tbxVerReservasAulaDia = (EditText)findViewById(R.id.tbxVerReservasAulaDia);
        tbxVerReservasAulaDia.setEnabled(false);

        btnVerAulasDisponibles = (Button)findViewById(R.id.btnVerAulasDisponibles);
        btnVerAulasDisponibles.setOnClickListener(this);

        btnVerReservas = (Button)findViewById(R.id.btnVerReservas);
        btnVerReservas.setOnClickListener(this);

        btnVerReservasAula = (Button)findViewById(R.id.btnVerReservasAula);
        btnVerReservasAula.setOnClickListener(this);
        btnVerReservasAula.setEnabled(false);

        btnAnadirReserva = (Button)findViewById(R.id.btnAnadirReserva);
        btnAnadirReserva.setOnClickListener(this);

        btnEnviarEmail = (Button)findViewById(R.id.btnEnviarEmail);
        btnEnviarEmail.setOnClickListener(this);

        //Se comprueba el campo de la base de datos de cada profesor que indica su rango
        //Solo se permiten los correos si el usuario es administrador, teniendo un 1 en tal campo
        if (profesorLogeado.getValido() == 2) {
            String texto2 = "Correos no disponibles";
            btnEnviarEmail.setText(texto2);
            btnEnviarEmail.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnVerAulasDisponibles:
                Intent in_uno = new Intent(getApplicationContext(), VistaAulasDisponibles.class);
                startActivity(in_uno);
                break;

            case R.id.btnVerReservas:
                Intent in_dos = new Intent(getApplicationContext(), VistaReservas.class);
                in_dos.putExtra("idprofesor", profesorLogeado.getId());
                startActivity(in_dos);
                break;

            case R.id.btnVerReservasAula:
                String numeroAula = tbxVerReservasAula.getText().toString();
                Intent in_tres = new Intent(getApplicationContext(), VistaReservasEnAula.class);

                //Se comprueba la validez del día indicado
                int res = comprobarMesDia();
                if (res == 1) {
                    Toast.makeText(getApplicationContext(), "La fecha introducida es inválida. Escribe un 0 para que se muestre todo", Toast.LENGTH_LONG).show();
                }
                else {
                    if (res == 2) {
                        Toast.makeText(getApplicationContext(), "El mes es correcto, pero el día no. Se muestran todas las reservas del mes", Toast.LENGTH_LONG).show();
                    }
                    else if (res == 3) {
                        Toast.makeText(getApplicationContext(), "Se muestran todas las reservas", Toast.LENGTH_SHORT).show();
                    }
                    else if (res == 4) {

                        //Se comprueban los días de fiesta
                        if (esVacaciones() == 1) {
                            Toast.makeText(getApplicationContext(), "El día indicado cae en semana blanca", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (esVacaciones() == 2) {
                            Toast.makeText(getApplicationContext(), "El día indicado cae en semana santa", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (esVacaciones() == 3) {
                            Toast.makeText(getApplicationContext(), "El día indicado es fiesta debido al día de Andalucía", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    in_tres.putExtra("numeroaula", numeroAula);
                    in_tres.putExtra("mesintroducido", mesPasado);
                    in_tres.putExtra("diaintroducido", diaPasado);

                    startActivity(in_tres);
                }
                break;

            case R.id.btnAnadirReserva:
                Intent in_cuatro = new Intent(getApplicationContext(), VistaAnadirReserva.class);
                in_cuatro.putExtra("iduserlog", profesorLogeado.getId());
                startActivity(in_cuatro);
                break;

            case R.id.btnEnviarEmail:
                Intent in_cinco = new Intent(getApplicationContext(), VistaEnviarCorreo.class);
                in_cinco.putExtra("idcorreo", profesorLogeado.getId());
                in_cinco.putExtra("correofrom", profesorLogeado.getEmail());
                startActivity(in_cinco);
                break;

        }
    }

    //Método que comprueba si el día introducido cae en vacaciones
    private int esVacaciones() {
        //0 - Lectivo
        //1 - Blanca
        //2 - Santa
        //3 - Andalucía
        int res = 0;

        if (mesPasado == 2 && (diaPasado == 22 | diaPasado == 23 | diaPasado == 24 | diaPasado == 25 | diaPasado == 26 | diaPasado == 27 | diaPasado == 28 | diaPasado == 28))
            res = 1;
        if (mesPasado == 2 && diaPasado == 29)
            res = 3;
        if (mesPasado == 3 && (diaPasado == 21 | diaPasado == 22 | diaPasado == 23 | diaPasado == 24 | diaPasado == 25 | diaPasado == 26 | diaPasado == 27 | diaPasado == 28))
            res = 2;

        return res;
    }

    //Método que comprueba si el día escrito es válido, entre el 1 de enero y el 24 de junio
    private int comprobarMesDia() {

        //res = 1 -> Mes es inválido, no se muestra ninguna reserva
        //res = 2 -> Mes es X, pero día inválido, se muestran las reservas del mes
        //res = 3 -> Mes es 0, se muestran todas las reservas
        //res = 4 -> Mes es X, día es X, se muestran las reservas correspondientes

        int res;

        if (tbxVerReservasAulaMes.getText().toString().length() == 0)
            mesPasado = 0;
        else
            mesPasado = Integer.parseInt(tbxVerReservasAulaMes.getText().toString());

        if (tbxVerReservasAulaDia.getText().toString().length() == 0 | !tbxVerReservasAulaDia.isEnabled())
            diaPasado = 0;
        else
            diaPasado = Integer.parseInt(tbxVerReservasAulaDia.getText().toString());

        //-------------------------------------------------------------------------------

        if (mesPasado > 0 && mesPasado < 7) {

            if (mesPasado == 1 && (diaPasado > 0 && diaPasado < 31)) { res = 4; }
            else if (mesPasado == 2 && (diaPasado > 0 && diaPasado < 30)) { res = 4; }
            else if (mesPasado == 3 && (diaPasado > 0 && diaPasado < 31)) { res = 4; }
            else if (mesPasado == 4 && (diaPasado > 0 && diaPasado < 30)) { res = 4; }
            else if (mesPasado == 5 && (diaPasado > 0 && diaPasado < 31)) { res = 4; }
            else if (mesPasado == 6 && (diaPasado > 0 && diaPasado < 25)) { res = 4; }
            else {
                diaPasado = 0;
                res = 2;
            }

        }
        else if (mesPasado == 0) {
            diaPasado = 0;
            res = 3;
        }
        else {
            mesPasado = 0;
            diaPasado = 0;
            res = 1;
        }

        return res;
    }
}

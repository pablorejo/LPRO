package com.example.pruebasql.calendario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.R;
import com.example.pruebasql.Server;
import com.example.pruebasql.bbdd.vacas.Enfermedad;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEnfermedad extends BarraSuperior {

    private Button btnInicio, btnFin, btnGuardar, btnCancelar;
    private TextView textNombreEnfermedad, textFechaInicio, textFechaFin, textNumeroPendiente, textPeriocidad, textMedicamento;

    private Server server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_enfermedad);
        configureToolbar();
        server = new Server(this);
        String localDate = getIntent().getStringExtra("fecha");
        
        if (localDate == null){
            LocalDate date = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            localDate = date.format(formatter);
        }


        btnInicio = findViewById(R.id.btnFechaInicio);
        btnFin = findViewById(R.id.btnFechaFin);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        textFechaInicio = findViewById(R.id.textFechaInicio);
        textFechaInicio.setText(localDate);

        textFechaFin = findViewById(R.id.textFechaFin);
        textFechaFin.setText(localDate);

        textNumeroPendiente = findViewById(R.id.editTextNumeroPendiente);
        textPeriocidad = findViewById(R.id.editTextPeriocidad);
        textMedicamento = findViewById(R.id.editTextMedicamento);
        textNombreEnfermedad = findViewById(R.id.editTextNombreEnfermedad);

        textNombreEnfermedad.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textNombreEnfermedad.setText(""); // Esto borrará el texto cuando el EditText gane el foco
                }
            }
        });

        btnInicio.setOnClickListener(v -> {
            openDialog(textFechaInicio);
        });

        btnFin.setOnClickListener(v -> {
            openDialog(textFechaFin);
        });

        btnGuardar.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            try {
                Date fechaInicio = sdf.parse(textFechaInicio.getText().toString());
                Date fechaFin = sdf.parse(textFechaFin.getText().toString());
                Enfermedad enfermedad = new Enfermedad(
                        0,
                        Integer.parseInt(textNumeroPendiente.getText().toString()),
                        textMedicamento.getText().toString(),
                        textNombreEnfermedad.getText().toString(),
                        fechaInicio,
                        fechaFin,
                        Integer.parseInt(textPeriocidad.getText().toString())
                );
                Toast.makeText(this,"Creado con exito" , Toast.LENGTH_LONG).show();
                getOnBackPressedDispatcher();
            }catch (ParseException e){
                Toast.makeText(this,e.toString() , Toast.LENGTH_LONG).show();
            }
        });

        btnCancelar.setOnClickListener(v -> {
            Toast.makeText(this,"Crear enfermedad cancelado" , Toast.LENGTH_LONG).show();
            getOnBackPressedDispatcher();
        });
    }

    private void openDialog(TextView text){

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                text.setText(String.valueOf(year)+"/"+String.valueOf(month)+"/"+String.valueOf(dayOfMonth));
            }
        },2024,4,9);

        dialog.show();
    }
}
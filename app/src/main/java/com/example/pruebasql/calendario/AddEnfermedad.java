package com.example.pruebasql.calendario;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.DataManager;
import com.example.pruebasql.R;
import com.example.pruebasql.Server;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.vacas.Enfermedad;
import com.example.pruebasql.bbdd.vacas.Vaca;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEnfermedad extends BarraSuperior {

    private Button btnInicio, btnFin, btnGuardar, btnCancelar;
    private TextView textNombreEnfermedad, textFechaInicio, textFechaFin, textPeriocidad, textMedicamento;

    private EditText editTextNotaEnfermedad;
    private AutoCompleteTextView editTextNumeroPendienteEnfermedad;
    private Server server;

    private Enfermedad enfermedad = null;

    private boolean nueva = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_enfermedad);
        configureToolbar();
        String localDate = getIntent().getStringExtra("fecha");
        String strId = getIntent().getStringExtra("id");

        if (strId != null){
            int id = Integer.valueOf(strId);
            for (Vaca vaca: usuario.getVacas()){
                for (Enfermedad enfermedad1: vaca.getEnfermedades()){
                    if (enfermedad1.getId_enfermedad_vaca() == id){
                        enfermedad = enfermedad1;
                        break;
                    }
                }
                if (enfermedad != null){
                    break;
                }
            }
            nueva = false;
        }else{
            nueva = true;
        }

        Usuario usuario = DataManager.getInstance().getUsuario();

        server = new Server(this,usuario);

        if (localDate == null){
            LocalDate date = LocalDate.now();
            localDate = formatter.format(date);
        }

        btnInicio = findViewById(R.id.btnFechaParto);
        btnFin = findViewById(R.id.btnFechaFin);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        textFechaInicio = findViewById(R.id.textFechaParto);
        textFechaInicio.setText(localDate.toString());

        textFechaFin = findViewById(R.id.textFechaFin);
        textFechaFin.setText(localDate.toString());

        editTextNumeroPendienteEnfermedad = findViewById(R.id.editTextNumeroPendienteEnfermedad);
        editTextNumeroPendienteEnfermedad.setAdapter(usuario.getAdapterVacas(this));
        editTextNumeroPendienteEnfermedad.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextNumeroPendienteEnfermedad.setText(""); // Esto borrará el texto cuando el EditText gane el foco
                }
            }
        });

        textPeriocidad = findViewById(R.id.editTextPeriocidad);
        textMedicamento = findViewById(R.id.editTextMedicamento);
        textNombreEnfermedad = findViewById(R.id.editTextNombreEnfermedad);
        editTextNotaEnfermedad = findViewById(R.id.editTextNotaEnfermedad);
        if (enfermedad != null){
            textFechaInicio.setText(enfermedad.getFechaInicio().toString());
            textFechaFin.setText(enfermedad.getFechaFin().toString());
            editTextNumeroPendienteEnfermedad.setText(String.valueOf(enfermedad.getNumero_pendiente()));
            textPeriocidad.setText(String.valueOf(enfermedad.getPeriocidad_en_dias()));
            textMedicamento.setText(enfermedad.getMedicamento());
            textNombreEnfermedad.setText(enfermedad.getEnfermedad());
            editTextNotaEnfermedad.setText(enfermedad.getNota());
        }

        textFechaInicio.setOnClickListener(v -> {
            openDialog(textFechaInicio);
        });

        textFechaFin.setOnClickListener(v -> {
            openDialog(textFechaFin);
        });

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

            int numeroPendiente = Integer.parseInt(editTextNumeroPendienteEnfermedad.getText().toString());
            int id_enfermedad_vaca = 0;
            if (enfermedad != null){
                id_enfermedad_vaca = enfermedad.getId_enfermedad_vaca();
            }
            try {
                LocalDate fechaInicio = LocalDate.parse(textFechaInicio.getText().toString(),formatter);
                LocalDate fechaFin = LocalDate.parse(textFechaFin.getText().toString(),formatter);
                if (fechaFin.isAfter(fechaInicio)){
                    Enfermedad enfermedad1 = new Enfermedad(
                            id_enfermedad_vaca,
                            numeroPendiente,
                            textMedicamento.getText().toString(),
                            textNombreEnfermedad.getText().toString(),
                            fechaInicio,
                            fechaInicio,
                            Integer.parseInt(textPeriocidad.getText().toString()),
                            editTextNotaEnfermedad.getText().toString()
                    );
                    if (nueva){ // Guardamos la enfermedad
                        usuario.getVacaByNumeroPendiente(numeroPendiente).addEnfermedad(enfermedad1);
                        server.addEnfermedad(enfermedad1);
                    }else{ // Actualizamos la enfermedad
                        usuario.updateEnfermedad(enfermedad1);
                        server.updateEnfermedad(enfermedad1);
                    }
                    setResult(Activity.RESULT_OK);
                }else{
                    Toast.makeText(this, "La fecha fin no puede ser menor que la fecha de inicio", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_CANCELED);
                }
                finish();

            }catch (Exception e){
                Toast.makeText(this, "No se ha podido crear la enfermedad faltan datos", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(v -> {
            Toast.makeText(this,"Crear enfermedad cancelado" , Toast.LENGTH_LONG).show();
            setResult(Activity.RESULT_OK);
            finish();
        });
    }
}
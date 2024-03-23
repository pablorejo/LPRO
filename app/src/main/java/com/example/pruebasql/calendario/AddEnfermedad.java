package com.example.pruebasql.calendario;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.DataManager;
import com.example.pruebasql.R;
import com.example.pruebasql.Server;
import com.example.pruebasql.bbdd.Usuario;
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
        String localDate = getIntent().getStringExtra("fecha");

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
            int numeroPendiente = Integer.parseInt(textNumeroPendiente.getText().toString());
            Enfermedad enfermedad = new Enfermedad(
                    0,
                    numeroPendiente,
                    textMedicamento.getText().toString(),
                    textNombreEnfermedad.getText().toString(),
                    LocalDate.parse(textFechaInicio.getText().toString(),formatter),
                    LocalDate.parse(textFechaFin.getText().toString(),formatter),
                    Integer.parseInt(textPeriocidad.getText().toString()),
                    null
            );
            usuario.getVacaByNumeroPendiente(numeroPendiente).addEnfermedad(enfermedad);
            server.addEnfermedad(enfermedad);
            finish();
        });

        btnCancelar.setOnClickListener(v -> {
            Toast.makeText(this,"Crear enfermedad cancelado" , Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
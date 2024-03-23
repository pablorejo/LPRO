package com.example.pruebasql.calendario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.example.pruebasql.bbdd.vacas.Parto;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddParto extends BarraSuperior {

    private EditText NumeroPendiente;
    private Button btnFechaParto, btnGuardar, btnCancelar;

    private TextView fechaParto;
    private Server server;
    LocalDate date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parto);
        configureToolbar();
        String localDate = getIntent().getStringExtra("fecha");
        Usuario usuario = DataManager.getInstance().getUsuario();

        server = new Server(this,usuario);

        if (localDate == null){
            date = LocalDate.now();
            localDate = formatter.format(date);
        }else{
            date = LocalDate.parse(localDate,formatter);
        }

        NumeroPendiente = findViewById(R.id.editTextNumeroPendiente);
        btnFechaParto = findViewById(R.id.btnFechaParto);
        fechaParto = findViewById(R.id.textFechaParto);
        fechaParto.setText(localDate);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        btnFechaParto.setOnClickListener(v -> {
            openDialog(fechaParto);
        });

        btnGuardar.setOnClickListener(v -> {

            int numeroPendiente = Integer.valueOf(NumeroPendiente.getText().toString());
            Parto parto = new Parto(
                    0,
                    numeroPendiente,
                    LocalDate.parse(fechaParto.getText().toString(),formatter),
                    null
            );
            usuario.getVacaByNumeroPendiente(numeroPendiente).addParto(parto);
            server.addFechaParto(parto);
            setResult(RESULT_OK, null); // Establece RESULT_OK para indicar éxito
            finish();

        });

        btnCancelar.setOnClickListener(v -> {
            Toast.makeText(this,"Crear parto cancelado" , Toast.LENGTH_LONG).show();
            finish();
            setResult(RESULT_CANCELED, null);
        });
    }
}
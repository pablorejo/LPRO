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
import com.example.pruebasql.bbdd.vacas.Vaca;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddParto extends BarraSuperior {

    private EditText editTextNumeroPendiente, editTextNotaParto;
    private Button btnFechaParto, btnGuardar, btnCancelar;

    private TextView fechaParto;
    private Server server;
    LocalDate date;

    private Parto parto = null;

    private boolean editandoParto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parto);
        configureToolbar();
        String localDate = getIntent().getStringExtra("fecha");
        String strId = getIntent().getStringExtra("id"); // Si el id llega a null significa que estamos creando un nuevo Parto

        if (strId != null){
            int id = Integer.valueOf(strId);
            for (Vaca vaca: usuario.getVacas()){
                for (Parto parto1: vaca.getPartos()){
                    if (parto1.getId_vaca_parto() == id){
                        parto = parto1;
                        break;
                    }
                }
                if (parto != null){
                    break;
                }
            }
            editandoParto = false;
        }else{
            editandoParto = true;
        }

        Usuario usuario = DataManager.getInstance().getUsuario();
        server = new Server(this,usuario);

        if (localDate == null){
            date = LocalDate.now();
            localDate = formatter.format(date);
        }else{
            date = LocalDate.parse(localDate,formatter);
        }

        editTextNumeroPendiente = findViewById(R.id.editTextNumeroPendiente);
        btnFechaParto = findViewById(R.id.btnFechaParto);
        fechaParto = findViewById(R.id.textFechaParto);
        fechaParto.setText(localDate);
        editTextNotaParto = findViewById(R.id.editTextNotaParto);

        if (parto != null){
            editTextNumeroPendiente.setText(String.valueOf(parto.getNumeroPendiente()));
            fechaParto.setText(parto.getFechaParto().toString());
            editTextNotaParto.setText(parto.getNota());
        }
        fechaParto.setOnClickListener(v -> {
            openDialog(fechaParto);
        });

        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        btnFechaParto.setOnClickListener(v -> {
            openDialog(fechaParto);
        });

        btnGuardar.setOnClickListener(v -> {

            int numeroPendiente = Integer.valueOf(editTextNumeroPendiente.getText().toString());
            Parto parto = new Parto(
                    0,
                    numeroPendiente,
                    LocalDate.parse(fechaParto.getText().toString(),formatter),
                    editTextNotaParto.getText().toString()
            );
            usuario.getVacaByNumeroPendiente(numeroPendiente).addParto(parto);
            server.addFechaParto(parto);
            setResult(RESULT_OK, null); // Establece RESULT_OK para indicar éxito
            finish();
        });

        btnCancelar.setOnClickListener(v -> {
            Toast.makeText(this,"Crear parto cancelado" , Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
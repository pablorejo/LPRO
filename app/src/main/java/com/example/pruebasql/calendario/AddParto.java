package com.example.pruebasql.calendario;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebasql.BarraSuperior;
import com.example.pruebasql.DataManager;
import com.example.pruebasql.R;
import com.example.pruebasql.Server;
import com.example.pruebasql.bbdd.Usuario;
import com.example.pruebasql.bbdd.vacas.Parto;
import com.example.pruebasql.bbdd.vacas.Vaca;
import com.example.pruebasql.listeners.ServerCallback;

import org.threeten.bp.LocalDate;

public class AddParto extends BarraSuperior {

    private EditText editTextNotaParto;
    private AutoCompleteTextView editTextNumeroPendiente;
    private Button btnFechaParto, btnGuardar, btnCancelar, btnEliminarAddParto;

    private TextView fechaParto;
    private Server server;
    LocalDate date;

    private Parto parto = null;

    private boolean nueva = false;

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
            nueva = false;
        }else{
            nueva = true;
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
        editTextNumeroPendiente.setAdapter(usuario.getAdapterVacas(this));
        editTextNumeroPendiente.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextNumeroPendiente.setText(""); // Esto borrará el texto cuando el EditText gane el foco
                }
            }
        });

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
            int id_vaca_parto = 0;
            if (parto != null){
                id_vaca_parto = parto.getId_vaca_parto();
            }
            Parto parto = new Parto(
                    id_vaca_parto,
                    numeroPendiente,
                    LocalDate.parse(fechaParto.getText().toString(),formatter),
                    editTextNotaParto.getText().toString()
            );
            if (nueva){// Hay que guardar el parto
                server.addParto(parto, new ServerCallback() {
                    @Override
                    public void onResponse(Object response) {
                        setResult(RESULT_OK, null); // Establece RESULT_OK para indicar éxito
                        finish();
                    }
                });
            }else{// Hay que actualizarlo
                usuario.updateParto(parto);
                server.updateParto(parto);
                finish();
            }
        });

        btnCancelar.setOnClickListener(v -> {
            Toast.makeText(this,"Crear parto cancelado" , Toast.LENGTH_LONG).show();
            finish();
        });

        btnEliminarAddParto = findViewById(R.id.btnEliminarAddParto);
        if (nueva){ // Solo se debe mostrar si ya existe si estamos creando uno nuevo no hay que mostrar el boton eliminar
            btnEliminarAddParto.setVisibility(View.GONE);
        }

        btnEliminarAddParto.setOnClickListener(v -> {
            usuario.getVacaByNumeroPendiente(parto.getNumeroPendiente()).getPartos().remove(parto);
            server.deleteParto(parto);
            finish();
        });
    }
}
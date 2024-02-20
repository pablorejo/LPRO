package com.example.pruebasql;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PrincipalActivity extends AppCompatActivity {

    TextView btnCowList, btnCowFinder, btnContactar, btnAutomatizacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // Vinculamos variables con los controles del layout
        btnCowList=findViewById(R.id.idCowList);
        btnCowFinder=findViewById(R.id.idCowGPS);
        btnContactar=findViewById(R.id.btnLogin);
        btnAutomatizacion=findViewById(R.id.btn_activity_singUp);
    }
}
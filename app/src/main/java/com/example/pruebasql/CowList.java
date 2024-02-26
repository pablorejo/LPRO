package com.example.pruebasql;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CowList extends BarraSuperior {

    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_list);

        linearLayout = findViewById(R.id.idLinearLayout);
        configureToolbar();


        this.crearCowItem("vaca1");
        this.crearCowItem("vaca2");
        this.crearCowItem("vaca3");

    }


    protected void crearCowItem(String text){
        View newLayout = LayoutInflater.from(this).inflate(R.layout.list_cow, linearLayout, false);
        TextView textView = newLayout.findViewById(R.id.idTextViewCowItemList);

        textView.setText(text);

        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CowItem.class);
            intent.putExtra("nombre_vaca", text);
            startActivity(intent);
        });
        linearLayout.addView(newLayout);
    }
}
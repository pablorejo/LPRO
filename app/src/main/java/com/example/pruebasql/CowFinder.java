package com.example.pruebasql;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CowFinder extends BarraSuperior {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_finder);
        configureToolbar();


    }
}
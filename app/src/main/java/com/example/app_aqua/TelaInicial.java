package com.example.app_aqua;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


public class TelaInicial extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_index);




    }



    private void iniciar(){

        Intent intent =
                new Intent(
                        TelaInicial.this,
                        TelaLogin.class
                );


        startActivity(intent);
        finish();

    }

}
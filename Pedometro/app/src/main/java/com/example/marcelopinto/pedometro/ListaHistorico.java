package com.example.marcelopinto.pedometro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.marcelopinto.pedometro.BancoDeDados.BancoController;
import com.example.marcelopinto.pedometro.Entidades.Historico;

import static com.example.marcelopinto.pedometro.R.layout.listahistorico;

/**
 * Created by marcelo.pinto on 01/04/2018.
 * e-mail: marcelopinto@hotmail.com
 *
 */

public class ListaHistorico extends AppCompatActivity {

    private ArrayAdapter<Historico> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(listahistorico);

        ListView lst_historico = (ListView) findViewById(R.id.lst_historico);

        //Métodos
        adapter = BancoController.listaHistorico(this);

        lst_historico.setAdapter(adapter);
    }
}

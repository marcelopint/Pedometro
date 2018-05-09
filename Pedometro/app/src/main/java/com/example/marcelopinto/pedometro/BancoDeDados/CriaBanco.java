package com.example.marcelopinto.pedometro.BancoDeDados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by marcelo.pinto on 01/04/2018.
 * e-mail: marcelopinto@hotmail.com
 */

public class CriaBanco extends SQLiteOpenHelper {

    public static final String NOME_BANCO = "pedometro";
    public static final String configuracoes = "configuracoes";
    public static final String idade = "idade";
    public static final String sexo = "sexo";
    public static final String peso = "peso";
    public static final String altura = "altura";
    public static final String comprimento = "comprimento";
    public static final String sensibilidade = "sensibilidade";
    public static final String historico = "historico";
    public static final String data_inicio = "data_inicio";
    public static final String duracao = "duracao";
    public static final String qtd_passos = "qtd_passos";
    public static final String distancia = "distancia";
    public static final String calorias = "calorias";
    public static final int VERSAO = 1;


    public CriaBanco(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE IF NOT EXISTS " + configuracoes + " ( " +
                "idade         INTEGER, " +
                "sexo          VARCHAR (10), " +
                "peso          REAL, " +
                "altura        REAL, " +
                "comprimento   REAL, " +
                "sensibilidade INTEGER ); ";
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS " + historico + " ( " +
                "id          INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                "data_inicio TEXT, " +
                "duracao     TEXT, " +
                "qtd_passos  INTEGER, " +
                "distancia   DOUBLE, " +
                "calorias    FLOAT ); ";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.w("Exemplo", "Atualizando banco de dados, irá apagar a tabela e recriar.");
        db.execSQL("DROP TABLE IF EXISTS " + configuracoes);
        db.execSQL("DROP TABLE IF EXISTS " + historico);
        onCreate(db);
    }
}
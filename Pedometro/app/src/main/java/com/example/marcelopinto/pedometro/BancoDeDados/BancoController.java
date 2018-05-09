package com.example.marcelopinto.pedometro.BancoDeDados;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.marcelopinto.pedometro.Entidades.Historico;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

import static com.example.marcelopinto.pedometro.BancoDeDados.CriaBanco.sensibilidade;

/**
 * Created by marcelo.pinto on 01/04/2018.
 * e-mail: marcelopinto@hotmail.com
 */

public class BancoController {

    private static SQLiteDatabase db;
    public static CriaBanco criaBanco;

    public BancoController(Context context) {
        criaBanco = new CriaBanco(context);
    }

    public String insereDadoConfiguracoes(int idade, String sexo, float peso, float altura, float comprimento, int sensibilidade){
        ContentValues valores;
        long resultado;

        db = criaBanco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.idade, idade);
        valores.put(CriaBanco.sexo, sexo);
        valores.put(CriaBanco.peso, peso);
        valores.put(CriaBanco.altura, altura);
        valores.put(CriaBanco.comprimento, comprimento);
        valores.put(CriaBanco.sensibilidade, sensibilidade);

        resultado = db.insert(CriaBanco.configuracoes, null, valores);
        db.close();

        if (resultado ==-1)
            return "Erro ao inserir configurações.";
        else
            return "Configurações cadastradas com sucesso";
    }

    public void apagaDadoConfiguracoes() {
        db = criaBanco.getWritableDatabase();
        db.delete("configuracoes", null, null);
        db.close();
    }

    public Cursor dadosConfiguracoes(){
        db = criaBanco.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from configuracoes", null);
        cursor.moveToFirst();
        db.close();
        return cursor;
    }

    public boolean tabelaExiste(String tabela){
        db = criaBanco.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tabela});
        cursor.moveToFirst();
        Log.i("tabelaExiste", cursor.getString(0));
//        Cursor cursor = db.rawQuery("select count(*) from " + tabela, null);
        if (!cursor.moveToFirst()){
            cursor.close();
            db.close();
            return false;
        }
        db.close();
        cursor.close();;
        return true;
    }

    public String insereDados(String duracao, int qtd_passos, double distancia, double calorias){
        ContentValues valores;
        long resultado;
        SimpleDateFormat dtFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

        db = criaBanco.getWritableDatabase();
        valores = new ContentValues();

        valores.put(CriaBanco.data_inicio, dtFormat.format(new Date()));
        valores.put(CriaBanco.duracao, duracao);
        valores.put(CriaBanco.qtd_passos, qtd_passos);
        valores.put(CriaBanco.distancia, distancia);

        valores.put(CriaBanco.calorias, calorias);

        resultado = db.insert(CriaBanco.historico, null, valores);
        db.close();

        if (resultado ==-1)
            return "Erro ao inserir os dados.";
        else
            return "Dados cadastrados com sucesso.";
    }

    public static ArrayAdapter<Historico> listaHistorico(Context context){

        ArrayAdapter<Historico> adpHistorico = new ArrayAdapter<Historico>(context, android.R.layout.simple_list_item_1);

        db = criaBanco.getWritableDatabase();

        try (Cursor cursor = db.query("HISTORICO", null, null, null, null, null, "id DESC")) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Historico historico = new Historico();
                    historico.setData_inicio(cursor.getString(1));
                    historico.setDuracao(cursor.getString(2));
                    historico.setQtd_passos(Integer.parseInt(cursor.getString(3)));
                    historico.setDistancia(Double.parseDouble(cursor.getString(4)));
                    historico.setCalorias(Float.parseFloat(cursor.getString(5)));

                    adpHistorico.add(historico);

                } while (cursor.moveToNext());
            }
        }
        db.close();
        return adpHistorico;
    }
}

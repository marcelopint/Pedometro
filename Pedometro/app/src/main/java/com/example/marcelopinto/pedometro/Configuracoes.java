package com.example.marcelopinto.pedometro;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcelopinto.pedometro.BancoDeDados.BancoController;

import static com.example.marcelopinto.pedometro.R.layout.configuracoes;

/**
 * Created by marcelo.pinto on 01/04/2018.
 * e-mail: marcelopinto@hotmail.com
 *
 */

public class Configuracoes extends AppCompatActivity {

    private Button btn_confirmar;
    private SeekBar seekBar;
    private TextView tv_sensibilidade;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(configuracoes);

        final BancoController crud = new BancoController(getBaseContext());

        final EditText idade = (EditText)findViewById(R.id.idade);
        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.rg_sexo);
        final EditText peso = (EditText)findViewById(R.id.peso);
        final EditText altura = (EditText)findViewById(R.id.altura);
        final EditText comprimento = (EditText)findViewById(R.id.comprimento);
        seekBar = (SeekBar)findViewById(R.id.seekBar);

        btn_confirmar = (Button)findViewById(R.id.btn_confirmar);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        tv_sensibilidade = (TextView)findViewById(R.id.tv_sensibilidade);

        Cursor c = crud.dadosConfiguracoes();

        if (c.getCount() <= 0) {
            crud.insereDadoConfiguracoes(43, "Masculino", 78, 1.8f, 60, 10);
            c = crud.dadosConfiguracoes();
        }

        idade.setText(c.getString(0));
        if (c.getString(1).equals("Feminino")) radioGroup.check(R.id.rb_feminino);
        else radioGroup.check(R.id.rb_masculino);
        peso.setText(c.getString(2));
        altura.setText(c.getString(3));
        comprimento.setText(c.getString(4));
        tv_sensibilidade.setText(c.getString(5));
        seekBar.setProgress(Integer.parseInt(c.getString(5)));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progresso = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progresso = i;
                tv_sensibilidade.setText(""+progresso);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv_sensibilidade.setText(""+progresso);
            }
        });

        btn_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int idades = Integer.parseInt(idade.getText().toString());
                int i = radioGroup.getCheckedRadioButtonId();
                RadioButton radiobutton = (RadioButton) findViewById(i);
                String sexos = (String) radiobutton.getText();
                float pesos = Float.parseFloat(peso.getText().toString());
                float alturas = Float.parseFloat(altura.getText().toString());
                float comprimentos = Float.parseFloat(comprimento.getText().toString());
                int sensibilidades = seekBar.getProgress();

                crud.apagaDadoConfiguracoes();
                String resultado = crud.insereDadoConfiguracoes(idades, sexos, pesos, alturas, comprimentos, sensibilidades);

                Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }
}

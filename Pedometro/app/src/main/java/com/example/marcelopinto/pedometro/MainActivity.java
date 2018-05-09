package com.example.marcelopinto.pedometro;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcelopinto.pedometro.BancoDeDados.BancoController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by marcelo.pinto on 01/04/2018.
 * e-mail: marcelopinto@hotmail.com
 *
 */

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    public static float tamanho_passo;
    public static float peso;
    public static long duracao = 0;

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;

    private int num_passos = 0;
    private float distancia = 0;
    private long inicio;
    private float calorias = 0;

    private TextView tv_passos, tv_distancia, tv_velocidade, tv_calorias;
    private Button btn_iniciar, btn_pausar, btn_parar, btn_historico;
    private boolean pause = false;

    private LinearLayout linearLayout;
    private Bitmap myBitmap;
    private File imagem = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Pedometro.jpg");

    private Chronometer mChronometer;
    private long lastPause = 0;

    /* Criação do menu */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.compartilhar: //Compartilhar
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);

                linearLayout = (LinearLayout) findViewById(R.id.linearlayout1);
                linearLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        //take screenshot
                        myBitmap = captureScreen(linearLayout);

                        try {
                            if (myBitmap != null) {
                                //save image 
                                imagem = saveImage(myBitmap);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                sendIntent.setType("image/jpeg");
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imagem));

                startActivity(sendIntent);


                return true;
            case R.id.configuracoes:
                Intent intent = new Intent(this, Configuracoes.class);
                startActivity(intent);
                return true;
            case R.id.sobre: //Sobre
                new AlertDialog.Builder(this).setTitle("Sobre").setMessage("Pedômetro - Versão 1.0\nBy Marcelo Pinto - 2018").setIcon(R.drawable.ic_notification).setNeutralButton("Fechar", null).show();
                return true;
            case R.id.sair: //Sair
                pararPedometro();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtendo uma instância do SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Recuperando a instância do Sensor
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        simpleStepDetector = new StepDetector();

        // Registrando o listener
        simpleStepDetector.registerListener(this);

        tv_passos     = (TextView) findViewById(R.id.tv_passos);
        tv_distancia  = (TextView) findViewById(R.id.tv_distancia);
        tv_velocidade = (TextView) findViewById(R.id.tv_velocidade);
        tv_calorias   = (TextView) findViewById(R.id.tv_calorias);

        btn_iniciar   = (Button) findViewById(R.id.btn_start);
        btn_parar     = (Button) findViewById(R.id.btn_stop);
        btn_pausar    = (Button) findViewById(R.id.btn_pause);
        btn_historico = (Button) findViewById(R.id.btn_historico);

        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        final BancoController crud = new BancoController(getBaseContext());
        Cursor c = crud.dadosConfiguracoes();
        if (c.getCount() <= 0) {
            crud.insereDadoConfiguracoes(43, "Masculino", 78, 1.8f, 60, 10);
            c = crud.dadosConfiguracoes();
        }

        peso = c.getFloat(c.getColumnIndex("peso"));
        tamanho_passo = c.getFloat(c.getColumnIndex("comprimento"));
        StepDetector.STEP_THRESHOLD = c.getInt(c.getColumnIndex("sensibilidade"));
        c.close();

        btn_iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                btn_iniciar.setEnabled(false);
                btn_pausar.setEnabled(true);
                btn_parar.setEnabled(true);
                btn_historico.setEnabled(false);
                inicio = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.start();
            }
        });

        btn_pausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pause) {
                    pararPedometro();
                    btn_pausar.setText(R.string.continuar);
                    pause = true;
                    lastPause = SystemClock.elapsedRealtime();
                    mChronometer.stop();
                } else {
                    sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                    btn_pausar.setText(R.string.pausar);
                    pause = false;

                    if (lastPause != 0) {
                        mChronometer.setBase(mChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
                    } else {
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                    }
                    mChronometer.start();
                }
            }
        });

        btn_parar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                pararPedometro();
                btn_iniciar.setEnabled(true);
                btn_pausar.setText(R.string.pausar);
                pause = false;
                btn_pausar.setEnabled(false);
                btn_parar.setEnabled(false);
                btn_historico.setEnabled(true);
                Toast.makeText(getApplicationContext(), crud.insereDados(mChronometer.getText().toString(), num_passos, distancia, calorias), Toast.LENGTH_LONG).show();
                zeraInformaoes();
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        ++num_passos;
        tv_passos.setText("" + num_passos);

        distancia = (tamanho_passo / 100) * num_passos;

        tv_distancia.setText(String.format("%7.2f", distancia));

        if ((TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - inicio) >= 1) {
            ++duracao;
            tv_velocidade.setText(String.format("%5.2f", (float) (distancia / duracao)));
            inicio = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        }
        //  Calorias Gastas (kcal) = METs X Duração (h) X Peso (Kg)
        calorias = 3.0f * (duracao / 3600f) * peso;
        tv_calorias.setText(String.format("%5.2f", calorias));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pararPedometro();
    }

    private void pararPedometro() {
        sensorManager.unregisterListener(MainActivity.this);
    }

    private void zeraInformaoes() {
        num_passos = 0;
        tv_passos.setText("0");

        distancia = 0;
        tv_distancia.setText("0,00");

        duracao = 0;

        tv_velocidade.setText("0,00");

        calorias = 0;
        tv_calorias.setText("0,00");
        num_passos = 0;

        mChronometer.stop();
        mChronometer.setBase(SystemClock.elapsedRealtime());
        lastPause = 0;

    }

    public static Bitmap captureScreen(View v) {

        Bitmap screenshot = null;
        try {
            if (v != null) {
                screenshot = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(screenshot);
                v.draw(canvas);
            }

        } catch (Exception e) {
            Log.d("ScreenShotActivity", "Falha ao capturar imagem da tela: " + e.getMessage());
        }

        return screenshot;
    }

    public File saveImage(Bitmap bitmap) throws IOException {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Pedometro.jpg");

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
            return (f);
        } catch (Exception e) {
            Log.d("ScreenShotActivity", "Falha ao capturar imagem da tela: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void lista_historico(View view) {
        Intent intent = new Intent(this, ListaHistorico.class);
        startActivity(intent);
    }
}

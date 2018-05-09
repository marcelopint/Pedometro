package com.example.marcelopinto.pedometro;

/**
  * Created by marcelo.pinto on 01/04/2018.
 * e-mail: marcelopinto@hotmail.com  
 * Escuta alertas sobre passos sendo detectados.
 */

public interface StepListener {

    /**
     * É chamado quando um passo é detectado.
     * Informando o tempo em nanosegundos em que o passo foi detectado.
     */

    public void step(long timeNs);
}
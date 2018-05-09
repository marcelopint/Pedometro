package com.example.marcelopinto.pedometro.Entidades;

/**
 * Created by marcelo.pinto on 01/04/2018. 
 * e-mail: marcelopinto@hotmail.com
 * 
 */

public class Historico {

    private long id;
    private String data_inicio;
    private String duracao;
    private int qtd_passos;
    private double distancia;
    private double calorias;

    public Historico(){
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getData_inicio(){
        return data_inicio;
    }

    public void setData_inicio(String data_inicio){
        this.data_inicio = data_inicio;
    }

    public String getDuracao(){
        return duracao;
    }

    public void setDuracao(String duracao){
        this.duracao = duracao;
    }

    public int getQtd_passos() { return qtd_passos; }

    public void setQtd_passos(int qtd_passos) { this.qtd_passos = qtd_passos; }

    public double getDistancia(){ return distancia; };

    public void setDistancia(double distancia) { this.distancia = distancia; }

    public double getCalorias() { return calorias; }

    public void setCalorias(double calorias) { this.calorias = calorias; }

    public String toString(){
        return "Data: " + this.getData_inicio() +
                "\nDuração: " + this.getDuracao() +
                "\nQuantidade de Passos: " + this.getQtd_passos() +
                "\nDistância: " + this.getDistancia() +
                "\nCalorias: " + this.getCalorias();
    }
}

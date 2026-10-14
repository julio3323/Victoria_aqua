package com.example.app_aqua;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo de uma leitura registrada no histórico do Dashboard.
 *
 * Guarda os dados que o usuário digitou (categoria, leitura
 * anterior e leitura atual), a data do registro e os valores
 * calculados a partir das tarifas (consumo, água, esgoto e total).
 *
 * O id vem do servidor WAMP (chave primária no MySQL) — é ele
 * que o app usa para editar e excluir a leitura depois.
 * Antes de salvar no servidor, o id fica 0.
 *
 * As anotações @SerializedName ensinam o Gson a converter
 * os nomes do JSON do PHP (ex.: "leitura_anterior") para os
 * campos Java (ex.: leituraAnterior).
 */
public class Leitura {

    @SerializedName("id")
    private int id;

    @SerializedName("data_registro")
    private String data;

    @SerializedName("categoria")
    private String categoria;

    @SerializedName("leitura_anterior")
    private double leituraAnterior;

    @SerializedName("leitura_atual")
    private double leituraAtual;

    @SerializedName("consumo")
    private double consumo;

    @SerializedName("valor_agua")
    private double valorAgua;

    @SerializedName("valor_esgoto")
    private double valorEsgoto;

    @SerializedName("valor_total")
    private double valorTotal;

    /**
     * Construtor vazio, usado pelo Gson ao receber a lista
     * de leituras do servidor (ListarLeituras.php).
     */
    public Leitura() {
    }

    public Leitura(
            String data,
            String categoria,
            double leituraAnterior,
            double leituraAtual,
            double consumo,
            double valorAgua,
            double valorEsgoto,
            double valorTotal
    ) {
        this.data = data;
        this.categoria = categoria;
        this.leituraAnterior = leituraAnterior;
        this.leituraAtual = leituraAtual;
        this.consumo = consumo;
        this.valorAgua = valorAgua;
        this.valorEsgoto = valorEsgoto;
        this.valorTotal = valorTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getLeituraAnterior() {
        return leituraAnterior;
    }

    public double getLeituraAtual() {
        return leituraAtual;
    }

    public double getConsumo() {
        return consumo;
    }

    public double getValorAgua() {
        return valorAgua;
    }

    public double getValorEsgoto() {
        return valorEsgoto;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setLeituraAnterior(double leituraAnterior) {
        this.leituraAnterior = leituraAnterior;
    }

    public void setLeituraAtual(double leituraAtual) {
        this.leituraAtual = leituraAtual;
    }

    public void setConsumo(double consumo) {
        this.consumo = consumo;
    }

    public void setValorAgua(double valorAgua) {
        this.valorAgua = valorAgua;
    }

    public void setValorEsgoto(double valorEsgoto) {
        this.valorEsgoto = valorEsgoto;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }
}

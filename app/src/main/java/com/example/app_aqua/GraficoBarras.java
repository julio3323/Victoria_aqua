package com.example.app_aqua;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Gráfico de barras simples, desenhado à mão no Canvas —
 * sem bibliotecas externas. Cada barra é uma leitura do
 * histórico; a altura segue o valor (consumo em m³) e o
 * rótulo embaixo é a data da leitura (dd/MM).
 *
 * Como usar: chame definirDados(valores, rotulos) e a view
 * se redesenha sozinha.
 */
public class GraficoBarras extends View {

    // Uma barra por leitura: valor e rótulo (data)
    private final ArrayList<Double> valores = new ArrayList<>();
    private final ArrayList<String> rotulos = new ArrayList<>();

    private final Paint pinturaBarra = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pinturaTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pinturaLinha = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pinturaVazio = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Retângulo reaproveitado em cada barra (evita criar objeto no onDraw)
    private final RectF retangulo = new RectF();

    public GraficoBarras(
            Context contexto,
            AttributeSet atributos
    ) {
        super(contexto, atributos);

        // Cores da própria paleta do projeto (colors.xml)
        pinturaBarra.setColor(
                ContextCompat.getColor(
                        contexto,
                        R.color.dark_teal
                )
        );

        pinturaTexto.setColor(
                ContextCompat.getColor(
                        contexto,
                        R.color.ink_black
                )
        );

        pinturaLinha.setColor(
                ContextCompat.getColor(
                        contexto,
                        R.color.campo_borda
                )
        );

        pinturaVazio.setColor(
                ContextCompat.getColor(
                        contexto,
                        R.color.air_force_blue
                )
        );

        pinturaTexto.setTextAlign(Paint.Align.CENTER);

        pinturaVazio.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * Recebe os valores das barras e os rótulos (datas) e
     * pede o redesenho do gráfico.
     */
    public void definirDados(
            ArrayList<Double> novosValores,
            ArrayList<String> novosRotulos
    ) {

        valores.clear();
        rotulos.clear();

        if (novosValores != null) {
            valores.addAll(novosValores);
        }

        if (novosRotulos != null) {
            rotulos.addAll(novosRotulos);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas tela) {

        super.onDraw(tela);

        float largura = getWidth();
        float altura = getHeight();

        // Tamanhos de texto dependem da densidade da tela
        definirTamanhos();

        // Estado vazio: aviso centralizado
        if (valores.isEmpty()) {

            pinturaVazio.setTextSize(
                    tamanhoTexto(14)
            );

            tela.drawText(
                    getResources()
                            .getString(R.string.rel_sem_dados),
                    largura / 2f,
                    altura / 2f,
                    pinturaVazio
            );

            return;
        }

        // Área útil do gráfico (margens para os textos)
        float esquerda = converterDp(44);
        float direita = largura - converterDp(10);
        float baixo = altura - converterDp(30);
        float topo = converterDp(26);

        float alturaUtil = baixo - topo;

        // Maior valor (mínimo 1 para não dividir por zero)
        double maiorValor = 1;

        for (double valor : valores) {

            if (valor > maiorValor) {
                maiorValor = valor;
            }
        }

        // Linhas-guia horizontais (5 níveis: 0% a 100%)
        float passoAltura = alturaUtil / 4f;

        for (int i = 0; i <= 4; i++) {

            float y = baixo - (passoAltura * i);

            tela.drawLine(
                    esquerda,
                    y,
                    direita,
                    y,
                    pinturaLinha
            );

            // Valor da linha-guia à esquerda
            double valorLinha = (maiorValor / 4.0) * i;

            pinturaTexto.setTextAlign(Paint.Align.RIGHT);

            tela.drawText(
                    formatar(valorLinha),
                    esquerda - converterDp(6),
                    y + converterDp(4),
                    pinturaTexto
            );
        }

        pinturaTexto.setTextAlign(Paint.Align.CENTER);

        // Uma "fatia" horizontal para cada barra
        float larguraUtil = direita - esquerda;
        float passo = larguraUtil / valores.size();

        float raio = converterDp(6);

        for (int i = 0; i < valores.size(); i++) {

            double valor = valores.get(i);

            float alturaBarra =
                    (float) (alturaUtil * (valor / maiorValor));

            float centroX =
                    esquerda + (passo * i) + (passo / 2f);

            // Barra com no máximo 48dp de largura (espaço sobra vira margem)
            float larguraBarra =
                    Math.min(passo * 0.6f, converterDp(48));

            retangulo.set(
                    centroX - (larguraBarra / 2f),
                    baixo - alturaBarra,
                    centroX + (larguraBarra / 2f),
                    baixo
            );

            tela.drawRoundRect(
                    retangulo,
                    raio,
                    raio,
                    pinturaBarra
            );

            // Valor no topo da barra
            tela.drawText(
                    formatar(valor),
                    centroX,
                    retangulo.top - converterDp(8),
                    pinturaTexto
            );

            // Rótulo (data) embaixo da barra
            String rotulo =
                    rotulos.size() > i
                            ? rotulos.get(i)
                            : "";

            if (rotulo.isEmpty()) {
                continue;
            }

            tela.drawText(
                    rotulo,
                    centroX,
                    baixo + converterDp(18),
                    pinturaTexto
            );
        }
    }

    /**
     * Aplica os tamanhos de texto (em sp) nas pinturas.
     */
    private void definirTamanhos() {

        pinturaTexto.setTextSize(
                tamanhoTexto(11)
        );
    }

    private float tamanhoTexto(int sp) {

        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                getResources().getDisplayMetrics()
        );
    }

    private float converterDp(float dp) {

        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    /**
     * Número curto para os eixos: inteiro sem casas decimais,
     * quebrado com uma casa (ex.: "12" e "12,5").
     */
    private String formatar(double valor) {

        if (valor == Math.floor(valor)) {

            return String.valueOf(
                    (long) valor
            );
        }

        return String.format(
                Locale.getDefault(),
                "%.1f",
                valor
        );
    }
}

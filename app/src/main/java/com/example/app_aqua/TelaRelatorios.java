package com.example.app_aqua;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Tela de Relatórios — gráfico de barras do consumo por leitura.
 *
 * As leituras vêm do servidor (WAMP) pelo ListarLeituras.php — as
 * mesmas informações do histórico do Dashboard. Com elas, a tela
 * monta o resumo (total, média e maior consumo) e o gráfico de
 * barras desenhado pela view customizada GraficoBarras.
 */
public class TelaRelatorios extends AppCompatActivity {

    // Quantas leituras aparecem no gráfico (as mais recentes)
    private static final int MAXIMO_BARRAS = 8;

    // Id do usuário logado, repassado entre as telas pela barra de abas
    private int usuarioId;

    // Abas da barra (a aba "Relatórios" é a atual — não precisa de listener)
    private LinearLayout navInicio;
    private LinearLayout navDashboard;
    private LinearLayout navPerfil;

    // Resumo das leituras
    private TextView txtRelTotal;
    private TextView txtRelMedia;
    private TextView txtRelMaior;

    // Gráfico de barras do consumo por leitura
    private GraficoBarras graficoBarras;

    // Leitura carregadas do servidor
    private ArrayList<Leitura> leituras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_relatorios);

        usuarioId =
                getIntent().getIntExtra("usuario_id", 0);

        iniciarComponentes();
        configurarNavegacao();

        // Carrega as leituras do servidor (WAMP) para montar o relatório
        carregarLeituras();
    }

    private void iniciarComponentes() {

        navInicio =
                findViewById(R.id.navInicio);

        navDashboard =
                findViewById(R.id.navDashboard);

        navPerfil =
                findViewById(R.id.navPerfil);

        txtRelTotal =
                findViewById(R.id.txtRelTotal);

        txtRelMedia =
                findViewById(R.id.txtRelMedia);

        txtRelMaior =
                findViewById(R.id.txtRelMaior);

        graficoBarras =
                findViewById(R.id.graficoBarras);
    }

    private void configurarNavegacao() {

        // Aba INÍCIO
        navInicio.setOnClickListener(
                v -> {
                    Intent intent =
                            new Intent(
                                    TelaRelatorios.this,
                                    TelaInicial.class
                            );

                    intent.putExtra(
                            "usuario_id",
                            usuarioId
                    );

                    startActivity(intent);
                }
        );

        // Aba DASHBOARD
        navDashboard.setOnClickListener(
                v -> {
                    Intent intent =
                            new Intent(
                                    TelaRelatorios.this,
                                    TelaDashboard.class
                            );

                    intent.putExtra(
                            "usuario_id",
                            usuarioId
                    );

                    startActivity(intent);
                }
        );

        // Aba PERFIL
        navPerfil.setOnClickListener(
                v -> {
                    Intent intent =
                            new Intent(
                                    TelaRelatorios.this,
                                    TelaPerfil.class
                            );

                    intent.putExtra(
                            "usuario_id",
                            usuarioId
                    );

                    startActivity(intent);
                }
        );
    }

    // ===================== RELATÓRIO (SERVIDOR WAMP) =====================

    /**
     * Busca as leituras do usuário logado no servidor e monta
     * o resumo e o gráfico de barras.
     */
    private void carregarLeituras() {

        API api =
                ApiClient
                        .getRetrofit()
                        .create(API.class);

        api.listarLeituras(
                        usuarioId
                )
                .enqueue(new Callback<>() {

                    @Override
                    public void onResponse(
                            Call<Resposta> call,
                            Response<Resposta> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && "sucesso".equals(
                                response.body().getStatus()
                        )) {

                            leituras =
                                    response.body().getLeituras() != null
                                            ? new ArrayList<>(
                                            response.body().getLeituras()
                                    )
                                            : new ArrayList<>();

                            montarRelatorio();

                        } else {

                            Toast.makeText(
                                    TelaRelatorios.this,
                                    R.string.rel_erro_carregar,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Resposta> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                TelaRelatorios.this,
                                "Sem conexão com o servidor.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /**
     * Preenche o resumo (total, média e maior consumo de todas as
     * leituras) e o gráfico (as MAXIMO_BARRAS mais recentes, da
     * mais antiga para a mais recente, da esquerda para a direita).
     */
    private void montarRelatorio() {

        double total = 0;
        double maior = 0;

        for (Leitura leitura : leituras) {

            total += leitura.getConsumo();

            if (leitura.getConsumo() > maior) {
                maior = leitura.getConsumo();
            }
        }

        double media =
                leituras.isEmpty()
                        ? 0
                        : total / leituras.size();

        txtRelTotal.setText(
                String.format(
                        Locale.getDefault(),
                        "%.2f m³",
                        total
                )
        );

        txtRelMedia.setText(
                String.format(
                        Locale.getDefault(),
                        "%.2f m³",
                        media
                )
        );

        txtRelMaior.setText(
                String.format(
                        Locale.getDefault(),
                        "%.2f m³",
                        maior
                )
        );

        // Últimas leituras para o gráfico (da mais antiga para a mais recente)
        ArrayList<Double> valores =
                new ArrayList<>();

        ArrayList<String> rotulos =
                new ArrayList<>();

        int inicio =
                Math.max(0, leituras.size() - MAXIMO_BARRAS);

        for (int i = leituras.size() - 1; i >= inicio; i--) {

            Leitura leitura =
                    leituras.get(i);

            valores.add(
                    leitura.getConsumo()
            );

            rotulos.add(
                    rotuloCurto(
                            leitura.getData()
                    )
            );
        }

        graficoBarras.definirDados(
                valores,
                rotulos
        );
    }

    /**
     * Encurta a data para caber embaixo da barra:
     * "05/02/2026" vira "05/02".
     */
    private String rotuloCurto(String data) {

        if (data != null && data.length() >= 5) {
            return data.substring(0, 5);
        }

        return data != null ? data : "";
    }
}

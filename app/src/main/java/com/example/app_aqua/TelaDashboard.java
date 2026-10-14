package com.example.app_aqua;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TelaDashboard extends AppCompatActivity
        implements AdapterLeituras.AcoesLeitura {

    private TextView txtDataAtual;
    private TextView txtConsumo;
    private TextView txtCusto;
    private TextView txtEconomia;

    private EditText inputAnterior;
    private EditText inputAtual;

    private TextView resConsumo;
    private TextView resAgua;
    private TextView resEsgoto;
    private TextView resTotal;

    private Button btnCalcular;

    private Spinner spinnerCategoria;
    private Spinner spinnerFiltro;

    // Abas da barra de navegação inferior
    private LinearLayout navInicio;
    private LinearLayout navPerfil;
    private LinearLayout navRelatorios;

    // Id do usuário logado (recebido da TelaLogin, repassado ao Perfil)
    private int usuarioId;

    private ListView listView;

    private ArrayList<Leitura> historico;

    private AdapterLeituras adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        // Guarda o id do usuário que fez login
        usuarioId =
                getIntent().getIntExtra("usuario_id", 0);

        iniciarComponentes();
        configurar();
        configurarNavegacao();

        // Carrega o histórico salvo no servidor (WAMP)
        carregarHistorico();
    }

    private void iniciarComponentes() {

        txtDataAtual =
                findViewById(R.id.txtDataAtual);

        txtConsumo =
                findViewById(R.id.txtDashConsumo);

        txtCusto =
                findViewById(R.id.txtDashCusto);

        txtEconomia =
                findViewById(R.id.txtDashEconomia);

        inputAnterior =
                findViewById(R.id.inputLeituraAnterior);

        inputAtual =
                findViewById(R.id.inputLeituraAtual);

        resConsumo =
                findViewById(R.id.txtResConsumo);

        resAgua =
                findViewById(R.id.txtResAgua);

        resEsgoto =
                findViewById(R.id.txtResEsgoto);

        resTotal =
                findViewById(R.id.txtResTotal);

        btnCalcular =
                findViewById(R.id.btnCalcularConsumo);

        spinnerCategoria =
                findViewById(R.id.spinnerCategoria);

        spinnerFiltro =
                findViewById(R.id.spinnerFiltroHistorico);

        listView =
                findViewById(R.id.listViewDashboard);

        navInicio =
                findViewById(R.id.navInicio);

        navPerfil =
                findViewById(R.id.navPerfil);

        navRelatorios =
                findViewById(R.id.navRelatorios);
    }

    private void configurar() {

        String data =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                ).format(new Date());

        txtDataAtual.setText(data);

        historico =
                new ArrayList<>();

        adapter =
                new AdapterLeituras(
                        this,
                        historico,
                        this
                );

        listView.setAdapter(adapter);

        btnCalcular.setOnClickListener(
                v -> calcularConsumo()
        );
    }

    private void configurarNavegacao() {

        // Aba INÍCIO: abre a tela inicial (sobre o projeto)
        navInicio.setOnClickListener(
                v -> {
                    Intent intent =
                            new Intent(
                                    TelaDashboard.this,
                                    TelaInicial.class
                            );

                    intent.putExtra(
                            "usuario_id",
                            usuarioId
                    );

                    startActivity(intent);
                }
        );

        // Aba PERFIL: abre o perfil, levando o id do usuário logado
        navPerfil.setOnClickListener(
                v -> {
                    Intent intent =
                            new Intent(
                                    TelaDashboard.this,
                                    TelaPerfil.class
                            );

                    intent.putExtra(
                            "usuario_id",
                            usuarioId
                    );

                    startActivity(intent);
                }
        );

        // Aba RELATÓRIOS: abre a tela de relatórios
        navRelatorios.setOnClickListener(
                v -> {
                    Intent intent =
                            new Intent(
                                    TelaDashboard.this,
                                    TelaRelatorios.class
                            );

                    intent.putExtra(
                            "usuario_id",
                            usuarioId
                    );

                    startActivity(intent);
                }
        );
    }

    private void calcularConsumo() {

        if (inputAnterior.getText().toString().trim().isEmpty()
                || inputAtual.getText().toString().trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Informe as leituras.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        } else {

            double leituraAnterior;
            double leituraAtual;

            try {

                leituraAnterior =
                        Double.parseDouble(
                                inputAnterior.getText().toString().trim()
                        );

                leituraAtual =
                        Double.parseDouble(
                                inputAtual.getText().toString().trim()
                        );

            } catch (Exception e) {

                Toast.makeText(
                        this,
                        "Informe apenas números válidos.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (leituraAnterior > leituraAtual) {

                Toast.makeText(
                        this,
                        "A leitura anterior não pode ser maior que a atual.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            String categoria =
                    spinnerCategoria.getSelectedItem().toString();

            double consumo =
                    leituraAtual - leituraAnterior;

            double valorAgua =
                    calcularValorAgua(
                            categoria,
                            consumo
                    );

            String mensagemExtra =
                    mensagemCategoria(categoria);

            double valorEsgoto = valorAgua;
            double valorTotal = valorAgua + valorEsgoto;

            NumberFormat moeda =
                    NumberFormat.getCurrencyInstance(
                            new Locale("pt", "BR")
                    );

            resConsumo.setText(
                    String.format(
                            Locale.getDefault(),
                            "%.2f m³",
                            consumo
                    )
            );

            resAgua.setText(
                    moeda.format(valorAgua)
            );

            resEsgoto.setText(
                    moeda.format(valorEsgoto)
            );

            resTotal.setText(
                    moeda.format(valorTotal)
            );

            txtConsumo.setText(
                    String.format(
                            Locale.getDefault(),
                            "%.2f m³",
                            consumo
                    )
            );

            txtCusto.setText(
                    moeda.format(valorTotal)
            );

            String dataLeitura =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    ).format(new Date());

            Leitura leitura =
                    new Leitura(
                            dataLeitura,
                            categoria,
                            leituraAnterior,
                            leituraAtual,
                            consumo,
                            valorAgua,
                            valorEsgoto,
                            valorTotal
                    );

            historico.add(
                    0,
                    leitura
            );

            adapter.notifyDataSetChanged();

            // Salva a leitura no servidor (WAMP)
            salvarLeituraServidor(leitura);

            Toast.makeText(
                    this,
                    mensagemExtra,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // ===================== EDITAR / EXCLUIR LEITURAS =====================

    /**
     * Chamado pelo adapter quando o usuário toca em "Editar"
     * em uma linha do histórico.
     */
    @Override
    public void aoEditar(int posicao) {

        abrirEditarLeitura(posicao);
    }

    /**
     * Chamado pelo adapter quando o usuário toca em "Excluir"
     * em uma linha do histórico.
     */
    @Override
    public void aoExcluir(int posicao) {

        confirmarExcluirLeitura(posicao);
    }

    /**
     * Abre o diálogo de edição já preenchido com os dados
     * atuais da leitura escolhida.
     */
    private void abrirEditarLeitura(int posicao) {

        Leitura leitura =
                historico.get(posicao);

        Dialog dialogo =
                new Dialog(this);

        dialogo.setContentView(
                R.layout.dialog_editar_leitura
        );

        dialogo.show();

        dialogo
                .getWindow()
                .setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT)
                );

        dialogo
                .getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

        TextView txtData =
                dialogo.findViewById(R.id.txtEditarData);

        Spinner spinnerEditarCategoria =
                dialogo.findViewById(R.id.spinnerEditarCategoria);

        EditText inputEditarAnterior =
                dialogo.findViewById(R.id.inputEditarAnterior);

        EditText inputEditarAtual =
                dialogo.findViewById(R.id.inputEditarAtual);

        // Pré-preenche com os valores atuais da leitura
        txtData.setText(
                getString(
                        R.string.dash_editar_leitura_data,
                        leitura.getData()
                )
        );

        String[] categorias =
                getResources()
                        .getStringArray(
                                R.array.spnrCategorias
                        );

        for (int i = 0; i < categorias.length; i++) {

            if (categorias[i].equals(
                    leitura.getCategoria()
            )) {
                spinnerEditarCategoria.setSelection(i);
                break;
            }
        }

        inputEditarAnterior.setText(
                textoLeitura(
                        leitura.getLeituraAnterior()
                )
        );

        inputEditarAtual.setText(
                textoLeitura(
                        leitura.getLeituraAtual()
                )
        );

        dialogo
                .findViewById(R.id.btnCancelarEditarLeitura)
                .setOnClickListener(v -> dialogo.dismiss());

        dialogo
                .findViewById(R.id.btnSalvarEditarLeitura)
                .setOnClickListener(
                        v -> salvarEdicaoLeitura(
                                dialogo,
                                leitura,
                                spinnerEditarCategoria,
                                inputEditarAnterior,
                                inputEditarAtual
                        )
                );
    }

    /**
     * Valida os novos valores, recalcula o consumo e os custos
     * e atualiza a leitura no histórico.
     */
    private void salvarEdicaoLeitura(
            Dialog dialogo,
            Leitura leitura,
            Spinner spinnerEditarCategoria,
            EditText inputEditarAnterior,
            EditText inputEditarAtual
    ) {

        if (inputEditarAnterior.getText().toString().trim().isEmpty()
                || inputEditarAtual.getText().toString().trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Informe as leituras.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double leituraAnterior;
        double leituraAtual;

        try {

            leituraAnterior =
                    Double.parseDouble(
                            inputEditarAnterior
                                    .getText()
                                    .toString()
                                    .trim()
                    );

            leituraAtual =
                    Double.parseDouble(
                            inputEditarAtual
                                    .getText()
                                    .toString()
                                    .trim()
                    );

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Informe apenas números válidos.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (leituraAnterior > leituraAtual) {

            Toast.makeText(
                    this,
                    "A leitura anterior não pode ser maior que a atual.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String categoria =
                spinnerEditarCategoria
                        .getSelectedItem()
                        .toString();

        double consumo =
                leituraAtual - leituraAnterior;

        double valorAgua =
                calcularValorAgua(
                        categoria,
                        consumo
                );

        // Guarda os novos valores na própria leitura
        leitura.setCategoria(categoria);
        leitura.setLeituraAnterior(leituraAnterior);
        leitura.setLeituraAtual(leituraAtual);
        leitura.setConsumo(consumo);
        leitura.setValorAgua(valorAgua);
        leitura.setValorEsgoto(valorAgua);
        leitura.setValorTotal(valorAgua * 2);

        dialogo.dismiss();

        adapter.notifyDataSetChanged();

        atualizarCardsResumo();

        // Salva as alterações no servidor (WAMP)
        atualizarLeituraServidor(leitura);

        Toast.makeText(
                this,
                "Leitura atualizada. "
                        + mensagemCategoria(categoria),
                Toast.LENGTH_LONG
        ).show();
    }

    /**
     * Pede confirmação antes de excluir a leitura do histórico.
     */
    private void confirmarExcluirLeitura(int posicao) {

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dash_excluir_titulo)
                .setMessage(R.string.dash_excluir_mensagem)
                .setNegativeButton(
                        R.string.dash_excluir_cancelar,
                        null
                )
                .setPositiveButton(
                        R.string.dash_excluir_confirmar,
                        (dialogo, opcao) -> excluirLeitura(posicao)
                )
                .show();
    }

    /**
     * Remove a leitura do histórico e atualiza a tela.
     */
    private void excluirLeitura(int posicao) {

        Leitura leitura =
                historico.get(posicao);

        historico.remove(posicao);

        adapter.notifyDataSetChanged();

        atualizarCardsResumo();

        // Apaga a leitura também no servidor (WAMP)
        excluirLeituraServidor(leitura);

        Toast.makeText(
                this,
                "Leitura excluída do histórico.",
                Toast.LENGTH_SHORT
        ).show();
    }

    /**
     * Os cards do topo (Consumo Mensal e Custo Estimado) sempre
     * mostram a leitura mais recente — a primeira do histórico.
     * Sem leituras, voltam aos valores iniciais.
     */
    private void atualizarCardsResumo() {

        if (historico.isEmpty()) {

            txtConsumo.setText(
                    R.string.dash_card_consumo_valor
            );

            txtCusto.setText(
                    R.string.dash_card_custo_valor
            );

            return;
        }

        Leitura maisRecente =
                historico.get(0);

        txtConsumo.setText(
                String.format(
                        Locale.getDefault(),
                        "%.2f m³",
                        maisRecente.getConsumo()
                )
        );

        NumberFormat moeda =
                NumberFormat.getCurrencyInstance(
                        new Locale("pt", "BR")
                );

        txtCusto.setText(
                moeda.format(
                        maisRecente.getValorTotal()
                )
        );
    }

    // ===================== SERVIDOR (WAMP) =====================

    /**
     * Carrega do servidor todas as leituras do usuário logado
     * (a mais recente fica no topo) e preenche o histórico.
     */
    private void carregarHistorico() {

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

                            historico.clear();

                            if (response.body().getLeituras() != null) {

                                historico.addAll(
                                        response.body().getLeituras()
                                );
                            }

                            adapter.notifyDataSetChanged();

                            atualizarCardsResumo();

                        } else {

                            Toast.makeText(
                                    TelaDashboard.this,
                                    "Não foi possível carregar o histórico.",
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
                                TelaDashboard.this,
                                "Sem conexão com o servidor.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /**
     * Salva a leitura no servidor e guarda o id que o banco
     * devolve — é ele que permite editar e excluir depois.
     */
    private void salvarLeituraServidor(Leitura leitura) {

        API api =
                ApiClient
                        .getRetrofit()
                        .create(API.class);

        api.cadastrarLeitura(
                        usuarioId,
                        leitura.getData(),
                        leitura.getCategoria(),
                        leitura.getLeituraAnterior(),
                        leitura.getLeituraAtual(),
                        leitura.getConsumo(),
                        leitura.getValorAgua(),
                        leitura.getValorEsgoto(),
                        leitura.getValorTotal()
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

                            // Guarda o id do banco na própria leitura
                            leitura.setId(
                                    response.body().getId()
                            );

                        } else {

                            Toast.makeText(
                                    TelaDashboard.this,
                                    "Não foi possível salvar a leitura no servidor.",
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
                                TelaDashboard.this,
                                "Sem conexão com o servidor.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /**
     * Envia as alterações da leitura para o servidor.
     * Leitura ainda não salva (id 0) é cadastrada agora.
     */
    private void atualizarLeituraServidor(Leitura leitura) {

        // Nunca chegou ao servidor? Então salva como nova
        if (leitura.getId() == 0) {
            salvarLeituraServidor(leitura);
            return;
        }

        API api =
                ApiClient
                        .getRetrofit()
                        .create(API.class);

        api.atualizarLeitura(
                        leitura.getId(),
                        usuarioId,
                        leitura.getCategoria(),
                        leitura.getLeituraAnterior(),
                        leitura.getLeituraAtual(),
                        leitura.getConsumo(),
                        leitura.getValorAgua(),
                        leitura.getValorEsgoto(),
                        leitura.getValorTotal()
                )
                .enqueue(new Callback<>() {

                    @Override
                    public void onResponse(
                            Call<Resposta> call,
                            Response<Resposta> response
                    ) {

                        if (!(response.isSuccessful()
                                && response.body() != null
                                && "sucesso".equals(
                                response.body().getStatus()
                        ))) {

                            Toast.makeText(
                                    TelaDashboard.this,
                                    "Não foi possível atualizar a leitura no servidor.",
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
                                TelaDashboard.this,
                                "Sem conexão com o servidor.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /**
     * Apaga a leitura do servidor. Leitura ainda não salva
     * (id 0) só existia na memória — nada a fazer.
     */
    private void excluirLeituraServidor(Leitura leitura) {

        if (leitura.getId() == 0) {
            return;
        }

        API api =
                ApiClient
                        .getRetrofit()
                        .create(API.class);

        api.excluirLeitura(
                        leitura.getId(),
                        usuarioId
                )
                .enqueue(new Callback<>() {

                    @Override
                    public void onResponse(
                            Call<Resposta> call,
                            Response<Resposta> response
                    ) {

                        if (!(response.isSuccessful()
                                && response.body() != null
                                && "sucesso".equals(
                                response.body().getStatus()
                        ))) {

                            Toast.makeText(
                                    TelaDashboard.this,
                                    "Não foi possível excluir a leitura no servidor.",
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
                                TelaDashboard.this,
                                "Sem conexão com o servidor.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // ===================== TABELA DE TARIFAS =====================

    /**
     * Tabela de tarifas por categoria: devolve o valor da ÁGUA
     * conforme o consumo informado (em m³).
     *
     * Aceita os nomes com e sem acento (ex.: "Vulnerável" e
     * "Vulneravel") porque o spinner usa a versão acentuada.
     */
    private double calcularValorAgua(
            String categoria,
            double consumo
    ) {

        return switch (categoria) {

            case "Vulneravel", "Vulnerável" -> {

                double valorAgua;

                if (consumo <= 10) {
                    valorAgua = 8.74;
                } else if (consumo <= 20) {
                    valorAgua = 8.74 + ((consumo - 10) * 1.10);
                } else if (consumo <= 30) {
                    valorAgua = 8.74 + (10 * 1.10)
                            + ((consumo - 20) * 3.67);
                } else if (consumo <= 50) {
                    valorAgua = 8.74 + (10 * 1.10)
                            + (10 * 3.67)
                            + ((consumo - 30) * 11.10);
                } else {
                    valorAgua = 8.74 + (10 * 1.10)
                            + (10 * 3.67)
                            + (20 * 11.10)
                            + ((consumo - 50) * 12.26);
                }

                yield valorAgua;
            }
            case "Social1" -> {

                double valorAgua;

                if (consumo <= 10) {
                    valorAgua = 11.46;
                } else if (consumo <= 20) {
                    valorAgua = 11.46 + ((consumo - 10) * 2.18);
                } else if (consumo <= 30) {
                    valorAgua = 11.46 + (10 * 2.18)
                            + ((consumo - 20) * 7.79);
                } else if (consumo <= 50) {
                    valorAgua = 11.46 + (10 * 2.18)
                            + (10 * 7.79)
                            + ((consumo - 30) * 11.10);
                } else {
                    valorAgua = 11.46 + (10 * 2.18)
                            + (10 * 7.79)
                            + (20 * 11.10)
                            + ((consumo - 50) * 12.26);
                }

                yield valorAgua;
            }
            case "Social2" -> {

                double valorAgua;

                if (consumo <= 10) {
                    valorAgua = 20.21;
                } else if (consumo <= 15) {
                    valorAgua = 20.21 + ((consumo - 10) * 3.20);
                } else if (consumo <= 20) {
                    valorAgua = 20.21 + (5 * 3.20)
                            + ((consumo - 15) * 6.40);
                } else if (consumo <= 50) {
                    valorAgua = 20.21 + (5 * 3.20)
                            + (5 * 6.40)
                            + ((consumo - 20) * 15.95);
                } else {
                    valorAgua = 20.21 + (5 * 3.20)
                            + (5 * 6.40)
                            + (30 * 15.95)
                            + ((consumo - 50) * 17.57);
                }

                yield valorAgua;
            }
            case "Residencial" -> {

                double valorAgua;

                if (consumo <= 10) {
                    valorAgua = 40.42;
                } else if (consumo <= 20) {
                    valorAgua = 40.42 + ((consumo - 10) * 6.40);
                } else if (consumo <= 50) {
                    valorAgua = 40.42 + (10 * 6.40)
                            + ((consumo - 20) * 15.95);
                } else {
                    valorAgua = 40.42 + (10 * 6.40)
                            + (30 * 15.95)
                            + ((consumo - 50) * 17.57);
                }

                yield valorAgua;
            }
            case "Comercial" -> {

                double valorAgua;

                if (consumo <= 10) {
                    valorAgua = 81.57;
                } else if (consumo <= 20) {
                    valorAgua = 81.57 + ((consumo - 10) * 15.95);
                } else if (consumo <= 50) {
                    valorAgua = 81.57 + (10 * 15.95)
                            + ((consumo - 20) * 30.57);
                } else {
                    valorAgua = 81.57 + (10 * 15.95)
                            + (30 * 30.57)
                            + ((consumo - 50) * 31.84);
                }

                yield valorAgua;
            }
            case "Assistencia", "Assistência" -> {

                double valorAgua;

                if (consumo <= 10) {
                    valorAgua = 40.78;
                } else if (consumo <= 20) {
                    valorAgua = 40.78 + ((consumo - 10) * 7.97);
                } else if (consumo <= 50) {
                    valorAgua = 40.78 + (10 * 7.97)
                            + ((consumo - 20) * 15.34);
                } else {
                    valorAgua = 40.78 + (10 * 7.97)
                            + (30 * 15.34)
                            + ((consumo - 50) * 15.94);
                }

                yield valorAgua;
            }
            case "Publica", "Pública" -> {

                double valorAgua;

                if (consumo <= 10) {
                    valorAgua = 61.11;
                } else if (consumo <= 20) {
                    valorAgua = 61.11 + ((consumo - 10) * 11.95);
                } else if (consumo <= 50) {
                    valorAgua = 61.11 + (10 * 11.95)
                            + ((consumo - 20) * 22.99);
                } else {
                    valorAgua = 61.11 + (10 * 11.95)
                            + (30 * 22.99)
                            + ((consumo - 50) * 23.90);
                }

                yield valorAgua;
            }
            case "Rural" -> 40.42;

            default -> 0.0;
        };
    }

    /**
     * Mensagem exibida após aplicar a tarifa da categoria.
     */
    private String mensagemCategoria(String categoria) {

        return switch (categoria) {

            case "Vulneravel", "Vulnerável" ->
                    "Categoria Vulnerável aplicada.";

            case "Social1" ->
                    "Categoria Social 1 aplicada.";

            case "Social2" ->
                    "Categoria Social 2 aplicada.";

            case "Residencial" ->
                    "Categoria Residencial aplicada.";

            case "Comercial" ->
                    "Categoria Comercial aplicada.";

            case "Assistencia", "Assistência" ->
                    "Categoria Assistência Social aplicada.";

            case "Publica", "Pública" ->
                    "Categoria Pública aplicada.";

            case "Rural" ->
                    "Categoria Rural aplicada.";

            default -> "";
        };
    }

    /**
     * Converte o valor da leitura para texto sem o ".0"
     * desnecessário (ex.: 135.0 vira "135"; 135.5 continua "135.5").
     */
    private String textoLeitura(double valor) {

        if (valor == Math.floor(valor)
                && !Double.isInfinite(valor)) {

            return String.valueOf(
                    (long) valor
            );
        }

        return String.valueOf(valor);
    }
}
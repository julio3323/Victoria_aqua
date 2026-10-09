package com.example.app_aqua;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TelaDashboard extends AppCompatActivity {

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

    private ListView listView;

    private ArrayList<String> historico;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        iniciarComponentes();
        configurar();
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
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        historico
                );

        listView.setAdapter(adapter);

        btnCalcular.setOnClickListener(
                v -> calcularConsumo()
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

            double valorAgua = 0;

            String mensagemExtra = switch (categoria) {
                case "Vulneravel" -> {

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

                    yield "Categoria Vulnerável aplicada.";
                }
                case "Social1" -> {

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

                    yield "Categoria Social 1 aplicada.";
                }
                case "Social2" -> {

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

                    yield "Categoria Social 2 aplicada.";
                }
                case "Residencial" -> {

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

                    yield "Categoria Residencial aplicada.";
                }
                case "Comercial" -> {

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

                    yield "Categoria Comercial aplicada.";
                }
                case "Assistencia" -> {

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

                    yield "Categoria Assistência Social aplicada.";
                }
                case "Publica" -> {

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

                    yield "Categoria Pública aplicada.";
                }
                case "Rural" -> {

                    valorAgua = 40.42;

                    yield "Categoria Rural aplicada.";
                }
                default -> "";
            };

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

            historico.add(
                    0,
                    "Data: "
                            + new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    ).format(new Date())
                            + "\nConsumo: "
                            + String.format(
                            Locale.getDefault(),
                            "%.2f",
                            consumo
                    )
                            + " m³"
                            + "\nTotal: "
                            + moeda.format(valorTotal)
            );

            if (historico.size() > 10) {
                historico.remove(historico.size() - 1);
            }

            adapter.notifyDataSetChanged();

            Toast.makeText(
                    this,
                    mensagemExtra,
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
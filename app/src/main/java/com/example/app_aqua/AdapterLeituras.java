package com.example.app_aqua;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Adapter da lista "Histórico de Consumo" do Dashboard.
 *
 * Cada linha mostra a data, a categoria, o consumo e o total da
 * leitura, além dos botões "Editar" e "Excluir". Quando um dos
 * botões é tocado, o adapter avisa a tela pela interface
 * AcoesLeitura, informando a posição do item na lista.
 */
public class AdapterLeituras extends ArrayAdapter<Leitura> {

    /**
     * Ações executadas pela tela quando os botões são tocados.
     */
    public interface AcoesLeitura {

        void aoEditar(int posicao);

        void aoExcluir(int posicao);
    }

    private final AcoesLeitura ouvinte;

    public AdapterLeituras(
            Context contexto,
            ArrayList<Leitura> leituras,
            AcoesLeitura ouvinte
    ) {

        super(contexto, 0, leituras);

        this.ouvinte = ouvinte;
    }

    @Override
    public View getView(
            int posicao,
            View itemView,
            ViewGroup pai
    ) {

        // Cria a linha na primeira vez; depois reaproveita (reciclagem)
        if (itemView == null) {

            itemView =
                    LayoutInflater
                            .from(getContext())
                            .inflate(
                                    R.layout.item_leitura,
                                    pai,
                                    false
                            );
        }

        Leitura leitura =
                getItem(posicao);

        TextView txtData =
                itemView.findViewById(R.id.txtItemData);

        TextView txtCategoria =
                itemView.findViewById(R.id.txtItemCategoria);

        TextView txtConsumo =
                itemView.findViewById(R.id.txtItemConsumo);

        TextView txtTotal =
                itemView.findViewById(R.id.txtItemTotal);

        ImageButton btnEditar =
                itemView.findViewById(R.id.btnItemEditar);

        ImageButton btnExcluir =
                itemView.findViewById(R.id.btnItemExcluir);

        txtData.setText(
                "Data: "
                        + leitura.getData()
        );

        txtCategoria.setText(
                "Categoria: "
                        + leitura.getCategoria()
        );

        txtConsumo.setText(
                "Consumo: "
                        + String.format(
                        Locale.getDefault(),
                        "%.2f",
                        leitura.getConsumo()
                )
                        + " m³"
        );

        NumberFormat moeda =
                NumberFormat.getCurrencyInstance(
                        new Locale("pt", "BR")
                );

        txtTotal.setText(
                "Total: "
                        + moeda.format(
                        leitura.getValorTotal()
                )
        );

        btnEditar.setOnClickListener(
                v -> ouvinte.aoEditar(posicao)
        );

        btnExcluir.setOnClickListener(
                v -> ouvinte.aoExcluir(posicao)
        );

        return itemView;
    }
}

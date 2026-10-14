package com.example.app_aqua;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface API {

    @FormUrlEncoded
    @POST("Cadastrar.php")
    Call<Resposta> cadastrar(
            @Field("nome") String nome,
            @Field("email") String email,
            @Field("senha") String senha
    );


    @FormUrlEncoded
    @POST("Logar.php")
    Call<Resposta> logar(
            @Field("email") String email,
            @Field("senha") String senha
    );


    @FormUrlEncoded
    @POST("Perfil.php")
    Call<Resposta> perfil(
            @Field("id") int id
    );


    @FormUrlEncoded
    @POST("AtualizarImagem.php")
    Call<Resposta> atualizarImagem(
            @Field("id") int id,
            @Field("tipo") String tipo,
            @Field("imagem") String imagem
    );


    @FormUrlEncoded
    @POST("AtualizarPerfil.php")
    Call<Resposta> atualizarPerfil(
            @Field("id") int id,
            @Field("cidade") String cidade,
            @Field("profissao") String profissao,
            @Field("website") String website,
            @Field("telefone") String telefone,
            @Field("data_nascimento") String dataNascimento,
            @Field("bio") String bio
    );

    // ===================== HISTÓRICO DE LEITURAS (DASHBOARD) =====================

    @FormUrlEncoded
    @POST("CadastrarLeitura.php")
    Call<Resposta> cadastrarLeitura(
            @Field("usuario_id") int usuarioId,
            @Field("data_registro") String dataRegistro,
            @Field("categoria") String categoria,
            @Field("leitura_anterior") double leituraAnterior,
            @Field("leitura_atual") double leituraAtual,
            @Field("consumo") double consumo,
            @Field("valor_agua") double valorAgua,
            @Field("valor_esgoto") double valorEsgoto,
            @Field("valor_total") double valorTotal
    );

    @FormUrlEncoded
    @POST("ListarLeituras.php")
    Call<Resposta> listarLeituras(
            @Field("usuario_id") int usuarioId
    );

    @FormUrlEncoded
    @POST("AtualizarLeitura.php")
    Call<Resposta> atualizarLeitura(
            @Field("id") int id,
            @Field("usuario_id") int usuarioId,
            @Field("categoria") String categoria,
            @Field("leitura_anterior") double leituraAnterior,
            @Field("leitura_atual") double leituraAtual,
            @Field("consumo") double consumo,
            @Field("valor_agua") double valorAgua,
            @Field("valor_esgoto") double valorEsgoto,
            @Field("valor_total") double valorTotal
    );

    @FormUrlEncoded
    @POST("ExcluirLeitura.php")
    Call<Resposta> excluirLeitura(
            @Field("id") int id,
            @Field("usuario_id") int usuarioId
    );

}

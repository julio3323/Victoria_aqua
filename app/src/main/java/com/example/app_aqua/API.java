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

}
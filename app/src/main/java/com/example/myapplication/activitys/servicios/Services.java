package com.example.myapplication.activitys.servicios;

import com.example.myapplication.activitys.dto.GnImpresionDto;
import com.example.myapplication.activitys.dto.GnConcienciaDto;
import com.example.myapplication.activitys.dto.TaEmocionalDto;
import com.example.myapplication.activitys.dto.GnRetoRecuReenDto;
import com.example.myapplication.activitys.dto.TrabajoDto;
import com.example.myapplication.activitys.model.GnImpresion;
import com.example.myapplication.activitys.model.GnConciencia;

import com.example.myapplication.activitys.model.TaEmocional;
import com.example.myapplication.activitys.model.GnRetoRecuReen;
import com.example.myapplication.activitys.model.Tema;
import com.example.myapplication.activitys.model.Trabajo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Services {

    @GET("TemaApp")
    Call<List<Tema>> getAll();

    @POST("Trabajo/list")
    Call<List<Trabajo>> GnTrabajoService(@Body TrabajoDto trabajoDto);

    @POST("Retorecureen/list")
    Call<List<GnRetoRecuReen>> GnRetoRecuReenService(@Body GnRetoRecuReenDto taEstadoAnimoDto);

    @POST("Conciencia/list")
    Call<List<GnConciencia>> ConcienciaService(@Body GnConcienciaDto gnConcienciaDto);

    @POST("Impresion/list")
    Call<List<GnImpresion>> GnImpresionService(@Body GnImpresionDto gnImpresionDto);

    @POST("Emocional/list")
    Call<List<TaEmocional>> TaEmocionalService(@Body TaEmocionalDto taEmocionalDto);
}

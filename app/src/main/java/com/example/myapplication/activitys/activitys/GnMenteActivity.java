package com.example.myapplication.activitys.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.GnMenteAdapter;
import com.example.myapplication.activitys.dto.TrabajoDto;
import com.example.myapplication.activitys.model.Tema;
import com.example.myapplication.activitys.model.Trabajo;
import com.example.myapplication.activitys.network.ApiRetrofit;
import com.example.myapplication.activitys.servicios.Services;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GnMenteActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, Serializable {

    GnMenteAdapter gnMenteAdapter;
    private SearchView svSearch;
    private List<Trabajo> trabajoList;

    private RecyclerView rvLista;

    ListView listViewtrabajo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gn_mente);
        //listViewtrabajo = findViewById(R.id.listTrabajo);
        rvLista = findViewById(R.id.rvLista);
        svSearch = findViewById(R.id.svSearch);

        initViews();
        movetoDescription();
        initListener();

    }

    private void initViews(){
        rvLista = findViewById(R.id.rvLista);
        svSearch = findViewById(R.id.svSearch);
    }

    private void initListener() {
        svSearch.setOnQueryTextListener(this);
    }


    private void movetoDescription() {

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvLista.setLayoutManager(manager);
        Tema tema = (Tema) getIntent().getSerializableExtra("Temas");
        TrabajoDto trabajoDto = new TrabajoDto();
        trabajoDto.setIdTema(tema.getIdTema());

        Call<List<Trabajo>> call = ApiRetrofit.retrofit().create(Services.class).GnTrabajoService(trabajoDto);
        call.enqueue(new Callback<List<Trabajo>>() {
            @Override
            public void onResponse(Call<List<Trabajo>> call, Response<List<Trabajo>> response) {

                if (response.isSuccessful()) {

                    trabajoList = response.body();

                    gnMenteAdapter = new GnMenteAdapter(trabajoList);

                    //List<TmTrabajo> trabajoList = trabajoAdapter.ge
                    //trabajoAdapter =  TmTrabajoAdapter(getApplicationContext(), trabajosList);

                    rvLista.setAdapter(gnMenteAdapter);

                }

            }

            @Override
            public void onFailure(Call<List<Trabajo>> call, Throwable t) {
                Toast.makeText(GnMenteActivity.this, "Error en conexion", Toast.LENGTH_SHORT).show();

            }


        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        gnMenteAdapter.filter(newText);
        return false;
    }

}
package com.example.myapplication.activitys.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.GnRetoRecuReenAdapter;
import com.example.myapplication.activitys.dto.GnRetoRecuReenDto;
import com.example.myapplication.activitys.model.GnRetoRecuReen;
import com.example.myapplication.activitys.model.Tema;
import com.example.myapplication.activitys.network.ApiRetrofit;
import com.example.myapplication.activitys.servicios.Services;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GnRetoRecuReenActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    GnRetoRecuReenAdapter gnRetoRecuReenAdapter;
    private SearchView svSearchEstadoAnimo;
    private List<GnRetoRecuReen> gnRetoRecuReenList;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gn_reto_recu_reen);
        svSearchEstadoAnimo = findViewById(R.id.svSearchGnRetoRecuReen);
        recyclerView = findViewById(R.id.rvListaGnRetoRecuReen);

        initViews();
        movetoDescription();
        initListener();
    }

    private void initListener() {

        svSearchEstadoAnimo.setOnQueryTextListener(this);
    }
    private void initViews() {

        svSearchEstadoAnimo = findViewById(R.id.svSearchGnRetoRecuReen);
        recyclerView = findViewById(R.id.rvListaGnRetoRecuReen);
    }
    private void movetoDescription() {

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        Tema listElement = (Tema) getIntent().getSerializableExtra("Temas");
        GnRetoRecuReenDto gnRetoRecuReenDto = new GnRetoRecuReenDto();
        gnRetoRecuReenDto.setIdTema(listElement.getIdTema());

        Call<List<GnRetoRecuReen>> call = ApiRetrofit.retrofit().create(Services.class).GnRetoRecuReenService(gnRetoRecuReenDto);
        call.enqueue(new Callback<List<GnRetoRecuReen>>() {
            @Override
            public void onResponse(Call<List<GnRetoRecuReen>> call, Response<List<GnRetoRecuReen>> response) {

                if (response.isSuccessful()) {

                    gnRetoRecuReenList = response.body();

                    gnRetoRecuReenAdapter = new GnRetoRecuReenAdapter(gnRetoRecuReenList);

                    //List<TmTrabajo> trabajoList = trabajoAdapter.ge
                    //trabajoAdapter =  TmTrabajoAdapter(getApplicationContext(), trabajosList);

                    recyclerView.setAdapter(gnRetoRecuReenAdapter);

                }

            }

            @Override
            public void onFailure(Call<List<GnRetoRecuReen>> call, Throwable t) {

            }
        });
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        gnRetoRecuReenAdapter.filter(newText);
        return false;
    }
}
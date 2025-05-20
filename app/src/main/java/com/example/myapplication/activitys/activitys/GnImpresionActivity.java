package com.example.myapplication.activitys.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.GnImpresionAdapter;
import com.example.myapplication.activitys.dto.GnImpresionDto;
import com.example.myapplication.activitys.model.GnImpresion;
import com.example.myapplication.activitys.model.Tema;
import com.example.myapplication.activitys.network.ApiRetrofit;
import com.example.myapplication.activitys.servicios.Services;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GnImpresionActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    GnImpresionAdapter gnImpresionAdapter;
    private SearchView svSearch;
    private List<GnImpresion> gnImpresionList;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gn_impresion);
        svSearch = findViewById(R.id.svSearchImpresion);
        recyclerView = findViewById(R.id.rvListaImpresion);

        initViews();
        movetoDescription();
        initListener();
    }

    private void initListener() {
        svSearch.setOnQueryTextListener(this);
    }

    private void initViews() {

        svSearch = findViewById(R.id.svSearchImpresion);
        recyclerView = findViewById(R.id.rvListaImpresion);

    }

    private void movetoDescription() {

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        Tema listElement = (Tema) getIntent().getSerializableExtra("Temas");
        GnImpresionDto gnImpresionDto = new GnImpresionDto();
        gnImpresionDto.setIdTema(listElement.getIdTema());

        Call<List<GnImpresion>> call = ApiRetrofit.retrofit().create(Services.class).GnImpresionService(gnImpresionDto);
        call.enqueue(new Callback<List<GnImpresion>>() {
            @Override
            public void onResponse(Call<List<GnImpresion>> call, Response<List<GnImpresion>> response) {

                if (response.isSuccessful()) {

                    gnImpresionList = response.body();

                    gnImpresionAdapter = new GnImpresionAdapter(gnImpresionList);

                    //List<TmTrabajo> trabajoList = trabajoAdapter.ge
                    //trabajoAdapter =  TmTrabajoAdapter(getApplicationContext(), trabajosList);

                    recyclerView.setAdapter(gnImpresionAdapter);

                }

            }

            @Override
            public void onFailure(Call<List<GnImpresion>> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        gnImpresionAdapter.filter(newText);
        return false;
    }
}
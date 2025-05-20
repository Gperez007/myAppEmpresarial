package com.example.myapplication.activitys.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.GnConcienciaAdapter;
import com.example.myapplication.activitys.dto.GnConcienciaDto;
import com.example.myapplication.activitys.model.GnConciencia;
import com.example.myapplication.activitys.model.Tema;
import com.example.myapplication.activitys.network.ApiRetrofit;
import com.example.myapplication.activitys.servicios.Services;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GnConcienciaActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    GnConcienciaAdapter taConcienciaAdapter;
    private SearchView searchView;
    private List<GnConciencia> taConcienciaList;
    private RecyclerView recyclerViewConducta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ta_conciencia);
        recyclerViewConducta = findViewById(R.id.rvListaConciencia);
        searchView = findViewById(R.id.svSearchConciecnia);

        initViews();
        movetoDescription();
        initListener();

    }

    private void initViews() {

        recyclerViewConducta = findViewById(R.id.rvListaConciencia);
        searchView = findViewById(R.id.svSearchConciecnia);

    }

    private void initListener() {
        searchView.setOnQueryTextListener(this);
    }

    private void movetoDescription() {

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerViewConducta.setLayoutManager(manager);
        Tema listElement = (Tema) getIntent().getSerializableExtra("Temas");
        GnConcienciaDto gnConcienciaDto = new GnConcienciaDto();
        gnConcienciaDto.setIdTema(listElement.getIdTema());

        Call<List<GnConciencia>> call = ApiRetrofit.retrofit().create(Services.class).ConcienciaService(gnConcienciaDto);
        call.enqueue(new Callback<List<GnConciencia>>() {
            @Override
            public void onResponse(Call<List<GnConciencia>> call, Response<List<GnConciencia>> response) {

                if (response.isSuccessful()) {

                    taConcienciaList = response.body();

                    taConcienciaAdapter = new GnConcienciaAdapter(taConcienciaList);

                    //List<TmTrabajo> trabajoList = trabajoAdapter.ge
                    //trabajoAdapter =  TmTrabajoAdapter(getApplicationContext(), trabajosList);

                    recyclerViewConducta.setAdapter(taConcienciaAdapter);

                }

            }

            @Override
            public void onFailure(Call<List<GnConciencia>> call, Throwable t) {
                Toast.makeText(GnConcienciaActivity.this, "Error en conexion", Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        taConcienciaAdapter.filter(newText);
        return false;
    }
}
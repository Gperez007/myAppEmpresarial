package com.example.myapplication.activitys.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.activitys.adapter.TaEmocionalAdapter;
import com.example.myapplication.activitys.dto.TaEmocionalDto;
import com.example.myapplication.activitys.model.TaEmocional;
import com.example.myapplication.activitys.model.Tema;
import com.example.myapplication.activitys.network.ApiRetrofit;
import com.example.myapplication.activitys.servicios.Services;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaEmocionalActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    TaEmocionalAdapter taEmocionalAdapter;
    private SearchView searchViewEmocional;
    private List<TaEmocional> taEmocionalList;
    private RecyclerView recyclerViewEmocional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ta_emocional);
        searchViewEmocional = findViewById(R.id.svSearchEmocional);
        recyclerViewEmocional = findViewById(R.id.rvListaEmocional);

        initViews();
        movetoDescription();
        initListener();

    }


    private void initListener() {

        searchViewEmocional.setOnQueryTextListener(this);
    }

    private void initViews() {
        searchViewEmocional = findViewById(R.id.svSearchEmocional);
        recyclerViewEmocional = findViewById(R.id.rvListaEmocional);
    }

    private void movetoDescription() {

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerViewEmocional.setLayoutManager(manager);
        Tema listElement = (Tema) getIntent().getSerializableExtra("Temas");
        TaEmocionalDto taEmocionalDto = new TaEmocionalDto();
        taEmocionalDto.setIdTema(listElement.getIdTema());

        Call<List<TaEmocional>> call = ApiRetrofit.retrofit().create(Services.class).TaEmocionalService(taEmocionalDto);
        call.enqueue(new Callback<List<TaEmocional>>() {
            @Override
            public void onResponse(Call<List<TaEmocional>> call, Response<List<TaEmocional>> response) {

                if (response.isSuccessful()) {

                    taEmocionalList = response.body();

                    taEmocionalAdapter = new TaEmocionalAdapter(taEmocionalList);

                    //List<TmTrabajo> trabajoList = trabajoAdapter.ge
                    //trabajoAdapter =  TmTrabajoAdapter(getApplicationContext(), trabajosList);

                    recyclerViewEmocional.setAdapter(taEmocionalAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<TaEmocional>> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
      taEmocionalAdapter.filter(newText);
        return false;
    }
}
package com.example.exercicio1_100826;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        carregarEstados();
    }

    private void carregarEstados() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String json = requisitar("https://servicodados.ibge.gov.br/api/v1/localidades/estados?orderBy=nome");
                JSONArray array = new JSONArray(json);
                List<Item> estados = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    estados.add(new Item(obj.getInt("id"), obj.getString("nome")));
                }

                runOnUiThread(() -> {
                    recyclerView.setAdapter(new ItemAdapter(estados, estado -> carregarCidades(estado.id)));
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Erro ao buscar estados", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void carregarCidades(int idEstado) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String json = requisitar("https://servicodados.ibge.gov.br/api/v1/localidades/estados/" + idEstado + "/municipios");
                JSONArray array = new JSONArray(json);
                List<Item> cidades = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    cidades.add(new Item(obj.getInt("id"), obj.getString("nome")));
                }
                runOnUiThread(() -> {
                    recyclerView.setAdapter(new ItemAdapter(cidades, null));
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Erro ao buscar cidades", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String requisitar(String urlTexto) throws Exception {
        URL url = new URL(urlTexto);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader buffer = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder resposta = new StringBuilder();
        String linha;
        while ((linha = buffer.readLine()) != null) {
            resposta.append(linha);
        }
        con.disconnect();
        return resposta.toString();
    }
}
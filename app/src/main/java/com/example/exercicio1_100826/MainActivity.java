package com.example.exercicio1_100826;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnVoltar = findViewById(R.id.btnVoltar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Listener do botão de voltar para recarregar a lista de estados
        btnVoltar.setOnClickListener(v -> carregarEstados());

        // Carregamento inicial ao abrir a tela
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
                    int id = obj.getInt("id");
                    String nomeEstado = obj.getString("nome");
                    String regiao = obj.getJSONObject("regiao").getString("nome");

                    estados.add(new Item(id, nomeEstado, "Região: " + regiao));
                }

                runOnUiThread(() -> {
                    btnVoltar.setVisibility(View.GONE);
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
                    int id = obj.getInt("id");
                    String nomeCidade = obj.getString("nome");
                    String microrregiao = obj.getJSONObject("microrregiao").getString("nome");
                    String regiaoImediata = obj.getJSONObject("regiao-imediata").getString("nome");

                    String detalhes = "Microrregião: " + microrregiao + " | Reg. Imediata: " + regiaoImediata;
                    cidades.add(new Item(id, nomeCidade, detalhes));
                }

                runOnUiThread(() -> {
                    btnVoltar.setVisibility(View.VISIBLE);
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

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder resposta = new StringBuilder();
            String linha;
            while ((linha = bufferedReader.readLine()) != null) {
                resposta.append(linha);
            }
            return resposta.toString();
        } finally {
            con.disconnect();
        }
    }
}
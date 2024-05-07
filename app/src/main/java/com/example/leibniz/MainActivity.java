package com.example.leibniz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private EditText etTerminos;
    private Button btnCalcular;
    private TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Asegúrate de que el layout coincida

        // Obtener referencias a los elementos de la interfaz de usuario
        etTerminos = findViewById(R.id.editTextText); // EditText para términos
        btnCalcular = findViewById(R.id.button); // Botón para calcular
        tvResultado = findViewById(R.id.resultado); // TextView para mostrar resultados

        // Configurar el botón para realizar la solicitud HTTP
        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String terminos = etTerminos.getText().toString().trim();

                // Ejecutar la tarea en segundo plano para obtener datos del servidor
                new CalcularPiTask().execute(
                        String.format(
                                "http://10.10.35.11:3000/calcular-pi?terminos=%s",
                                terminos
                        )
                );
            }
        });
    }

    private class CalcularPiTask extends AsyncTask<String, Void, String> {
        private final OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... urls) {
            try {
                Request request = new Request.Builder()
                        .url(urls[0])
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);

            if (resultado.startsWith("Error:")) {
                tvResultado.setText(resultado);
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(resultado);
                int terminos = jsonObject.getInt("terminos");
                String serie = jsonObject.getString("serie");
                double piAprox = jsonObject.getDouble("valorAproximadoDePi");

                // Construir la salida en un formato legible
                String resultadoFormateado = String.format(
                        "Número de términos: %d\nSerie: %s\nValor aproximado de Pi: %.6f",
                        terminos,
                        serie,
                        piAprox
                );

                tvResultado.setText(resultadoFormateado);

            } catch (Exception e) {
                tvResultado.setText("Error al procesar la respuesta");
            }
        }
    }
}
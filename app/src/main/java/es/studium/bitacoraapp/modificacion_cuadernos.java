package es.studium.bitacoraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import es.studium.bitacoraapp.controlador.AdapterCuadernos;
import es.studium.bitacoraapp.modelos.Cuadernos;

import static es.studium.bitacoraapp.MainActivity.server;

public class modificacion_cuadernos extends AppCompatActivity implements View.OnClickListener {


    ModificacionRemota modifica;
    AdapterCuadernos adapterCuadernos;
    TextView id;
    EditText nombre;
    Button modificar;
    Button cancelar;

    private Cuadernos cuadernos;

    JSONArray result;
    JSONObject jsonobject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificacion_cuadernos);

        id = findViewById(R.id.idCuaderno);
        id.setText("ID: "+MainActivity.items.get(MainActivity.id).getIdCuaderno());
        nombre = findViewById(R.id.nombreCuaderno);
        nombre.setText(MainActivity.items.get(MainActivity.id).getCuaderno());
        modificar = findViewById(R.id.btnModificar);
        modificar.setOnClickListener(this);
        cancelar = findViewById(R.id.btnCancelarModificar);
        cancelar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==(R.id.btnModificar)){

            String nombreModificado = String.valueOf(nombre.getText());
            modifica = new ModificacionRemota(MainActivity.items.get(MainActivity.id).getIdCuaderno()+"",nombreModificado);
            modifica.execute();
            Intent intent =  new Intent(modificacion_cuadernos.this, MainActivity.class);
            startActivity(intent);

        }
        else {
            finish();
        }
    }

    private class ModificacionRemota extends AsyncTask<Void, Void, String>
    {
        // Atributos
        String idCuaderno;
        String nombreCuaderno;

        // Constructor
        public ModificacionRemota(String id,String nombre)
        {
            this.idCuaderno = id;
            this.nombreCuaderno = nombre;

        }
        // Inspectores
        protected void onPreExecute()
        {
            Toast.makeText(modificacion_cuadernos.this, "Modificando...",
                    Toast.LENGTH_SHORT).show();
        }
        protected String doInBackground(Void... voids)
        {
            try
            {
                String response = "";
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority(server)
                        .path("/ApiRest/cuadernos.php")
                        .appendQueryParameter("idCuaderno", this.idCuaderno)
                        .appendQueryParameter("nombreCuaderno", this.nombreCuaderno)
                        .build();
                // Create connection
                URL url = new URL(uri.toString());
                HttpURLConnection connection = (HttpURLConnection)
                        url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("PUT");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                int responseCode=connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    String line;
                    BufferedReader br=new BufferedReader(new
                            InputStreamReader(connection.getInputStream()));
                    while ((line=br.readLine()) != null)
                    {
                        response+=line;
                    }
                }
                else
                {
                    response="";
                }
                connection.getResponseCode();
                if (connection.getResponseCode() == 200)
                {
                    // Success
                    Log.println(Log.ASSERT,"Resultado", "Registro modificado:"+response);
                    connection.disconnect();
                }
                else
                {
                    // Error handling code goes here
                    Log.println(Log.ASSERT,"Error", "Error");
                }
            }
            catch(Exception e)
            {
                Log.println(Log.ASSERT,"Excepción", e.getMessage());
            }
            return null;
        }
        protected void onPostExecute(String mensaje)
        {
            Toast.makeText(modificacion_cuadernos.this, "Actualizando datos...",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
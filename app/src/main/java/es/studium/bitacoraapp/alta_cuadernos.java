package es.studium.bitacoraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import es.studium.bitacoraapp.controlador.AdapterCuadernos;

import static es.studium.bitacoraapp.MainActivity.server;

public class alta_cuadernos extends AppCompatActivity implements View.OnClickListener {

    EditText nombre;
    Button alta;
    Button cancelar;
    AltaRemota altaRemota;
    AdapterCuadernos adapterCuadernos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_cuadernos);
        nombre = findViewById(R.id.nombreCuaderno);
        alta = findViewById(R.id.btnAlta);
        alta.setOnClickListener(this);
        cancelar = findViewById(R.id.btnCancelarModificar);
        cancelar.setOnClickListener(this
        );
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==(R.id.btnAlta)){
            altaRemota = new AltaRemota(String.valueOf(nombre.getText()));
            altaRemota.execute();
            Intent intent =  new Intent(alta_cuadernos.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            finish();
        }
    }

    private class AltaRemota extends AsyncTask<Void, Void, String>
    {
        // Atributos
        String nombreCuaderno;

        // Constructor
        public AltaRemota(String nombreCuaderno)
        {
            this.nombreCuaderno = nombreCuaderno;

        }
        // Inspectoras
        protected void onPreExecute()
        {
            Toast.makeText(alta_cuadernos.this, "Alta..."+this.nombreCuaderno,
                    Toast.LENGTH_SHORT).show();
        }
        protected String doInBackground(Void... argumentos)
        {
            try {
                // Crear la URL de conexión al API
                URL url = new
                        URL("http://"+server+"/ApiRest/cuadernos.php");
                // Crear la conexión HTTP
                HttpURLConnection myConnection = (HttpURLConnection)
                        url.openConnection();
                // Establecer método de comunicación.
                myConnection.setRequestMethod("POST");
                // Conexión exitosa
                String response = "";
                HashMap<String, String> postDataParams = new
                        HashMap<String, String>();
                postDataParams.put("nombreCuaderno",
                        this.nombreCuaderno);
                myConnection.setDoInput(true);
                myConnection.setDoOutput(true);
                OutputStream os = myConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new
                        OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();

                os.close();
                myConnection.getResponseCode();
                if (myConnection.getResponseCode() == 200)
                {
                    // Success
                    myConnection.disconnect();
                }
                else {
                    // Error handling code goes here
                    Log.println(Log.ASSERT, "Error", "Error");
                }
            }
            catch(Exception e)
            {
                Log.println(Log.ASSERT,"Excepción", e.getMessage());
            }
            return (null);
        }
        protected void onPostExecute(String mensaje)
        {
            // Actualizamos los cuadros de texto

            Toast.makeText(alta_cuadernos.this, "Alta Correcta...",
                    Toast.LENGTH_SHORT).show();
        }
        private String getPostDataString(HashMap<String, String> params)
                throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet())
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    result.append("&");
                }
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return result.toString();
        }
    }
}
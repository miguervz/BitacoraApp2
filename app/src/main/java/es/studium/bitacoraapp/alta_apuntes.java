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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static es.studium.bitacoraapp.MainActivity.server;

public class alta_apuntes extends AppCompatActivity implements View.OnClickListener {








    EditText fecha;
    EditText texto;
    Button alta;
    Button cancelar;
    Date fechaActual;
    AltaRemota altaRemota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_apuntes);

        fechaActual = new Date();
        fecha = findViewById(R.id.txtFechaAlta);
        fecha.setText(new SimpleDateFormat("dd/MM/yyyy").format(fechaActual));
        texto = findViewById(R.id.nombreApunte);
        alta = findViewById(R.id.btnAltaApuntes);
        alta.setOnClickListener(this);
        cancelar = findViewById(R.id.btnCancelarAltaApuntes);
        cancelar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==(R.id.btnAltaApuntes)){
            int idCuadernoFK = MainActivity.items.get(MainActivity.id).getIdCuaderno();
            String fechaEuropea = String.valueOf(fecha.getText());
            String[] fechaActual = fechaEuropea.split("/");
            String fechaAmericana = fechaActual[2]+"-"+fechaActual[1]+"-"+fechaActual[0];
            altaRemota = new AltaRemota(fechaAmericana, String.valueOf(texto.getText()),idCuadernoFK);
            altaRemota.execute();
            Intent intent =  new Intent(alta_apuntes.this, MainActivity2.class);
            startActivity(intent);
        }
        else {
            finish();
        }
    }

    private class AltaRemota extends AsyncTask<Void, Void, String>
    {
        // Atributos
        String fechaApunte;
        String textoApunte;
        int idCuadernoFK;

        // Constructor
        public AltaRemota(String fechaApunte,String nombreCuaderno, int idCuadernoFK)
        {
            this.textoApunte = nombreCuaderno;
            this.fechaApunte = fechaApunte;
            this.idCuadernoFK = idCuadernoFK;
        }
        // Inspectoras
        protected void onPreExecute()
        {
            Toast.makeText(alta_apuntes.this, "Alta..."+this.textoApunte,
                    Toast.LENGTH_SHORT).show();
        }
        protected String doInBackground(Void... argumentos)
        {
            try {
                // Crear la URL de conexión al API
                URL url = new
                        URL("http://"+server+"/ApiRest/apuntes.php");
                // Crear la conexión HTTP
                HttpURLConnection myConnection = (HttpURLConnection)
                        url.openConnection();
                // Establecer método de comunicación.
                myConnection.setRequestMethod("POST");
                // Conexión exitosa
                String response = "";
                HashMap<String, String> postDataParams = new
                        HashMap<String, String>();
                postDataParams.put("fechaApunte",
                        this.fechaApunte);
                postDataParams.put("textoApunte",
                        this.textoApunte);
                postDataParams.put("idCuadernoFK",
                        this.idCuadernoFK+"");
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

            Toast.makeText(alta_apuntes.this, "Alta Correcta...",
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
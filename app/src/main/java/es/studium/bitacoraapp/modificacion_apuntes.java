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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static es.studium.bitacoraapp.MainActivity.server;

public class modificacion_apuntes extends AppCompatActivity implements View.OnClickListener {

    ModificacionRemota modifica;

    TextView id;
    EditText fecha;
    EditText texto;
    Button modificar;
    Button cancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificacio_apuntes);

        id = findViewById(R.id.txtIdApunte);
        id.setText("ID: "+MainActivity2.items.get(MainActivity2.idApunte).getIdApunte());
        fecha = findViewById(R.id.txtFechaModificacion);
        fecha.setText(MainActivity2.items.get(MainActivity2.idApunte).getFecha());
        texto = findViewById(R.id.textoApunteModificar);
        texto.setText(MainActivity2.items.get(MainActivity2.idApunte).getTexto());
        modificar = findViewById(R.id.btnModificacionApuntes);
        modificar.setOnClickListener(this);
        cancelar = findViewById(R.id.btnCancelarModificacionApuntes);
        cancelar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==(R.id.btnModificacionApuntes)){
            String idApunte = MainActivity2.items.get(MainActivity2.idApunte).getIdApunte()+"";
            String fechaEuropea = String.valueOf(fecha.getText());
            String[] fechaActual = fechaEuropea.split("/");
            String fechaAmericana = fechaActual[2]+"-"+fechaActual[1]+"-"+fechaActual[0];
            String textoEditado = String.valueOf(texto.getText());
            modifica = new ModificacionRemota(idApunte, fechaAmericana, textoEditado);
            modifica.execute();
            Intent intent =  new Intent(modificacion_apuntes.this, MainActivity2.class);
            startActivity(intent);
        }
        else {
            finish();
        }
    }

    private class ModificacionRemota extends AsyncTask<Void, Void, String>
    {
        // Atributos
        String idApunte;
        String fechaApunte;
        String textoApunte;

        // Constructor
        public ModificacionRemota(String id,String fechaApunte, String textoApunte)
        {
            this.idApunte = id;
            this.fechaApunte = fechaApunte;
            this.textoApunte = textoApunte;

        }
        // Inspectores
        protected void onPreExecute()
        {
            Toast.makeText(modificacion_apuntes.this, "Modificando...",
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
                        .path("/ApiRest/apuntes.php")
                        .appendQueryParameter("idApunte", this.idApunte)
                        .appendQueryParameter("fechaApunte", this.fechaApunte)
                        .appendQueryParameter("textoApunte", this.textoApunte)
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
            Toast.makeText(modificacion_apuntes.this, "Actualizando datos...",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
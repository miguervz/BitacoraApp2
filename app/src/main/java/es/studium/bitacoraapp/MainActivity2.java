package es.studium.bitacoraapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import es.studium.bitacoraapp.controlador.AdapterApuntes;
import es.studium.bitacoraapp.controlador.ItemClickListener;
import es.studium.bitacoraapp.controlador.RecyclerTouchListener;
import es.studium.bitacoraapp.modelos.Apuntes;

public class MainActivity2 extends AppCompatActivity {

    private TextView nombreBitacora;
    private RecyclerView recyclerView;
    private AdapterApuntes adapterApuntes;
    private Apuntes apuntes;
    public static List<Apuntes> items;
    private String server ="192.168.0.253";
    public static int idApunte = -1;

    private BajaRemota bajaRemota;
    private AccesoRemoto acceso;

    JSONArray result;
    JSONObject jsonobject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        nombreBitacora = findViewById(R.id.txtBitacoraApuntes);
        nombreBitacora.setText(MainActivity.items.get(MainActivity.id).getCuaderno());

        acceso = new AccesoRemoto();
        acceso.execute();

        items = new ArrayList<Apuntes>();
        recyclerView = findViewById(R.id.myRecyclerView);
        adapterApuntes = new AdapterApuntes(MainActivity2.this, items, new ItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                if(v.getId()==(R.id.imagen)){
                    idApunte = position;
                    Intent intent = new Intent(MainActivity2.this, modificacion_apuntes.class);
                    startActivity(intent);
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterApuntes);

        //Click Largo
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onLongClick(View view, final int position) {
                final Apuntes apunteParaEliminar = items.get(position);
                AlertDialog dialog = new AlertDialog.Builder(MainActivity2.this)
                        .setPositiveButton("Sí, eliminar",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Metodo para eliminar
                                        bajaRemota = new BajaRemota(items.get(position).getIdApunte()+"");
                                        bajaRemota.execute();
                                        Intent intent =  new Intent(MainActivity2.this, MainActivity2.class);
                                        startActivity(intent);
                                    }
                                })
                        .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                        .setTitle("Eliminar Bitácora")
                        .setMessage("¿Eliminar Apunte "+items.get(position).getTexto()+"?")
                        .create();
                dialog.show();


            }
        }));

    }


    public void volver(View view){
        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
        startActivity(intent);
    }

    public void altaApunte(View view){
        Intent intent = new Intent(MainActivity2.this, alta_apuntes.class);
        startActivity(intent);
    }

    public class AccesoRemoto extends AsyncTask<Void, Void, String>
    {
        String idApunte = "";
        String fechaApunte = "";
        String textoApunte = "";
        String idCuadernoFK = "";

        protected void onPreExecute()
        {
            Toast.makeText(MainActivity2.this, "Obteniendo datos...",
                    Toast.LENGTH_SHORT).show();
        }

        protected String doInBackground(Void... argumentos)
        {
            try
            {
                // Crear la URL de conexión al API
                URL url = new
                        URL("http://"+server+"/ApiRest/apuntes.php?idCuaderno="+(MainActivity.items.get(MainActivity.id).getIdCuaderno()));
                // Crear la conexión HTTP
                HttpURLConnection myConnection = (HttpURLConnection)
                        url.openConnection();
                // Establecer método de comunicación. Por defecto GET.
                myConnection.setRequestMethod("GET");
                if (myConnection.getResponseCode() == 200)
                {
                    // Conexión exitosa
                    // Creamos Stream para la lectura de datos desde el servidor
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    // Creamos Buffer de lectura
                    BufferedReader bR = new
                            BufferedReader(responseBodyReader);
                    String line = "";
                    StringBuilder responseStrBuilder = new StringBuilder();
                    // Leemos el flujo de entrada
                    while ((line = bR.readLine()) != null)
                    {
                        responseStrBuilder.append(line);
                    }
                    // Parseamos respuesta en formato JSON
                    result = new JSONArray(responseStrBuilder.toString());

                    for(int i=0; i < result.length(); i++){
                        // Sacamos todos los datos
                        jsonobject = result.getJSONObject(i);
                        // Sacamos dato a dato obtenido
                        idApunte = jsonobject.getString("idApunte");
                        idCuadernoFK = jsonobject.getString("idCuadernoFK");
                        String fechaAmericana = jsonobject.getString("fechaApunte");
                        textoApunte = jsonobject.getString("textoApunte");
                        String[] fecha = fechaAmericana.split("-");
                        fechaApunte = fecha[2]+"/"+fecha[1]+"/"+fecha[0];
                        apuntes = new Apuntes(Integer.parseInt(idApunte),fechaApunte, textoApunte, R.drawable.ic_kisspng_computer_icons_editing_portable_network_graphics_i_edit_profile_svg_png_icon_free_download_1, Integer.parseInt(idCuadernoFK));
                        items.add(apuntes);
                    }
                    responseBody.close();
                    responseBodyReader.close();
                    myConnection.disconnect();
                }
                else
                {
                    // Error en la conexión
                    Log.println(Log.ERROR,"Error", "¡Conexión fallida!");
                }
            }
            catch (Exception e)
            {
                Log.println(Log.ERROR,"Error", "¡Conexión fallida!");
            }
            return (null);
        }

        protected void onPostExecute(String mensaje)
        {
            //Toast.makeText(MainActivity2.this, items.toString(), Toast.LENGTH_LONG).show();

        }
    }

    private class BajaRemota extends AsyncTask<Void, Void, String>
    {
        // Atributos

        String idApunte;
        // Constructor
        public BajaRemota(String id)
        {
            this.idApunte = id;
        }
        // Inspectores
        protected void onPreExecute()
        {
            Toast.makeText(MainActivity2.this, "Eliminando...",
                    Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(Void... voids)
        {
            try
            {
                // Crear la URL de conexión al API
                URI baseUri = new
                        URI("http://"+server+"/ApiRest/apuntes.php");
                String[] parametros = {"id",this.idApunte};
                URI uri = applyParameters(baseUri, parametros);
                // Create connection
                HttpURLConnection myConnection = (HttpURLConnection)
                        uri.toURL().openConnection();
                // Establecer método. Por defecto GET.
                myConnection.setRequestMethod("DELETE");
                if (myConnection.getResponseCode() == 200)
                {
                    // Success
                    Log.println(Log.ASSERT,"Resultado", "Registro borrado");
                    myConnection.disconnect();
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
            Toast.makeText(MainActivity2.this, "Actualizando datos...",
                    Toast.LENGTH_SHORT).show();
        }
        URI applyParameters(URI uri, String[] urlParameters)
        {
            StringBuilder query = new StringBuilder();
            boolean first = true;
            for (int i = 0; i < urlParameters.length; i += 2)
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    query.append("&");
                }
                try
                {
                    query.append(urlParameters[i]).append("=")
                            .append(URLEncoder.encode(urlParameters[i + 1],
                                    "UTF-8"));
                }
                catch (UnsupportedEncodingException ex)
                {
                    /* As URLEncoder are always correct, this exception
                     * should never be thrown. */
                    throw new RuntimeException(ex);
                }
            }
            try
            {
                return new URI(uri.getScheme(), uri.getAuthority(),
                        uri.getPath(), query.toString(), null);
            }
            catch (Exception ex)
            {
                /* As baseUri and query are correct, this exception
                 * should never be thrown. */
                throw new RuntimeException(ex);
            }
        }
    }
}
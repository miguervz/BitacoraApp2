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

import es.studium.bitacoraapp.controlador.AdapterCuadernos;
import es.studium.bitacoraapp.controlador.ItemClickListener;
import es.studium.bitacoraapp.controlador.RecyclerTouchListener;
import es.studium.bitacoraapp.modelos.Apuntes;
import es.studium.bitacoraapp.modelos.Cuadernos;

public class MainActivity extends AppCompatActivity  {


    private RecyclerView recyclerView;
    private AdapterCuadernos adapterCuadernos;
    private Cuadernos cuadernos;
    private Apuntes apuntes;
    public static List<Cuadernos> items;
    private  List<Apuntes> items2;
    public static String server ="192.168.0.253";
    public static int id = -1;
    public static int id2;
    public static AccesoRemoto acceso;
    public static AccesoRemoto2 acceso2;
    private BajaRemota bajaRemota;

    JSONArray result;
    JSONObject jsonobject;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        acceso = new AccesoRemoto();
        acceso.execute();
        items = new ArrayList<Cuadernos>();

        recyclerView = findViewById(R.id.myRecyclerView);
        adapterCuadernos = new AdapterCuadernos(MainActivity.this, items, new ItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                switch (v.getId()){
                    case R.id.cuaderno:
                        id = position;
                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        startActivity(intent);
                        break;
                    case R.id.imagen:
                        id = position;
                        Intent intent2 = new Intent(MainActivity.this, modificacion_cuadernos.class);
                        startActivity(intent2);
                        break;
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterCuadernos);

        //Click Largo
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onLongClick(View view, final int position) {
                final Cuadernos cuadernoParaEliminar = items.get(position);
                id2 = position;
                acceso2 = new AccesoRemoto2();
                acceso2.execute();
                items2 = new ArrayList<Apuntes>();
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setPositiveButton("Sí, eliminar",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(items2.size() == 0){
                                            //Metodo para eliminar
                                            bajaRemota = new BajaRemota(items.get(position).getIdCuaderno()+"");
                                            bajaRemota.execute();
                                            Intent intent =  new Intent(MainActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                        else {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                            builder.setMessage("Primero elimine el contenido.")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // FIRE ZE MISSILES!
                                                        }
                                                    })
                                                    .setTitle("ERROR")
                                            . create();
                                            // Create the AlertDialog object and return it
                                            builder.show();
                                            items2.clear();
                                        }
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
                        .setMessage("¿Eliminar Bitácora "+items.get(position).getCuaderno()+"?")
                        .create();
                dialog.show();


            }
        }));

    }

    public void altaCuaderno(View view){
        Intent intent = new Intent(MainActivity.this, alta_cuadernos.class);
        startActivity(intent);
    }

    class AccesoRemoto extends AsyncTask<Void, Void, String>
    {
        String idCuaderno = "";
        String nombreCuaderno = "";

        protected void onPreExecute()
        {
            Toast.makeText(MainActivity.this, "Obteniendo datos...",
                    Toast.LENGTH_SHORT).show();
        }

        protected String doInBackground(Void... argumentos)
        {
            try
            {
                // Crear la URL de conexión al API
                URL url = new
                        URL("http://"+server+"/ApiRest/cuadernos.php");
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
                        idCuaderno = jsonobject.getString("idCuaderno");
                        nombreCuaderno = jsonobject.getString("nombreCuaderno");
                        cuadernos = new Cuadernos(Integer.parseInt(idCuaderno),nombreCuaderno,R.drawable.ic_kisspng_computer_icons_editing_portable_network_graphics_i_edit_profile_svg_png_icon_free_download_1);
                        items.add(cuadernos);
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

        }
    }

    private class BajaRemota extends AsyncTask<Void, Void, String>
    {
        // Atributos

        String idCuaderno; 
        // Constructor
        public BajaRemota(String id)
        {
            this.idCuaderno = id;
        }
        // Inspectores
        protected void onPreExecute()
        {
            //Toast.makeText(MainActivity.this, "Eliminando...",
                    //Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(Void... voids)
        {
            try
            {
                // Crear la URL de conexión al API
                URI baseUri = new
                        URI("http://"+server+"/ApiRest/cuadernos.php");
                String[] parametros = {"id",this.idCuaderno};
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
                    Intent intent =  new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);

                }
                else
                {
                    // Error handling code goes here
                    Log.println(Log.ASSERT,"Error", "Error");
                    Toast.makeText(MainActivity.this, "ERROR!!! Para borrar un Bitácora debe estar vacío.",
                            Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(MainActivity.this, "Actualizando datos...",
                   // Toast.LENGTH_SHORT).show();
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

    public class AccesoRemoto2 extends AsyncTask<Void, Void, String>
    {
        String idApunte = "";
        String fechaApunte = "";
        String textoApunte = "";
        String idCuadernoFK = "";

        protected void onPreExecute()
        {
            Toast.makeText(MainActivity.this, "Obteniendo datos...",
                    Toast.LENGTH_SHORT).show();
        }

        protected String doInBackground(Void... argumentos)
        {
            try
            {
                // Crear la URL de conexión al API
                URL url = new
                        URL("http://"+server+"/ApiRest/apuntes.php?idCuaderno="+(items.get(id2).getIdCuaderno()));
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
                        items2.add(apuntes);
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
}
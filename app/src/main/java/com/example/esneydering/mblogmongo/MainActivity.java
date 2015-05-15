package com.example.esneydering.mblogmongo;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity  {
    private EditText dni;
    private EditText nombre;
    private EditText telefono;
    private EditText email;
    private Button insertar;
    private Button mostrar;
    private ImageButton mas;
    private ImageButton menos;
    private int posicion=0;
    private List<Personas> listaPersonas;
    private Personas personas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_main);
       //ARRAY PERSONA
        listaPersonas=new ArrayList<Personas>();

        //atributtos layout persona
        dni=(EditText)findViewById(R.id.dni);
        nombre=(EditText)findViewById(R.id.nombre);
        telefono=(EditText)findViewById(R.id.telefono);
        email=(EditText)findViewById(R.id.email);
        //Insertamos los datos de la persona.
        insertar=(Button)findViewById(R.id.insertar);
        insertar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!dni.getText().toString().trim().equalsIgnoreCase("")||
                        !nombre.getText().toString().trim().equalsIgnoreCase("")||
                        !telefono.getText().toString().trim().equalsIgnoreCase("")||
                        !email.getText().toString().trim().equalsIgnoreCase(""))

                    new Insertar(MainActivity.this).execute();

                else
                    Toast.makeText(MainActivity.this, "Hay información por rellenar", Toast.LENGTH_LONG).show();
            }

        });
        //Mostramos los datos de la persona por pantalla.
        mostrar=(Button)findViewById(R.id.mostrar);
        mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Mostrar().execute();
            }
        });
        //Se mueve por nuestro ArrayList mostrando el objeto posterior.
        mas=(ImageButton)findViewById(R.id.mas);
        //Se mueve por nuestro ArrayList mostrando el objeto anterior
        menos=(ImageButton)findViewById(R.id.menos);
    }

    //Inserta los datos de las Personas en el servidor.
    private boolean insertar(){
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpPost httppost;
        httpclient=new DefaultHttpClient();
        httppost= new HttpPost("http://192.168.1.52/app/insert.php"); // Url del Servidor
        //Añadimos nuestros datos
        nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("dni",dni.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("nombre",nombre.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("telefono",telefono.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("email",email.getText().toString().trim()));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpclient.execute(httppost);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //AsyncTask para insertar Personas
    class Insertar extends AsyncTask<String,String,String>{

        private Activity context;

        Insertar(Activity context){
            this.context=context;
        }
        @Override
        protected String doInBackground(String... params) {

            if(insertar())
                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {

                        Toast.makeText(context, "Registro insertado con éxito", Toast.LENGTH_LONG).show();
                        nombre.setText("");
                        dni.setText("");
                        telefono.setText("");
                        email.setText("");
                    }
                });
            else
                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {

                        Toast.makeText(context, "Registrto no insertado con éxito", Toast.LENGTH_LONG).show();
                    }
                });
            return null;
        }
    }


    //método paramostrar personas
    private String mostrar(){
        HttpPost httppost;
        HttpClient httpclient = new DefaultHttpClient();
        httppost= new HttpPost("http://192.168.1.52/app/listadoPersonas.php"); // Url del Servidor
        String resultado="";
        HttpResponse response;
        try {
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream  instream = entity.getContent();
            resultado= convertStreamToString(instream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }


    // Este método convertirá objetos de tipo InputStream en objetos de tipo String.
    private String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    //método filtrar
    private boolean filtrarDatos(){
        listaPersonas.clear();
        String data=mostrar();
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try {
                json = new JSONObject(data);

                JSONArray jsonArray = json.optJSONArray("personas");
                for (int i = 0; i < jsonArray.length(); i++) {
                    personas=new Personas();
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    personas.setIdentificacion(jsonArrayChild.optString("dni"));
                    personas.setNombre(jsonArrayChild.optString("nombre"));
                    personas.setTelefono(jsonArrayChild.optString("telefono"));
                    personas.setEmail(jsonArrayChild.optString("email"));
                    listaPersonas.add(personas);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    //mostrar persona
    private void mostrarPersona(final int posicion){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                Personas personas=listaPersonas.get(posicion);
                nombre.setText(personas.getNombre());
                dni.setText(personas.getIdentificacion());
                telefono.setText(personas.getTelefono());
                email.setText(personas.getEmail());
            }
        });
    }
//consumo el método mostrarPersona
    class Mostrar extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            if(filtrarDatos())mostrarPersona(posicion);
            return null;
        }
    }
}
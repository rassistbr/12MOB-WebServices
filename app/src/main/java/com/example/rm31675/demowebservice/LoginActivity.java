package com.example.rm31675.demowebservice;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etUsuario)
    EditText etUsuario;

    @BindView(R.id.etSenha)
    EditText etSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btLogin)
    public void logar(){
        if(isConnected()) {
            new LoginTask().execute("http://10.0.2.2:3000/login");
        }
        else {
            Toast.makeText(LoginActivity.this, "Não conectado!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isConnected(){
        ConnectivityManager connMan = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);

        NetworkInfo info = connMan.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }


    private class LoginTask extends AsyncTask<String, Void, String> {

        String nomeUsuario;
        String senha;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nomeUsuario = etUsuario.getText().toString();
            senha = etSenha.getText().toString();
        }

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            try{
                URL url = new URL(strings[0]);
                JSONObject usuario = new JSONObject();
                usuario.put("nomeUsuario",usuario);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(usuario));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();
                }
                else{
                    return new String("false : " + responseCode);
                }

            }catch (MalformedURLException e ){
                result = e.getMessage();
            }catch (JSONException je) {
                result = je.getMessage();
            }catch (IOException he) {
                result = he.getMessage();
            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
        }

        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while(itr.hasNext()){

                String key= itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }
    }
}

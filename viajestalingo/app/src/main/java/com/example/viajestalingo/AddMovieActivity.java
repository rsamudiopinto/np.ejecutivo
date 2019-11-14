package com.example.viajestalingo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import  com.example.viajestalingo.helper.CheckNetworkStatus;
import  com.example.viajestalingo.helper.HttpJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddMovieActivity extends AppCompatActivity {
    private static final String KEY_COMPANY = "company";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_AUTO_NAME = "auto_name";
    private static final String KEY_FECHA = "fecha";
    private static final String KEY_SALIDA = "salida";
    private static final String KEY_RATING = "rating";

    private static final String BASE_URL = "http://etalingo.dx.am/p_dinamic/viajescar/";
    private static String STRING_EMPTY = "";
    private EditText autoNameEditText;
    private EditText salidaEditText;
    private EditText ratingEditText;
    private String autoName;
    private String salida;
    private String rating;
    private Button addButton;
    private int success;
    private ProgressDialog pDialog;
    private Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
        autoNameEditText = (EditText) findViewById(R.id.txtAutoNameAdd);

        DateFormat h_salida = new SimpleDateFormat("HH:mm:ss");

        salidaEditText = (EditText) findViewById(R.id.txtSalidaAdd);
        salidaEditText.setText(h_salida.format(date));

        ratingEditText = (EditText) findViewById(R.id.txtRatingAdd);
        addButton = (Button) findViewById(R.id.btnAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    addMovie();
                } else {
                    Toast.makeText(AddMovieActivity.this,
                            "No hay Internet",
                            Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    /**
     * Checks whether all files are filled. If so then calls AddMovieAsyncTask.
     * Otherwise displays Toast message informing one or more fields left empty
     *                 !STRING_EMPTY.equals(yearEditText.getText().toString()) &&
     */
    private void addMovie() {
        if (!STRING_EMPTY.equals(autoNameEditText.getText().toString()) &&
                !STRING_EMPTY.equals(salidaEditText.getText().toString()) &&
                !STRING_EMPTY.equals(ratingEditText.getText().toString())) {

            autoName = autoNameEditText.getText().toString();
            salida = salidaEditText.getText().toString();
            rating = ratingEditText.getText().toString();
            new AddMovieAsyncTask().execute();
        } else {
            Toast.makeText(AddMovieActivity.this,
                    "Ingrese toda la información!",
                    Toast.LENGTH_LONG).show();

        }


    }

    /**
     * AsyncTask for adding a movie
     */
    private class AddMovieAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display proggress bar
            pDialog = new ProgressDialog(AddMovieActivity.this);
            pDialog.setMessage("Agregando Viaje. Espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            //Populating request parameters

            httpParams.put(KEY_COMPANY, "1546");
            httpParams.put(KEY_AUTO_NAME, autoName);
            httpParams.put(KEY_SALIDA, salida);
            httpParams.put(KEY_RATING, rating);

            DateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");

            httpParams.put(KEY_FECHA,fecha.format(date));

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "add_movie.php", "POST", httpParams);
            try {
                success = jsonObject.getInt(KEY_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 1) {
                        //Display success message
                        Toast.makeText(AddMovieActivity.this,
                                "Agregando Viaje", Toast.LENGTH_LONG).show();
                        Intent i = getIntent();
                        //send result code 20 to notify about movie update
                        setResult(20, i);
                        //Finish ths activity and go back to listing activity
                        finish();

                    } else {
                        Toast.makeText(AddMovieActivity.this,
                                "Error al crear el viaje",
                                Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}

package com.example.viajestalingo;

import android.app.ProgressDialog;
//import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//import android.app.AlertDialog;

import com.example.viajestalingo.helper.CheckNetworkStatus;
import com.example.viajestalingo.helper.HttpJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MovieUpdateDeleteActivity extends AppCompatActivity {
    private static String STRING_EMPTY = "";
    private static final String KEY_COMPANY = "company";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_AUTO_ID = "id";
    private static final String KEY_AUTO_NAME = "auto";
    private static final String KEY_SALIDA = "salida";
    private static final String KEY_LLEGADA = "llegada";
    private static final String KEY_PASAJEROS = "pasajeros";
    private static final String BASE_URL = "http://etalingo.dx.am/p_dinamic/viajescar/";
    private String autoId;
    private EditText autoNameEditText;
    private EditText salidaEditText;
    private EditText llegadaEditText;
    private EditText ratingEditText;
    private String autoName;
    private String salida;
    private String llegada;
    private String pasajeros;
    //private Button deleteButton;
    private Button updateButton;
    private int success;
    private ProgressDialog pDialog;
    private Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_update_delete);
        Intent intent = getIntent();
        autoNameEditText = (EditText) findViewById(R.id.txtAutoNameUpdate);
        salidaEditText = (EditText) findViewById(R.id.txtsalidaUpdate);
        llegadaEditText = (EditText) findViewById(R.id.txtLlegadaUpdate);
        DateFormat h_salida = new SimpleDateFormat("HH:mm:ss");
        llegadaEditText.setText(h_salida.format(date));
        ratingEditText = (EditText) findViewById(R.id.txtRatingUpdate);

        autoId = intent.getStringExtra(KEY_AUTO_ID);
        new FetchMovieDetailsAsyncTask().execute();
        //deleteButton = (Button) findViewById(R.id.btnDelete);
        /*deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });*/
        updateButton = (Button) findViewById(R.id.btnUpdate);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    updateMovie();

                } else {
                    Toast.makeText(MovieUpdateDeleteActivity.this,
                            "No Hay Internet",
                            Toast.LENGTH_LONG).show();

                }

            }
        });


    }

    /**
     * Fetches single movie details from the server
     */
    private class FetchMovieDetailsAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(MovieUpdateDeleteActivity.this);
            pDialog.setMessage("Buscando Viaje, espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            httpParams.put(KEY_AUTO_ID, autoId);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "get_movie_details.php", "GET", httpParams);
            try {
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONObject movie;
                if (success == 1) {
                    //Parse the JSON response
                    movie = jsonObject.getJSONObject(KEY_DATA);
                    autoName = movie.getString(KEY_AUTO_NAME);
                    salida = movie.getString(KEY_SALIDA);
                    //year = movie.getString(KEY_LLEGADA);
                    pasajeros = movie.getString(KEY_PASAJEROS);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    //Populate the Edit Texts once the network activity is finished executing
                    autoNameEditText.setText(autoName);
                    salidaEditText.setText(salida);
                    //yearEditText.setText(year);
                    ratingEditText.setText(pasajeros);

                }
            });
        }


    }

    /**
     * Displays an alert dialogue to confirm the deletion
     */
    /*private void confirmDelete() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MovieUpdateDeleteActivity.this);
        alertDialogBuilder.setMessage("Are you sure, you want to delete this movie?");
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                            //If the user confirms deletion, execute DeleteMovieAsyncTask
                            new DeleteMovieAsyncTask().execute();
                        } else {
                            Toast.makeText(MovieUpdateDeleteActivity.this,
                                    "Unable to connect to internet",
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }*/

    /**
     * AsyncTask to delete a movie
     */
   /* private class DeleteMovieAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(MovieUpdateDeleteActivity.this);
            pDialog.setMessage("Deleting Movie. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            //Set movie_id parameter in request
            httpParams.put(KEY_MOVIE_ID, movieId);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "delete_movie.php", "POST", httpParams);
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
                        Toast.makeText(MovieUpdateDeleteActivity.this,
                                "Movie Deleted", Toast.LENGTH_LONG).show();
                        Intent i = getIntent();
                        //send result code 20 to notify about movie deletion
                        setResult(20, i);
                        finish();

                    } else {
                        Toast.makeText(MovieUpdateDeleteActivity.this,
                                "Some error occurred while deleting movie",
                                Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }*/

    /**
     * Checks whether all files are filled. If so then calls UpdateMovieAsyncTask.
     * Otherwise displays Toast message informing one or more fields left empty
     */
    private void updateMovie() {

        if (!STRING_EMPTY.equals(autoNameEditText.getText().toString()) &&
                !STRING_EMPTY.equals(salidaEditText.getText().toString()) &&
                !STRING_EMPTY.equals(llegadaEditText.getText().toString()) &&
                !STRING_EMPTY.equals(ratingEditText.getText().toString())) {

            autoName = autoNameEditText.getText().toString();
            salida = salidaEditText.getText().toString();
            llegada = llegadaEditText.getText().toString();
            pasajeros = ratingEditText.getText().toString();
            new UpdateMovieAsyncTask().execute();
        } else {
            Toast.makeText(MovieUpdateDeleteActivity.this,
                    "Complete la Información!",
                    Toast.LENGTH_LONG).show();

        }


    }
    /**
     * AsyncTask for updating a movie details
     */

    private class UpdateMovieAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(MovieUpdateDeleteActivity.this);
            pDialog.setMessage("Cerrando Viaje, espere...");
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
            httpParams.put(KEY_AUTO_ID, autoId);
            httpParams.put(KEY_AUTO_NAME, autoName);
            httpParams.put(KEY_SALIDA, salida);
            httpParams.put(KEY_LLEGADA, llegada);
            httpParams.put(KEY_PASAJEROS, pasajeros);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "update_movie.php", "POST", httpParams);
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
                        Toast.makeText(MovieUpdateDeleteActivity.this,
                                "Viaje Cerrado", Toast.LENGTH_LONG).show();
                        Intent i = getIntent();
                        //send result code 20 to notify about movie update
                        setResult(20, i);
                        finish();

                    } else {
                        Toast.makeText(MovieUpdateDeleteActivity.this,
                                "Error al Cerrar Viaje",
                                Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}
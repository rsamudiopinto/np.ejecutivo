package com.example.viajestalingo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.widget.Toast;
import com.example.viajestalingo.helper.HttpJsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ListViajes extends AppCompatActivity {
    private static final String KEY_COMPANY = "company";
    private static final String KEY_FECHA = "fecha";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_VIAJE_ID = "id";
    private static final String KEY_AUTO_NAME = "auto";
    private static final String KEY_CANTIDAD = "cantidad";
    private static final String BASE_URL = "http://etalingo.dx.am/p_dinamic/viajescar/";

    private ArrayList<HashMap<String, String>> viajesList;
    private HashMap<String, String> map;
    private ListView viajesListView;
    private ProgressDialog pDialog;
    private Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_row);
        viajesList = new ArrayList<HashMap<String, String>>();

        viajesListView = (ListView) findViewById(R.id.SCHEDULE);
        new FetchMoviesAsyncTask().execute();
    }

    /**
     * Fetches the list of movies from the server
     */
    private class FetchMoviesAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(ListViajes.this);
            pDialog.setMessage("Buscando Viajes, espere..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            httpParams.put(KEY_COMPANY, "1546");
            DateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");

            httpParams.put(KEY_FECHA,fecha.format(date));

            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "informeviajes.php", "GET", httpParams);
            try {
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONArray movies;
                if (success == 1) {

                    movies = jsonObject.getJSONArray(KEY_DATA);
                    //Iterate through the response and populate movies list
                    for (int i = 0; i < movies.length(); i++) {
                        JSONObject movie = movies.getJSONObject(i);
                        Integer viajeId = movie.getInt(KEY_VIAJE_ID);
                        String autoName = movie.getString(KEY_AUTO_NAME);
                        String cantidadv = movie.getString(KEY_CANTIDAD);
                        map = new HashMap<String, String>();
                        map.put("cell1", viajeId.toString());
                        map.put("cell2", autoName);
                        map.put("cell3", cantidadv);
                        viajesList.add(map);
                    }

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
                    populateMovieList();
                }
            });
        }

    }

    /**
     * Updating parsed JSON data into ListView
     * */
    private void populateMovieList() {
        SimpleAdapter mSchedule;
        mSchedule = new SimpleAdapter(ListViajes.this, viajesList, R.layout.informe_viajes,
                new String[] {"cell1", "cell2", "cell3"}, new int[] {R.id.NUM_CELL,R.id.AUTO_CELL, R.id.CANTIDAD_CELL});
        viajesListView.setAdapter(mSchedule);
        /*ListAdapter adapter = new SimpleAdapter(
                ListViajes.this, viajesList,
                R.layout.list_item, new String[]{KEY_VIAJE_ID,
                KEY_AUTO_NAME},
                new int[]{R.id.movieId, R.id.movieName});
        // updating listview
        viajesListView.setAdapter(adapter);
        //Call MovieUpdateDeleteActivity when a movie is clicked
        viajesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Check for network connectivity
                /*if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    String movieId = ((TextView) view.findViewById(R.id.movieId))
                            .getText().toString();
                    Intent intent = new Intent(getApplicationContext(),
                            MovieUpdateDeleteActivity.class);
                    intent.putExtra(KEY_VIAJE_ID, movieId);
                    startActivityForResult(intent, 20);

                } else {
                    Toast.makeText(ListViajes.this,
                            "Seleccionado",
                            Toast.LENGTH_LONG).show();

               // }


            }
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 20) {
            // If the result code is 20 that means that
            // the user has deleted/updated the movie.
            // So refresh the movie listing
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

}

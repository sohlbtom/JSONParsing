package com.example.android.jsonparsing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView listView;
    private Integer testi=0;

    protected static String url = "http://rata.digitraffic.fi/api/v1/metadata/stations";

    ArrayList<HashMap<String, String>> stationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stationList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list);
        new dataFetcher().execute();
    }
        class dataFetcher extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Http Request Code start
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.d("Response from url: ", jsonStr);
            // Http Request Code end
            // Json Parsing Code Start
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i <jsonArray.length(); i++)
                {
                    HashMap<String, String> station = new HashMap<>();
                    JSONObject jsonObjectStation = jsonArray.getJSONObject(i);
                    String passengerTraffic = jsonObjectStation.getString("passengerTraffic");

                    //Laitettu ehto, jotta tulostaa listalle vain kaupallisen matkustajaliikenteen asemat.
                    if(passengerTraffic == "true") {
                        station.put("stationShortCode", jsonObjectStation.getString("stationShortCode"));
                        station.put("stationName", jsonObjectStation.getString("stationName"));
                        station.put("stationUICCode", jsonObjectStation.getString("stationUICCode"));
                        stationList.add(station);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//Json Parsing code end
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();

            final ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, stationList,
                    R.layout.list_item, new String[]{"stationName", "stationShortCode",
                    "stationUICCode"}, new int[]{R.id.stationName,
                    R.id.stationShortCode, R.id.stationUICCode});


            listView.setAdapter(adapter);
            listView.setClickable(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this, TrainPerStation.class);
                    intent.putExtra("stationShortCode",stationList.indexOf(i));
                    startActivity(intent);
                }
            });
        }
    }
}

package com.example.android.jsonparsing;

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
import android.widget.ListView;

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

public class MainActivity extends AppCompatActivity {

    ListView listView;
    StationAdapter adapter;
    ArrayList<Station> stationArrayList;
    DBHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);
        handler = new DBHandler(this);
        NetworkUtils utils = new NetworkUtils(MainActivity.this);
        if (handler.getStationCount() == 0 && utils.isConnectingToInternet())
        {
            new DataFetcherTask().execute();
        }
        else
        {
            ArrayList<Station> cityList = handler.getAllStation();
            adapter = new StationAdapter(MainActivity.this,cityList);
            listView.setAdapter(adapter);
        }
    }
    class DataFetcherTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //String serverData = null;// String object to store fetched data from server
            String stream = null;
            // Http Request Code start
            //DefaultHttpClient httpClient = new DefaultHttpClient();
            //HttpGet httpGet = new HttpGet("http://rata.digitraffic.fi/api/v1/metadata/stations");
            try {
                URL url = new URL("http://rata.digitraffic.fi/api/v1/metadata/stations");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line);
                }
                stream = sb.toString();
                // End reading...............
                Log.d("response", stream);
                // Disconnect the HttpURLConnection
                urlConnection.disconnect();
                //HttpResponse httpResponse = httpClient.execute(httpGet);
                //HttpEntity httpEntity = httpResponse.getEntity();
                //serverData = EntityUtils.toString(httpEntity);

            } catch (IOException e) {
                e.printStackTrace();
            }
// Http Request Code end
// Json Parsing Code Start
            try {
                stationArrayList = new ArrayList<>();
                //JSONObject jsonObject = new JSONObject(serverData);
                JSONArray jsonArray = new JSONArray(stream);
                for (int i = 0; i <jsonArray.length(); i++)
                {
                    JSONObject jsonObjectStation = jsonArray.getJSONObject(i);

                    String stationName = jsonObjectStation.getString("stationName");
                    String stationShortCode = jsonObjectStation.getString("stationShortCode");
                    int stationUICCode = jsonObjectStation.getInt("stationUICCode");
                    Station station = new Station();
                    station.setStationName(stationName);
                    station.setStationShortCode(stationShortCode);
                    station.setStationUICCode(stationUICCode);
                    handler.addStation(station);// Inserting into DB
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
            ArrayList<Station> stationList = handler.getAllStation();
            adapter = new StationAdapter(MainActivity.this,stationList);
            listView.setAdapter(adapter);
        }
    }
}

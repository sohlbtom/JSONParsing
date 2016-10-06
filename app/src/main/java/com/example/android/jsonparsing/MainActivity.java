package com.example.android.jsonparsing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView listView;
    private Integer testi=0;

    protected static String url = "http://rata.digitraffic.fi/api/v1/metadata/stations";

    ArrayList<Station> stationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

                    JSONObject jsonObjectStation = jsonArray.getJSONObject(i);
                    String passengerTraffic = jsonObjectStation.getString("passengerTraffic");

                    //Laitettu ehto, jotta tulostaa listalle vain kaupallisen matkustajaliikenteen asemat.
                    if(passengerTraffic == "true") {
                        String stationShortCode = jsonObjectStation.getString("stationShortCode");
                        String stationName = jsonObjectStation.getString("stationName");
                        String stationUICCode = jsonObjectStation.getString("stationUICCode");
                        stationList.add(new Station(stationName, stationShortCode, stationUICCode));
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


        ArrayAdapter adapter = new ArrayAdapter<Station>(MainActivity.this, R.layout.list_item, R.id.stationName, stationList);


            listView.setAdapter(adapter);
            listView.setClickable(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this, TrainPerStation.class);
                    intent.putExtra("stationShortCode", stationList.get(i).getStationShortCode().toString());
                    startActivity(intent);
                }
            });
        }
    }
}

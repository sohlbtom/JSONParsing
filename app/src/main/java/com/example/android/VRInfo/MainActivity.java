package com.example.android.VRInfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView listView;
    EditText searchBox;
    public String searchTerm = "", jsonStr;

    //testi

    protected static String url = "http://rata.digitraffic.fi/api/v1/metadata/stations";

    ArrayList<Station> stationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        new dataFetcher().execute();
    }

    class dataFetcher extends AsyncTask<Void, Void, Void> {

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
            jsonStr = sh.makeServiceCall(url);
            Log.d("Response from url: ", jsonStr);
            parsiJson();
            // Http Request Code end
            // Json Parsing Code Start


            return null;
        }

        protected Void parsiJson() {
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObjectStation = jsonArray.getJSONObject(i);
                    String passengerTraffic = jsonObjectStation.getString("passengerTraffic");
                    String stationName = jsonObjectStation.getString("stationName");


                    //Laitettu ehto, jotta tulostaa listalle vain kaupallisen matkustajaliikenteen asemat.
                    if (passengerTraffic == "true" && stationName.toLowerCase().startsWith(searchTerm)) {
                        String stationShortCode = jsonObjectStation.getString("stationShortCode");

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

            parsiJson();


            final ArrayAdapter adapter = new ArrayAdapter<Station>(MainActivity.this, R.layout.list_station, R.id.stationName, stationList);

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

            searchBox = (EditText) findViewById(R.id.search);
            searchBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //adapter.getFilter().filter(s);
                    Log.e("beforeText: ", searchTerm);

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchTerm = s.toString().toLowerCase();
                    Log.e("onTextChanged: ", searchTerm);
                    stationList.clear();
                    parsiJson();
                    adapter.notifyDataSetChanged();
                    return;


                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.e("afterTextChanged: ", searchTerm);
                    adapter.notifyDataSetChanged();
                    adapter.notifyDataSetInvalidated();
                    //adapter.getFilter().filter(s);


                }
            });


        }

        ;


    }
}


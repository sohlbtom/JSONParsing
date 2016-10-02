package com.example.android.jsonparsing;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TrainPerStation extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView listView2;
    private Integer testi=0;

    protected static String url = "http://rata.digitraffic.fi/api/v1/live-trains?station=KR&minutes_before_departure=150&minutes_after_departure=150&minutes_before_arrival=150&minutes_after_arrival=150";

    ArrayList<HashMap<String, String>> trainsPerStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_per_station);
        trainsPerStation = new ArrayList<>();
        listView2 = (ListView) findViewById(R.id.list2);
        new dataFetcher().execute();
    }
    class dataFetcher extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(TrainPerStation.this);
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

                    station.put("trainNumber", jsonObjectStation.getString("trainNumber"));

                    JSONArray timeTableRows = jsonObjectStation.getJSONArray("timeTableRows");

                    for(int j = 0; j < timeTableRows.length();j++){
                        JSONObject jsonTimeTableRow = timeTableRows.getJSONObject(j);
                        station.put("stationShortCode", jsonTimeTableRow.getString("stationShortCode"));
                        station.put("scheduledTime", jsonTimeTableRow.getString("scheduledTime"));

                    }

                    trainsPerStation.add(station);
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

            ListAdapter adapter = new SimpleAdapter(
                    TrainPerStation.this, trainsPerStation,
                    R.layout.list_train, new String[]{"trainNumber", "stationShortCode",
                    "scheduledTime"}, new int[]{R.id.trainNumber,
                    R.id.stationShortCode, R.id.scheduledTime});

            listView2.setAdapter(adapter);
        }
    }
}

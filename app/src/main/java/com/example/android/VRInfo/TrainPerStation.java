package com.example.android.VRInfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.TimeZone;

public class TrainPerStation extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView listView2;
    private Integer testi = 0;
    String shortCode, url, parsedTime, trainTime;
    //TimeZone tz = TimeZone.getDefault();
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");


    int mins, hours;


    ArrayList<LinkedHashMap<String, String>> trainsPerStation;
    LinkedHashMap<String, String> station;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_per_station);
        trainsPerStation = new ArrayList<>();
        listView2 = (ListView) findViewById(R.id.list2);
        new dataFetcher().execute();
        Intent intent = getIntent();
        shortCode = intent.getStringExtra("stationShortCode");
        sdf.setCalendar(cal);

        url = "http://rata.digitraffic.fi/api/v1/live-trains?station=" + shortCode + "&minutes_before_departure=0&minutes_after_departure=0&minutes_before_arrival=30&minutes_after_arrival=0";
    }


    class dataFetcher extends AsyncTask<Void, Void, Void> {

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
                for (int i = 0; i < jsonArray.length(); i++) {
                    station = new LinkedHashMap<>();
                    JSONObject jsonObjectStation = jsonArray.getJSONObject(i);


                    JSONArray timeTableRows = jsonObjectStation.getJSONArray("timeTableRows");

                    for (int j = 0; j < timeTableRows.length(); j++) {
                        JSONObject jsonTimeTableRow = timeTableRows.getJSONObject(j);
                        String stationShortCode = jsonTimeTableRow.getString("stationShortCode");
                        String type = jsonTimeTableRow.getString("type");

                        if (stationShortCode.equals(shortCode) && type.equals("ARRIVAL")) {
                            //haetaan junan aikataulu ja siivotaan siitä pelkkä HH:mm
                            trainTime = jsonTimeTableRow.getString("scheduledTime");

                            try {
                                //luetaan jsonista GMT aikana sisään.
                                cal.setTimeZone(TimeZone.getTimeZone(""));
                                cal.setTime(sdf.parse(trainTime));
                                //muutetaan aikavyöhyke
                                cal.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));

                                hours = cal.get(Calendar.HOUR_OF_DAY);
                                mins = cal.get(Calendar.MINUTE);
                                //string format lisää nollan jos minuutteja alle 10 16:7 -> 16:07
                                parsedTime = (hours + ":" + String.format("%02d", mins));

                                station.put("trainNumber", jsonObjectStation.getString("trainNumber"));
                                //station.put("stationShortCode", stationShortCode);
                                station.put("scheduledTime", parsedTime);
                                //tarkistetaan tuleeko JSONissa mukana diffrerenceInMinutes"-tietoa
                                if (jsonTimeTableRow.has("differenceInMinutes")) {
                                    station.put("differenceInMinutes", jsonTimeTableRow.getString("differenceInMinutes"));
                                } else {
                                    station.put("differenceInMinutes", "0");
                                }
                                trainsPerStation.add((LinkedHashMap<String, String>) station);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                }   //if (trainsPerStation.size()<1){
                //station.put("trainNumber", "N/A");
                //station.put("scheduledTime", "--");
                //station.put("differenceInMinutes", "--");
                //trainsPerStation.add((LinkedHashMap<String, String>) station);
                //}
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
                    R.layout.list_train, new String[]{"trainNumber", "differenceInMinutes",
                    "scheduledTime"}, new int[]{R.id.trainNumber,
                    R.id.differenceInMinutes, R.id.scheduledTime});

            listView2.setAdapter(adapter);
        }
    }
}

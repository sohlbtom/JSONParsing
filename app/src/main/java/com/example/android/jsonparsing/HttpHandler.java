package com.example.android.jsonparsing;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tommi on 25.9.2016.
 */

public class HttpHandler {

    public HttpHandler() {

    }

    String makeServiceCall(String reqUrl) {

        String stream = null;
        try {
            URL url = new URL(reqUrl);
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


        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }
}
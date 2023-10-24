package com.example.pr14;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loadData = (Button) findViewById(R.id.button);
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        handler = new Handler(Looper.getMainLooper());

        loadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(() -> {
                    try {
                        URL apiUrl = new URL("https://randomfox.ca/floof/");
                        HttpURLConnection httpURLConnection = (HttpURLConnection) apiUrl.openConnection();

                        int responseCode = httpURLConnection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            InputStream inputStream = httpURLConnection.getInputStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                            StringBuilder response = new StringBuilder();
                            String line;

                            while ((line = bufferedReader.readLine()) != null) {
                                response.append(line);
                            }


                            JSONObject jsonObject = new JSONObject(response.toString());
                            String imageUrl = jsonObject.getString("image");
                            new Thread(() -> {
                                try {
                                    URL ImageUrl = new URL(imageUrl);
                                    InputStream in = ImageUrl.openStream();
                                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                                    handler.post(() -> iv.setImageBitmap(bitmap));
                                } catch (Exception e) {
                                    Log.e("Error", Objects.requireNonNull(e.getMessage()));
                                    e.printStackTrace();
                                }
                            }).start();
                        } else {
                            Log.e("Error", "Error de respuesta HTTP: " + responseCode);
                        }
                    } catch (Exception e) {
                        Log.e("Error", Objects.requireNonNull(e.getMessage()));
                        e.printStackTrace();
                    }
                }).start();
            }
        });


    }


    String error = ""; // string field

    private String getDataFromUrl(String demoIdUrl) {

        String result = null;
        int resCode;
        InputStream in;
        try {
            URL url = new URL(demoIdUrl);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            resCode = httpsConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpsConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();
                result = sb.toString();
            } else {
                error += resCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }
}
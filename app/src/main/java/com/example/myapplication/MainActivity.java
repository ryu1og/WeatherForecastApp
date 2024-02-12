package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.IOException;
import java.util.Iterator;
import org.json.JSONArray;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        final TextView textView = findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();
                String url = "https://api.open-meteo.com/v1/forecast?latitude=35.6895&longitude=139.6917&daily=temperature_2m_max,temperature_2m_min&timezone=auto";

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("Network Error", "Failed to fetch weather data", e);
                        MainActivity.this.runOnUiThread(() -> textView.setText("ネットワークエラーが発生しました。"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String myResponse = response.body().string();

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(myResponse);
                                        JSONObject daily = jsonResponse.getJSONObject("daily");
                                        JSONArray dates = daily.getJSONArray("time");
                                        JSONArray maxTemps = daily.getJSONArray("temperature_2m_max");
                                        JSONArray minTemps = daily.getJSONArray("temperature_2m_min");

                                        // 最初の日のデータを使用する例
                                        String date = dates.getString(0);  // 最初の日付
                                        double maxTemp = maxTemps.getDouble(0);  // 最初の日の最高気温
                                        double minTemp = minTemps.getDouble(0);  // 最初の日の最低気温

                                        // テキストビューに表示する文字列をフォーマット
                                        String formattedText = String.format(
                                                "日付: %s\n最高気温: %.1f°C\n最低気温: %.1f°C",
                                                date, maxTemp, minTemp
                                        );
                                        textView.setText(formattedText);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        textView.setText("天気情報の解析に失敗しました。");
                                    }
                                }
                            });
                        }
                    }




                });
            }
        });
    }
}


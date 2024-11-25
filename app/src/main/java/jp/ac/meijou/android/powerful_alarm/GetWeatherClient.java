package jp.ac.meijou.android.powerful_alarm;

import androidx.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Optional;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// 後で使う 気にしないで

public class GetWeatherClient {
    /*
    // networking
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();
    private final JsonAdapter<WeatherForecast> weatherForecastJsonAdapter = moshi.adapter(WeatherForecast.class);

    private final String url = "https://weather.tsukumijima.net/api/forecast/city/230010";

    public String getWeather() {
        var request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 通信に失敗したときの処理
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // レスポンスボディをWeatherForecast型に変換
                var weatherForecast = weatherForecastJsonAdapter.fromJson(response.body().source());

                Optional.ofNullable(weatherForecast)
                        .map(wf -> wf.forecasts.get(0))
                        .map(forecast -> forecast.telop)
                        .ifPresent(todayTelop -> {
                            out = todayTelop; // これString TODO TODO TODO !!!!
                        });
            }
        });
        return out;
    }
     */
}

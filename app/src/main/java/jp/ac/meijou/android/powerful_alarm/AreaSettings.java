package jp.ac.meijou.android.powerful_alarm;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Optional;

import jp.ac.meijou.android.powerful_alarm.databinding.ActivityAreaSettingsBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AreaSettings extends AppCompatActivity {
    private ActivityAreaSettingsBinding binding;
    private PrefDataStore prefDataStore;

    // Networking Field
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();
    private final JsonAdapter<WeatherForecast> weatherForecastJsonAdapter = moshi.adapter(WeatherForecast.class);
    private final String url = "https://weather.tsukumijima.net/api/forecast/city/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_area_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityAreaSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // PrefDataStore(シングルトン)のインスタンスを取得
        prefDataStore = PrefDataStore.getInstance(this);

        // 現在の地域を表示
        binding.currentText.setText("現在: INIT_1");
        binding.weatherInfo.setText("現在の天気: INIT_1");
        prefDataStore.getString("area")
                .ifPresent(prev_area -> {
                    binding.currentText.setText("現在: " + prev_area);
                    getWeather(prev_area);
                    /*
                    スピナーの初期値を決めるプログラムを書いてもいいかも
                    と思いたちやってみようとした
                    // 取得がめんどくさい getString(R.string.area_list)でstring-array取得できない だる
                    final String[] AREAS =
                    // 下をfor文で回すと行けるかもしれんけどおもそうだし何回回せばいいかわからんし
                    binding.areaSpinner.getItemAtPosition()
                    binding.areaSpinner.setSelection(2); // スピナーの初期値
                     */
                });

        // MainActivity2に帰る！！
        binding.previous.setOnClickListener(view -> {
            Intent intent = new Intent(AreaSettings.this, MainActivity2.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        binding.confirmButton.setOnClickListener(view -> {
            String area = binding.areaSpinner.getSelectedItem().toString();
            binding.currentText.setText("現在: " + area);

            // 選択した地域をデータストアに保持する
            prefDataStore.setString("area", area);
            getWeather(area);
        });
        
        
    }


    // インターネットから天気情報を取得する関数
    protected void getWeather(String area) {
        binding.weatherInfo.setText("現在の天気: 取得中");

        String areaId = getAreaId(area);
        if (areaId.equals("0")) {
            binding.weatherInfo.setText("現在の天気: 取得失敗");
            return;
        }

        var request = new Request.Builder()
                .url(url + areaId)
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
                            binding.weatherInfo.setText("現在の天気: " + todayTelop); // TODO
                        });
            }
        });
    }


    // 地域の名前を地域のIDに変換する関数
    private String getAreaId(String area) {
        final String[] areaNames = {
                "札幌",
                "東京",
                "名古屋",
                "大阪",
                "福岡"
        };
        final String[] areaIds = {
                "016010", // 札幌
                "130010", // 東京
                "230010", // 名古屋
                "270000", // 大阪
                "400010"  // 福岡
        };

        for (int i = 0; i < areaNames.length; i++) {
            if (areaNames[i].equals(area)) {
                return areaIds[i];
            }
        }

        return "0";
    }
}
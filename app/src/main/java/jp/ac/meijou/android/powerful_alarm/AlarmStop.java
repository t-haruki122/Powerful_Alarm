package jp.ac.meijou.android.powerful_alarm;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import jp.ac.meijou.android.powerful_alarm.databinding.ActivityAlarmStopBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.os.Handler;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.TextView;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class AlarmStop extends AppCompatActivity {
    private ActivityAlarmStopBinding binding;
    private MediaPlayer mediaPlayer;
    private List<Integer> imageIdList;
    private Timer timer;
    final private int[] imageIds = {
            R.drawable.cloudy, R.drawable.rainy, R.drawable.snowy,
            R.drawable.sunny, R.drawable.cloudy_after_rainy,
            R.drawable.cloudy_after_sunny, R.drawable.lightning,
            R.drawable.torrential_rain
    };
    private ImageView[] imageList;
    private TextView[] interval;
    private int correct = R.drawable.sunny;
    private int currentIndex = 0;
    private boolean isInterval = false;

    // Networking Field
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final Moshi moshi = new Moshi.Builder().build();
    private final JsonAdapter<WeatherForecast> weatherForecastJsonAdapter = moshi.adapter(WeatherForecast.class);
    private final String url = "https://weather.tsukumijima.net/api/forecast/city/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmStopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // インターネットから最新の情報を取得
        correct = getCorrect();

        //現在時刻の取得
        ZonedDateTime current = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String currentTime = current.format(formatter);

        //音源再生
        mediaPlayer = MediaPlayer.create(this,R.raw.komorebixylophone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        imageList = new ImageView[] {
                binding.image1, binding.image2, binding.image3
        };
        interval = new TextView[] {
                binding.count3, binding.count2, binding.count1
        };
        //リストにidを代入
        imageIdList = new ArrayList<>();
        for (int id : imageIds) imageIdList.add(id);

        //現在時刻の表示
        binding.currentTime.setText(currentTime);

        //停止ボタン押下時
        binding.stop.setOnClickListener(view -> {
            checkInterval();
            binding.stop.setVisibility(View.INVISIBLE);
            randomizeImage();
        });

        binding.image1.setOnClickListener(view -> {
            if ((int)imageList[0].getTag() == correct) {
                stopAlarmSound();
                moveActivity();
            }
            else{
                count();
                randomizeImage();
            }
        });
        binding.image2.setOnClickListener(view -> {
            if ((int)imageList[1].getTag() == correct) {
                stopAlarmSound();
                moveActivity();
            }
            else {
                count();
                randomizeImage();
            }
        });
        binding.image3.setOnClickListener(view -> {
            if ((int)imageList[2].getTag() == correct) {
                stopAlarmSound();
                moveActivity();
            }
            else {
                count();
                randomizeImage();
            }
        });
    }

    //天気の画像をランダムに変更
    private void randomizeImage() {
        List<Integer> selected = new ArrayList<>();
        selected.add(correct);

        List<Integer> others = new ArrayList<>(imageIdList);
        others.remove(Integer.valueOf(correct));
        Collections.shuffle(others);

        selected.add(others.get(0));
        selected.add(others.get(1));

        Collections.shuffle(selected);

        for (int i = 0; i < imageList.length; ++i) {
            imageList[i].setImageResource(selected.get(i));
            imageList[i].setTag(selected.get(i));
        }
    }
    //アラームを止める
    private void stopAlarmSound() {
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
        for (ImageView imageView : imageList) {
            imageView.setVisibility(View.INVISIBLE);
        }
        binding.stop.setVisibility(View.VISIBLE);
    }
    //天気選択インターバル
    private void count() {
        isInterval = true;
        Handler handler = new Handler();
        Runnable timer = new Runnable() {
            @Override
            public void run() {
                if (currentIndex >= interval.length) {
                    interval[--currentIndex].setVisibility(View.INVISIBLE);
                    currentIndex = 0;
                    isInterval = false;
                    return;
                }
                for (TextView textView : interval) {
                    textView.setVisibility(View.INVISIBLE);
                }
                interval[currentIndex].setVisibility(View.VISIBLE);
                ++currentIndex;

                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(timer, 0);
    }
    private void checkInterval() {
        timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isInterval){
                            for (ImageView imageView : imageList) {
                            imageView.setVisibility(View.INVISIBLE);
                            }
                        }
                        else {
                            for (ImageView imageView : imageList) {
                                imageView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 100);
    }
    private void moveActivity() {
        Intent intent = new Intent(AlarmStop.this, MainActivity2.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }

    protected int getCorrect() {
        // Correctを取得する同期関数
        // 取得に失敗したら R.drawable.sunny を返す

        // areaデータ読み出し
        // DataStore
        PrefDataStore prefDataStore = PrefDataStore.getInstance(this);
        Optional<String> area = prefDataStore.getString("area");
        if (area.isEmpty()) return R.drawable.sunny;

        // areaデータをareaIdに変換
        String areaId = AreaSettings.getAreaId(area.get());
        if (areaId.equals("0")) return R.drawable.sunny;

        // HTTPリクエストを作成
        Request request = new Request.Builder()
                .url(url + areaId)
                .build();

        // 同期的にリクエストを実行
        // これをバックグラウンド処理にしないとAndroid3.0(API11)以降は怒られるらしい
        // つまりokHttpのRambda文を使わなければいけない
        // でもそれを使うと返り値があああああ
        String telop = "";
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) return R.drawable.sunny;
            var weatherForecast = Optional.ofNullable(
                            weatherForecastJsonAdapter.fromJson(response.body().source())
                    )
                    .map(wf -> wf.forecasts.get(0))
                    .map(forecast -> forecast.telop);
            if (weatherForecast.isEmpty()) return R.drawable.sunny;
            telop = weatherForecast.get();
        } catch (IOException e) {
            return R.drawable.sunny; // 通信エラー
        }
        if (telop.isEmpty()) return R.drawable.sunny;

        // telop (String) (晴れなど) を処理
        switch (telop.charAt(0)) {
            case '晴': return R.drawable.sunny;
            case '雨': return R.drawable.rainy;
            case '曇': return R.drawable.cloudy;
            case '雲': return R.drawable.cloudy;
            case '雪': return R.drawable.snowy;
            case '雷': return R.drawable.lightning;
            default: return R.drawable.sunny;
        }
    }
}

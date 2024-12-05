package jp.ac.meijou.android.powerful_alarm;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import jp.ac.meijou.android.powerful_alarm.databinding.ActivityAlarmStopBinding;
import java.time.*;
import java.time.format.DateTimeFormatter;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmStopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //現在時刻の取得
        ZonedDateTime current = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String currentTime = current.format(formatter);

        //音源再生
        //mediaPlayer = MediaPlayer.create(this,R.raw.komorebi_xylophone);
        //mediaPlayer.start();

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
        //if (mediaPlayer.isPlaying()) mediaPlayer.pause();
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
}

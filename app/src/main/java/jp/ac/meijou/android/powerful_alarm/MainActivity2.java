package jp.ac.meijou.android.powerful_alarm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import jp.ac.meijou.android.powerful_alarm.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    private AlarmAdapter alarmAdapter;
    private DatabaseHelper helper;

    // 消さないでAlarmSettingで使ってます
    final static public int EDIT_REQ_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        helper = new DatabaseHelper(this);

        // RecyclerViewの初期化
        initRecyclerView();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.add.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity2.this, AlarmSettings.class);
            startActivity(intent);
        });
    }

    // RecyclerViewの初期化
    private void initRecyclerView() {
        List<ListItem> alarms = Util.getAllAlarms(helper);

        // デバッグ用ログ: データのサイズを確認
        Log.d("MainActivity", "取得したアラーム数: " + alarms.size());

        alarmAdapter = new AlarmAdapter(alarms);

        // RecyclerViewにアダプタとレイアウトマネージャーを設定
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(alarmAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // アラームリストを再取得して更新する
        List<ListItem> alarms = Util.getAllAlarms(helper);
//        alarmAdapter.updateAlarms(alarms);

        // デバッグ用ログを追加
        Log.d("MainActivity2", "アラームの数: " + alarms.size());

        // アダプタが存在する場合は、データを更新
        if (alarmAdapter != null) {
            alarmAdapter.updateAlarms(alarms);
            Log.d("MainActivity2", "アラームリストを更新しました");
        } else {
            // アダプタがない場合は再初期化
            alarmAdapter = new AlarmAdapter(alarms);
            binding.recyclerView.setAdapter(alarmAdapter);
            Log.d("MainActivity2", "アダプターを初期化しました");
        }
    }

}
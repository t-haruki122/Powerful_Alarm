package jp.ac.meijou.android.powerful_alarm;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.health.connect.datatypes.SexualActivityRecord;
import android.os.Bundle;
import android.security.ConfirmationAlreadyPresentingException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.database.sqlite.SQLiteDatabaseKt;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import jp.ac.meijou.android.powerful_alarm.databinding.ActivityAlarmSettingsBinding;


public class AlarmSettings extends AppCompatActivity {

    private ActivityAlarmSettingsBinding binding;
    private int reqCode = -1; // requestコードを保持
    private int alarmID = -1; // アラームIDを保持
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAlarmSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // タイムピッカーの取得
        binding.timePicker.setIs24HourView(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // date
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner,
                getResources().getStringArray(R.array.date)
        );
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        binding.date.setAdapter(adapter);

        // sound
        ArrayAdapter<String> soundAdapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner,
                getResources().getStringArray(R.array.sound)
        );
        soundAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        binding.sound.setAdapter(soundAdapter);


        // return main
        binding.previous.setOnClickListener(view -> {
            Intent intent = new Intent(AlarmSettings.this, MainActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // アラームの編集モードか新規作成モードかの判定
        Intent intent = getIntent();
        reqCode = intent.getIntExtra(getString(R.string.request_code), -1);
        alarmID = intent.getIntExtra(getString(R.string.alarm_id), -1);

        if (reqCode == MainActivity2.EDIT_REQ_CODE) {
            // 編集モード: アラームIDを使って既存のデータをロード
            // ここでデータベースや他のストレージからアラーム情報を取得してUIに反映
            ListItem item = Util.getAlarmsByID(alarmID, helper); // アラーム情報を取得
            if (item != null) {
                binding.timePicker.setHour(Integer.parseInt(item.getHour()));
                binding.timePicker.setMinute(Integer.parseInt(item.getMinute()));
                binding.editAlarmText.setText(item.getAlarmName());
            }
        }

        // save
        binding.save.setOnClickListener(view -> {
            // タイムピッカーから時間を取得
            int hour = binding.timePicker.getHour();
            int minute = binding.timePicker.getMinute();

            // スピナーから選択された曜日を取得
            String selectedDay = binding.date.getSelectedItem().toString();

            // アラーム名をユーザー入力から取得
            String alarmName = binding.editAlarmText.getText().toString();  // 入力されたアラーム名を取得

            // デバッグログ
            Log.d("AlarmSettings", "Saveボタンが押されました: 時間 = " + hour + " 分 = " + minute);

            if (alarmName.isEmpty()) {
                alarmName = "My Alarm";  // 名前が入力されていなかった場合のデフォルト
            }

            // データベースにアラームを保存または更新
            if (helper == null) {
                helper = new DatabaseHelper(this);
            }

            SQLiteDatabase db = helper.getWritableDatabase();
            db.beginTransaction();  // トランザクションを使用

            try {
                ContentValues values = new ContentValues();
                values.put("name", alarmName);
                values.put("hour", hour);
                values.put("minute", minute);
                values.put("days", selectedDay);  // 選択された曜日を保存

                if (reqCode == MainActivity2.EDIT_REQ_CODE) {
                    // アラームを編集
                    String whereClause = "alarmID=?";
                    String[] whereArgs = new String[]{String.valueOf(alarmID)};
                    int rowsUpdated = db.update("alarms", values, whereClause, whereArgs);
                    if (rowsUpdated > 0) {
                        Log.d("AlarmSettings", "アラームが更新されました: ID = " + alarmID);
                        Toast.makeText(this, "アラームが更新されました", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.e("AlarmSettings", "アラームの更新に失敗しました: ID = " + alarmID);
                        Toast.makeText(this, "アラームの更新に失敗しました", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    long newRowId = db.insert("alarms", null, values);
                    if (newRowId != -1) {
                        Toast.makeText(this, "アラームが作成されました (ID: " + newRowId + ")", Toast.LENGTH_SHORT).show();
                        // デバッグログ
                        Log.d("AlarmSettings", "アラームが保存されました: ID = " + newRowId);
                    }
                    else {
                        Toast.makeText(this, "アラームの作成に失敗しました", Toast.LENGTH_SHORT).show();
                        Log.e("AlarmSettings", "アラームの作成に失敗しました");
                    }
                }

                db.setTransactionSuccessful();  // トランザクション成功
            }catch (Exception e) {
                e.printStackTrace();
                Log.e("AlarmSettings", "エラーが発生しました: " + e.getMessage());
                Toast.makeText(this, "エラー: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }finally {
                db.endTransaction(); // トランザクション終了
            }

            // 保存後、メイン画面に戻る
            Intent mainIntent = new Intent(AlarmSettings.this, MainActivity2.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mainIntent);
            finish();
        });



    }
}
package jp.ac.meijou.android.powerful_alarm;

import android.app.Activity;
import android.app.AlertDialog;
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

    // 初期状態の選択された曜日を保持するリスト
    private boolean[] selectedDays = new boolean[7];
    private String[] daysArray = {"月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日", "日曜日"};

    // 初期状態では"設定しない"が表示される
    private String selectedDaysString = "設定しない";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAlarmSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // タイムピッカーの取得
        binding.timePicker.setIs24HourView(false);
        // DatabaseHelperの初期化
        helper = new DatabaseHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // date
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                R.layout.custom_spinner,
//                getResources().getStringArray(R.array.date)
//        );
//        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
//        binding.date.setAdapter(adapter);

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

        // 曜日選択ボタンを押した時にダイアログを表示
        binding.dateButton.setOnClickListener(view -> showDaysPickerDialog());

        // アラームの編集モードか新規作成モードかの判定
        Intent intent = getIntent();
        reqCode = intent.getIntExtra(getString(R.string.request_code), -1);
        alarmID = intent.getIntExtra(getString(R.string.alarm_id), -1);

        if (reqCode == MainActivity2.EDIT_REQ_CODE && helper != null) {
            // 編集モード: アラームIDを使って既存のデータをロード
            // ここでデータベースや他のストレージからアラーム情報を取得してUIに反映
            ListItem item = Util.getAlarmsByID(alarmID, helper); // アラーム情報を取得
            if (item != null) {
                binding.timePicker.setHour(Integer.parseInt(item.getHour()));
                binding.timePicker.setMinute(Integer.parseInt(item.getMinute()));
                binding.editAlarmText.setText(item.getAlarmName());
                binding.dateButton.setText(item.getDays());
            }
        }

        // save
        binding.save.setOnClickListener(view -> {
            // タイムピッカーから時間を取得
            int hour = binding.timePicker.getHour();
            int minute = binding.timePicker.getMinute();

            // スピナーから選択された曜日を取得(スピナー)
//            String selectedDay = binding.date.getSelectedItem().toString();

            // 選択された曜日
            String selectedDaysResult = selectedDaysString.equals("設定しない") ? "設定しない" : selectedDaysString;

            // アラーム名をユーザー入力から取得
            String alarmName = binding.editAlarmText.getText().toString();  // 入力されたアラーム名を取得

            // デバッグログ
            Log.d("AlarmSettings", "Saveボタンが押されました: 時間 = " + hour + " 分 = " + minute);

            if (alarmName.isEmpty()) {
                alarmName = "My Alarm";  // 名前が入力されていなかった場合のデフォルト
            }

            // スピナーから選択されたサウンドを取得
            String selectedSound = binding.sound.getSelectedItem().toString();

            // データベースにアラームを保存または更新
            if (helper == null) {
                helper = new DatabaseHelper(this);
            }

            // データベースに保存
            SQLiteDatabase db = helper.getWritableDatabase();
            db.beginTransaction();  // トランザクションを使用

            try {
                ContentValues values = new ContentValues();
                values.put("name", alarmName);
                values.put("hour", hour);
                values.put("minute", minute);
                // values.put("days", selectedDay);  // 選択された曜日を保存
                values.put("days", selectedDaysResult);  // 選択された曜日を保存
                values.put("sound", selectedSound);  // サウンド列に値を保存

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

        // ダブルクリック
        if (reqCode == MainActivity2.EDIT_REQ_CODE) {
            // 編集モード: アラームIDを使って既存のデータをロード
            ListItem item = Util.getAlarmsByID(alarmID, helper); // アラーム情報を取得
            if (item != null) {
                binding.timePicker.setHour(Integer.parseInt(item.getHour()));
                binding.timePicker.setMinute(Integer.parseInt(item.getMinute()));
                binding.editAlarmText.setText(item.getAlarmName());

                // 曜日データの復元
                selectedDaysString = item.getDays(); // 取得した曜日データ
                binding.dateButton.setText(selectedDaysString);

                // サウンド設定をスピナーに反映
                String sound = item.getSound();
                if (sound != null) {
                    int position = soundAdapter.getPosition(sound);
                    binding.sound.setSelection(position);
                }

                // ダイアログ用に選択状態を反映
                String[] selectedDaysArray = selectedDaysString.split(" ");
                for (int i = 0; i < daysArray.length; i++) {
                    selectedDays[i] = false; // 初期化
                    for (String day : selectedDaysArray) {
                        if (day.equals(daysArray[i])) {
                            selectedDays[i] = true;
                            break;
                        }
                    }
                }
            }
        }

    }

    private void showDaysPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("曜日を設定してください");

        // 複数選択用のチェックボックスを表示
        builder.setMultiChoiceItems(daysArray, selectedDays, (dialog, which, isChecked) -> {
            selectedDays[which] = isChecked;
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            StringBuilder selectedDaysBuilder = new StringBuilder();
            for (int i = 0; i < selectedDays.length; i++) {
                if (selectedDays[i]) {
                    selectedDaysBuilder.append(daysArray[i]).append(" ");
                }
            }

            if (selectedDaysBuilder.length() == 0) {
                selectedDaysString = "設定しない";
            }
            else {
                selectedDaysString = selectedDaysBuilder.toString().trim();
            }

            binding.dateButton.setText(selectedDaysString);
        });

        builder.setNegativeButton("キャンセル", (dialog, which) -> {
            // キャンセルした場合は何もしない
        });

        builder.show(); // ダイアログを表示
    }
}
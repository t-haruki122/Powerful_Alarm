package jp.ac.meijou.android.powerful_alarm;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import jp.ac.meijou.android.powerful_alarm.databinding.ActivityAlarmSettingsBinding;
import jp.ac.meijou.android.powerful_alarm.databinding.ActivityAreaSettingsBinding;
import okhttp3.OkHttpClient;


public class AreaSettings extends AppCompatActivity {
    private ActivityAreaSettingsBinding binding;
    private PrefDataStore prefDataStore;

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
        prefDataStore.getString("area")
                .ifPresent(prev_area -> {
                    binding.currentText.setText("現在: " + prev_area);
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
        });
    }
}
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


public class AreaSettings extends AppCompatActivity {
    private ActivityAreaSettingsBinding binding;

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

        // return main copied from AlarmSettings thank you
        binding.previous.setOnClickListener(view -> {
            Intent intent = new Intent(AreaSettings.this, MainActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        binding.confirmButton.setOnClickListener(view -> {
            String area = binding.areaSpinner.getSelectedItem().toString();
            binding.currentText.setText("現在" + ": " + area);

            // 選択した地域を保持するプログラムを記述
        });

    }
}
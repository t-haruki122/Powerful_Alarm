package jp.ac.meijou.android.powerful_alarm;

import android.app.Activity;
import android.health.connect.datatypes.SexualActivityRecord;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import jp.ac.meijou.android.powerful_alarm.databinding.ActivityAlarmSettingsBinding;


public class AlarmSettings extends AppCompatActivity {

    private ActivityAlarmSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAlarmSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
    }
}
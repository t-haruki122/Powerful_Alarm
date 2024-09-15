package jp.ac.meijou.android.powerful_alarm;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<ListItem> alarmList;

    public AlarmAdapter(List<ListItem> alarmList) {
        this.alarmList = alarmList;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        ListItem alarm = alarmList.get(position);
        holder.alarmName.setText(alarm.getAlarmName());
        holder.alarmTime.setText(String.format("%02d:%02d", Integer.parseInt(alarm.getHour()), Integer.parseInt(alarm.getMinute())));

        // 曜日を設定
        holder.dates.setText(alarm.getDays());  // 日付のTextViewに曜日を表示

        // デバッグ用のログを追加して、どのデータが表示されているか確認
        Log.d("AlarmAdapter", "表示するアラーム: " + alarm.getAlarmName() + " " + alarm.getHour() + ":" + alarm.getMinute() + " 曜日: " + alarm.getDays());
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    // アラームリストの更新
    public void updateAlarms(List<ListItem> newAlarms) {
        this.alarmList.clear();       // 一度リストをクリア
        this.alarmList.addAll(newAlarms);  // 新しいデータを追加
        notifyDataSetChanged();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView alarmName, alarmTime, dates;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmName = itemView.findViewById(R.id.alarmName);
            alarmTime = itemView.findViewById(R.id.alarmTime);
            dates = itemView.findViewById(R.id.dates);
        }
    }
}

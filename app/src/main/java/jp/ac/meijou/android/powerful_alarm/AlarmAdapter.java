package jp.ac.meijou.android.powerful_alarm;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<ListItem> alarmList;
    private OnAlarmClickListener listener;

    public AlarmAdapter(List<ListItem> alarmList, OnAlarmClickListener listener) {

        this.alarmList = alarmList;
        this.listener = listener; // コンストラクタでリスナーを初期化
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

        // SwitchCompat の状態を設定
        holder.switchCompat.setChecked(alarm.isActive());

        // ダブルクリック
        holder.itemView.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                if (listener != null) {
                    Log.d("AlarmAdapter", "ダブルクリック検出: ID = " + alarm.getAlarmID());
                    listener.onAlarmDoubleClick(alarm.getAlarmID());
                }
            }
        });

        // デバッグ用のログを追加して、どのデータが表示されているか確認
        Log.d("AlarmAdapter", "表示するアラーム: " + alarm.getAlarmName() + " " + alarm.getHour() + ":" + alarm.getMinute() + " 曜日: " + alarm.getDays());

        // 削除ボタンのクリックリスナーを設定
        // 削除ボタンのクリックリスナーを設定
        holder.deleteButton.setOnClickListener(v -> {
            // ダイアログを表示
            new android.app.AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("確認")
                    .setMessage("本当に削除しますか？")
                    .setPositiveButton("はい", (dialog, which) -> {
                        // ユーザーが「はい」を選択した場合に削除を実行
                        DatabaseHelper helper = new DatabaseHelper(holder.itemView.getContext());
                        SQLiteDatabase db = helper.getWritableDatabase();

                        int alarmID = alarm.getAlarmID();
                        int rowsDeleted = db.delete("alarms", "alarmID=?", new String[]{String.valueOf(alarmID)});

                        if (rowsDeleted > 0) {
                            Toast.makeText(holder.itemView.getContext(), "アラームを削除しました", Toast.LENGTH_SHORT).show();
                            alarmList.remove(position); // リストから削除
                            notifyItemRemoved(position); // UIを更新
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "削除に失敗しました", Toast.LENGTH_SHORT).show();
                        }

                        db.close();
                    })
                    .setNegativeButton("いいえ", (dialog, which) -> {
                        // ユーザーが「いいえ」を選択した場合は何もしない
                        dialog.dismiss();
                    })
                    .show(); // ダイアログを表示
        });


        // Switch の状態変更を検知してデータベースを更新
        holder.switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.setActive(isChecked);
            DatabaseHelper dbHelper = new DatabaseHelper(holder.itemView.getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("isActive", isChecked ? 1 : 0);
            String whereClause = "alarmID=?";
            String[] whereArgs = {String.valueOf(alarm.getAlarmID())};

            int rowsUpdated = db.update("alarms", values, whereClause, whereArgs);
            if (rowsUpdated > 0) {
                Log.d("AlarmAdapter", "アラーム状態が更新されました: ID = " + alarm.getAlarmID() + ", isActive = " + isChecked);
            }
            db.close();
        });

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
        Button deleteButton; // 削除ボタンを追加
        SwitchCompat switchCompat; // スイッチを追加

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmName = itemView.findViewById(R.id.alarmName);
            alarmTime = itemView.findViewById(R.id.alarmTime);
            dates = itemView.findViewById(R.id.dates);
            deleteButton = itemView.findViewById(R.id.deleteButton); // 削除ボタンを取得
            switchCompat = itemView.findViewById(R.id.onoff); // XML の ID を設定
        }
    }

    // ダブルクリックのインターフェース
    public interface OnAlarmClickListener {
        void onAlarmDoubleClick(int alarmID);
    }
}

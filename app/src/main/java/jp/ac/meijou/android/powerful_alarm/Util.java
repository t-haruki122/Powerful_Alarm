package jp.ac.meijou.android.powerful_alarm;

import java.util.List;

public class Util {
    // アラームをIDで取得するメソッド
    public static ListItem getAlarmsByID(int alarmID, DatabaseHelper helper) {
        return helper.getAlarm(alarmID);  // DatabaseHelper内でgetAlarmを呼び出す
    }

    // すべてのアラームを取得するメソッド addしました
    public static List<ListItem> getAllAlarms(DatabaseHelper helper) {
        return helper.getAllAlarms();  // DatabaseHelper内でgetAllAlarmsを呼び出す
    }
}

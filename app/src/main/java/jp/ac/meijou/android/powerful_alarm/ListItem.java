package jp.ac.meijou.android.powerful_alarm;

public class ListItem {
    private int alarmID; // アラームID
    private String hour;
    private String minute;
    private String alarmName;
    private String days;
    private String sound; // サウンドを表すフィールドを追加
    private boolean isActive;

    public ListItem(int alarmID,String alarmName,String hour, String minute, String days, String sound, boolean isActive) {
        this.alarmID = alarmID;
        this.hour = hour;
        this.minute = minute;
        this.alarmName = alarmName;
        this.days = days;
        this.sound = sound;
        this.isActive = isActive;
    }

    // Getter と Setter
    public int getAlarmID() {
        return alarmID;
    }
    public void setAlarmID(int alarmID) {
        this.alarmID = alarmID;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

    public  String getAlarmName() {
        return alarmName;
    }

    public String getDays() {
        return days;
    }

    // サウンドのゲッターとセッター
    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

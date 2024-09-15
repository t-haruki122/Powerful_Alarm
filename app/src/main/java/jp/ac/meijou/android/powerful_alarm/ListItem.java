package jp.ac.meijou.android.powerful_alarm;

public class ListItem {
    private String hour;
    private String minute;
    private String alarmName;
    private String days;

    public ListItem(String alarmName,String hour, String minute, String days) {
        this.hour = hour;
        this.minute = minute;
        this.alarmName = alarmName;
        this.days = days;
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
}

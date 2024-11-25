package jp.ac.meijou.android.powerful_alarm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.number.UnlocalizedNumberFormatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alarms.db";
    private static final int DATABASE_VERSION = 5;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "onCreate: テーブルを作成しています");

//        db.execSQL("CREATE TABLE alarms (id INTEGER PRIMARY KEY, hour TEXT, minute TEXT)");
        String createTable = "CREATE TABLE alarms (" +
                "alarmID INTEGER PRIMARY KEY AUTOINCREMENT, " + // alarmID を自動生成するプライマリキーに設定
                "name TEXT, "+
                "hour TEXT, " +
                "minute TEXT, " +
                "days TEXT)";
        db.execSQL(createTable);

        Log.d("DatabaseHelper", "onCreate: テーブルが作成されました");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "onUpgrade: データベースをアップグレードしています");

        db.execSQL("DROP TABLE IF EXISTS alarms");
        onCreate(db);
    }

    // アラームを取得するメソッド
    public ListItem getAlarm(int alarmID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "alarms", null, "id=?", new String[]{String.valueOf(alarmID)}, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String hour = cursor.getString(cursor.getColumnIndexOrThrow("hour"));
            String minute = cursor.getString(cursor.getColumnIndexOrThrow("minute"));
            String days = cursor.getString(cursor.getColumnIndexOrThrow("days"));
            cursor.close();
            return new ListItem(name, hour, minute, days);
        }

        return null;
    }

    // すべてのアラームを取得するメソッド 追加しました
    public List<ListItem> getAllAlarms() {
        List<ListItem> alarms = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("alarms", null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String hour = cursor.getString(cursor.getColumnIndexOrThrow("hour"));
                String minute = cursor.getString(cursor.getColumnIndexOrThrow("minute"));
                String days = cursor.getString(cursor.getColumnIndexOrThrow("days"));
                alarms.add(new ListItem(name, hour, minute, days));

                // デバッグログ: データ取得を確認
                Log.d("DatabaseHelper", "取得したアラーム: " + hour + ":" + minute);
            }
            cursor.close();
        }

        // デバッグ用ログ: データの数を確認
        Log.d("DatabaseHelper", "取得したアラーム数: " + alarms.size());

        return alarms;
    }
}
package jhw.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.AlarmManagerCompat;
import android.util.Log;

import java.util.Calendar;

import jhw.alarm.data.AlarmItem;

public class AlarmCompat {

    public static final String ALARM_REPEAT = "alarm_repeat";
    public static final long SECOND = 1000;        // 秒
    public static final long MINUTE = SECOND * 60;   // 分钟
    public static final long HOUR = MINUTE * 60;   // 小时
    public static final long DAY = HOUR * 24;      // 分钟
    public static final long INTERVAL = DAY;       // 一天24小时;

    public static void addAlarm(Context context, AlarmItem alarmItem) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar firstAlarmDate = Calendar.getInstance();
        firstAlarmDate.setTimeInMillis(alarmItem.date);

        Calendar alarmDate = Calendar.getInstance();
        alarmDate.set(Calendar.HOUR_OF_DAY, firstAlarmDate.get(Calendar.HOUR_OF_DAY));
        alarmDate.set(Calendar.MINUTE, firstAlarmDate.get(Calendar.MINUTE));
        alarmDate.set(Calendar.SECOND, firstAlarmDate.get(Calendar.SECOND));

        Calendar today = Calendar.getInstance();
        if (alarmDate.before(today)) {
            // 如果时间小于当前时间，加一天
            alarmDate.add(Calendar.DATE, 1);
        }
        if (alarmManager != null) {
            AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, alarmDate.getTimeInMillis(), getBroadcastPendingIntent(context, alarmItem.id, INTERVAL));
        }
        Log.d("jihongwen", "add Alarm time " + AlarmUtil.getDateFormat(alarmDate.getTimeInMillis()));
    }

    public static void repeatAlarm(Context context, long intervalMillis, boolean repeat, PendingIntent pendingIntent) {
        if (context == null) {
            return;
        }
        if (repeat) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) {
                return;
            }
            long date = System.currentTimeMillis() + intervalMillis;
            //long date = System.currentTimeMillis() + MINUTE * 5;
            // test 2 minute
            AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, date, pendingIntent);
            Log.d("jihongwen", "repeatAlarm time " + AlarmUtil.getDateFormat(date));
        }
    }

    public static PendingIntent getBroadcastPendingIntent(Context context, int requestCode, long intervalMillis) {
        Intent intent = new Intent(context, AlarmRepeatReceiver.class);
        intent.setAction(ALARM_REPEAT);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("intervalMillis", intervalMillis);
        intent.putExtra("repeat", true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }

    @SuppressWarnings("unused")
    public static PendingIntent getServicePendingIntent(Context context, Class<? extends Service> serviceClass, int requestCode, long intervalMillis) {
        Intent intent = new Intent(context, serviceClass);
        intent.setAction(ALARM_REPEAT);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("intervalMillis", intervalMillis);
        intent.putExtra("repeat", true);
        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }

    public static void cancel(Context context, int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(getBroadcastPendingIntent(context, id, INTERVAL));
        }
    }
}

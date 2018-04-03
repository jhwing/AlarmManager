package jhw.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import jhw.alarm.data.AlarmItem;
import jhw.alarm.data.AlarmRepository;

public class AlarmService extends Service {

    public static Class<? extends AlarmService> serviceClass = AlarmService.class;

    protected static final String LOG_TAG = "AlarmService";

    public static final long SECOND = 1000;        // 秒
    public static final long MINUTE = SECOND * 60;   // 分钟
    public static final long HOUR = MINUTE * 60;   // 小时
    public static final long DAY = HOUR * 24;      // 分钟
    public static final long INTERVAL = DAY;       // 一天24小时;

    AlarmRepository alarmRepository;
    boolean needInit = false;

    static boolean isStarted = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        alarmRepository = new AlarmRepository(this);
        needInit = true;
        createNotification();
        startForeground(1, createNotification());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        isStarted = false;
    }

    public Notification createNotification() {
        String channelId = "alarm_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelId, "alarm_channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("提醒服务")
                .setContentText("content text");
        return builder.build();
    }

    public void onAlarm(int id) {
        Log.d("jihongwen", "onAlarm id=" + id);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (needInit) {
            init();
            return super.onStartCommand(intent, flags, startId);
        }
        String action = intent.getStringExtra("action");
        if ("alarm".equals(action)) {
            int requestCode = intent.getIntExtra("requestCode", -1);
            onAlarm(requestCode);
            repeatAlarm(this, intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static PendingIntent getPendingIntent(Context context, int requestCode, long intervalMillis) {
        Intent intent = new Intent(context, getServiceClass());
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("intervalMillis", intervalMillis);
        intent.putExtra("repeat", true);
        intent.putExtra("action", "alarm");
        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }

    public static void addAlarm(Context context, AlarmItem alarmItem) {
        if (!isStarted()) {
            // 如果没有启动服务，直接启动服务，通过服务初始化
            context.startService(new Intent(context, getServiceClass()));
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
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
            AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, alarmDate.getTimeInMillis(), getPendingIntent(context, alarmItem.id, INTERVAL));
        }
        Log.d("jihongwen", "add Alarm time " + AlarmUtil.getDateFormat(alarmDate.getTimeInMillis()));
    }

    @NonNull
    private static Class<? extends AlarmService> getServiceClass() {
        return serviceClass;
    }

    public static void repeatAlarm(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        int requestCode = intent.getIntExtra("requestCode", -1);
        long intervalMillis = intent.getLongExtra("intervalMillis", -1);
        boolean repeat = intent.getBooleanExtra("repeat", false);
        if (repeat) {
            PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            if (alarmManager == null) {
                return;
            }
            long date = System.currentTimeMillis();
            AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, date + intervalMillis, pendingIntent);
            Log.d("jihongwen", "repeatAlarm time " + AlarmUtil.getDateFormat(date));
        }
    }

    public static void cancel(Context context, int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(AlarmService.getPendingIntent(context, id, INTERVAL));
        }
    }

    public static boolean isStarted() {
        return isStarted;
    }

    private void init() {
        needInit = false;
        Log.d("jihongwen", "AlarmService init");
        reAlarm();
    }

    private void reAlarm() {
        if (alarmRepository == null) {
            return;
        }
        alarmRepository.getAlarmList(new AlarmRepository.LoadTasksCallback() {
            @Override
            public void onItemsLoaded(List<AlarmItem> items) {
                for (AlarmItem item : items) {
                    addAlarm(AlarmService.this, item);
                }
            }

            @Override
            public void onDataNotAvailable() {
            }
        });
    }

}

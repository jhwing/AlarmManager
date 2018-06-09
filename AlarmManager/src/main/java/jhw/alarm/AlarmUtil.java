package jhw.alarm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmUtil {

    public static final long SECOND = 1000;        // 秒
    public static final long MINUTE = SECOND * 60;   // 分钟
    public static final long HOUR = MINUTE * 60;   // 小时
    public static final long DAY = HOUR * 24;      // 分钟
    public static final long INTERVAL = DAY;       // 一天24小时;

    public static String getDateFormat(long date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(date));
    }

//    public static void requestService(Class<? extends AlarmService> serviceClass) {
//        AlarmService.serviceClass = serviceClass;
//    }
//
//    public static void addAlarm(Context context, AlarmItem alarmItem) {
//        AlarmService.addAlarm(context, alarmItem);
//    }
//
//    public static void resume(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(new Intent(context, AlarmService.class));
//        } else {
//            context.startService(new Intent(context, AlarmService.class));
//        }
//    }
//
//    public static void cancel(Context context, int id) {
//        AlarmService.cancel(context, id);
//    }
//
//    public static void stopAlarmService(Context context) {
//        context.stopService(new Intent(context, AlarmService.class));
//        //AlarmService.stopService(context);
//    }
}

package jhw.alarm.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import jhw.alarm.AlarmService;

public class MyAlarmService extends AlarmService {

    @Override
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
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("提醒服务")
                .setContentText("content text");
        return builder.build();
    }

    @Override
    public void onAlarm(int id) {
        super.onAlarm(id);
        AlarmNoticeUtil.showNotice(this, id);
    }
}

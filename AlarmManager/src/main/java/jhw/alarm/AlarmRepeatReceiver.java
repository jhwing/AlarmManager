package jhw.alarm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmRepeatReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AlarmCompat.ALARM_REPEAT.equals(intent.getAction())) {
            int requestCode = intent.getIntExtra("requestCode", -1);
            if (requestCode != -1) {
                long intervalMillis = intent.getLongExtra("intervalMillis", -1);
                boolean repeat = intent.getBooleanExtra("repeat", false);
                if (repeat) {
                    AlarmCompat.repeatAlarm(context, intervalMillis, repeat, PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT));
                }
                // do something
                Intent sendMsg = new Intent("jhw.alarm.action");
                sendMsg.putExtra("aid", requestCode);
                context.sendBroadcast(sendMsg);
                //AlarmNoticeUtil.showNotice(context, requestCode);
            }
        }
    }
}

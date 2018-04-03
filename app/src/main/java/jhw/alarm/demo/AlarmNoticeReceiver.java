package jhw.alarm.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmNoticeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }
}

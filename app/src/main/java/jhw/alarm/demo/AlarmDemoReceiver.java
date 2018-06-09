package jhw.alarm.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmDemoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("jhw.alarm.action".equals(intent.getAction())) {
            int aid = intent.getIntExtra("aid", -1);
            if (aid != -1) {
                AlarmNoticeUtil.showNotice(context, aid);
            }
        }
    }
}

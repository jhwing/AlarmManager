package jhw.alarm;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import jhw.alarm.data.AlarmItem;

/**
 * 不能在指定的某一时刻执行
 */
@Deprecated
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AlarmJobService extends JobService {

    protected static final String LOG_TAG = "AlarmJobService";

    public void onAlarm(int id) {
        Log.d(LOG_TAG, "onAlarm id=" + id);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        int jobId = jobParameters.getJobId();
        long date = jobParameters.getExtras().getLong("date");
        onAlarm(jobId);
        AlarmJobService.addNextAlarm(this, jobId, date);
        jobFinished(jobParameters, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    /**
     * @param jobId
     * @param date
     */
    public static void addNextAlarm(Context context, int jobId, long date) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null) {
            return;
        }

        Calendar currencyCalendar = Calendar.getInstance();
        currencyCalendar.set(Calendar.SECOND, 0);

        // 对齐日期
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.setTimeInMillis(date);
        int hourOfDay = alarmCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = alarmCalendar.get(Calendar.MINUTE);
        alarmCalendar.set(Calendar.YEAR, currencyCalendar.get(Calendar.YEAR));
        alarmCalendar.set(Calendar.MONTH, currencyCalendar.get(Calendar.MONTH));
        alarmCalendar.set(Calendar.DAY_OF_YEAR, currencyCalendar.get(Calendar.DAY_OF_YEAR));
        alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        alarmCalendar.set(Calendar.MINUTE, minute);
        alarmCalendar.set(Calendar.SECOND, 0);

        if (alarmCalendar.before(currencyCalendar)) {
            // 如果时间小于当前时间
            if (AlarmJobConfig.STEP == AlarmJobConfig.STEP_TYPE.DAY) {
                alarmCalendar.add(Calendar.DATE, 1);
            } else if (AlarmJobConfig.STEP == AlarmJobConfig.STEP_TYPE.MONTH) {
                alarmCalendar.add(Calendar.MONTH, 1);
            } else if (AlarmJobConfig.STEP == AlarmJobConfig.STEP_TYPE.YEAR) {
                alarmCalendar.add(Calendar.YEAR, 1);
            } else {
                // default is day
                alarmCalendar.add(Calendar.DATE, 1);
            }
        }

        long nextInterval = Math.abs(currencyCalendar.getTimeInMillis() - alarmCalendar.getTimeInMillis());

        PersistableBundle extras = new PersistableBundle();
        extras.putLong("date", date);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, new ComponentName(context, AlarmJobService.class))
                //.setMinimumLatency(AlarmJobConfig.INTERVAL_MILLIS) // 延时执行
                .setMinimumLatency(nextInterval) // 延时执行
                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // 任何网络
                .setPersisted(true) // 设备重启后，任务是否继续存在
                .setExtras(extras); // 额外参数
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(true);
        }
        int resultCode = jobScheduler.schedule(builder.build()); // 调度任务
        Log.d(LOG_TAG, "job next schedule " + AlarmUtil.getDateFormat(alarmCalendar.getTimeInMillis()));
        Log.d(LOG_TAG, "job schedule resultCode=" + resultCode);
    }

    /**
     * 恢复定时提醒
     *
     * @param context
     * @param list
     */
    public static void resume(Context context, List<AlarmItem> list) {
        for (AlarmItem item : list) {
            addNextAlarm(context, item.id, item.date);
        }
    }

    public static void cancel(Context context, int id) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.cancel(id);
        }
    }

    public static void cancelAll(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.cancelAll();
        }
    }
}

package jhw.alarm.data;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by jihongwen on 2017/11/28.
 */

public class AlarmRepository {

    private final Executor diskIO;

    private final Executor mainThread;

    private final AlarmDao alarmDao;

    public AlarmRepository(Context context) {
        diskIO = new DiskIOThreadExecutor();
        mainThread = new MainThreadExecutor();
        alarmDao = AppDatabase.instance(context).alarmDao();
    }

    public void isExist(final IsExistCallback isExistCallback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final int count = alarmDao.isExist();
                mainThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        isExistCallback.onExist(count > 0);
                    }
                });
            }
        };
        diskIO.execute(runnable);
    }

    public void getAlarmList(final LoadTasksCallback loadTasksCallback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<AlarmItem> alarmItems = alarmDao.getAlarmItems();
                mainThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (alarmItems != null) {
                            loadTasksCallback.onItemsLoaded(alarmItems);
                        } else {
                            loadTasksCallback.onDataNotAvailable();
                        }
                    }
                });
            }
        };
        diskIO.execute(runnable);
    }

    public void saveAlarm(final AlarmItem alarmItem) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                alarmDao.insertAlarm(alarmItem);
            }
        };
        diskIO.execute(runnable);
    }

    public void delete(final AlarmItem alarmItem) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                alarmDao.delete(alarmItem);
            }
        };
        diskIO.execute(runnable);

    }

    public interface LoadTasksCallback {

        void onItemsLoaded(List<AlarmItem> items);

        void onDataNotAvailable();
    }

    public interface GetTaskCallback {

        void onItemLoaded(AlarmItem items);

        void onDataNotAvailable();
    }

    public interface IsExistCallback {
        void onExist(boolean isExist);
    }
}

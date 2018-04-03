package jhw.alarm.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by jihongwen on 2017/11/29.
 */

@Database(entities = {AlarmItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract AlarmDao alarmDao();

    private static final Object sLock = new Object();

    public static AppDatabase instance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "alarm.db").build();
            }
            return INSTANCE;
        }
    }
}

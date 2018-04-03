package jhw.alarm.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by jihongwen on 2017/11/29.
 */

@Dao
public interface AlarmDao {

    @Insert
    void insertAlarm(AlarmItem alarmItem);

    @Query("SELECT * FROM ALARM")
    List<AlarmItem> getAlarmItems();

    @Delete
    void delete(AlarmItem alarmItem);

    @Query("select count(*) from ALARM")
    int isExist();
}

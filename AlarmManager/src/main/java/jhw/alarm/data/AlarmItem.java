package jhw.alarm.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import jhw.alarm.AlarmUtil;


/**
 * Created by jihongwen on 2017/11/29.
 */

@Entity(tableName = "alarm")
public class AlarmItem {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "alarm_id")
    public int id;

    @Nullable
    @ColumnInfo(name = "date")
    public long date;

    @Nullable
    @ColumnInfo(name = "interval")
    public long interval;

    @Nullable
    @ColumnInfo(name = "repeat")
    public boolean repeat;

    public AlarmItem(int id, long date, long interval, boolean repeat) {
        this.id = id;
        this.date = date;
        this.interval = interval;
        this.repeat = repeat;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public String getDate() {
        return AlarmUtil.getDateFormat(date);
    }

}

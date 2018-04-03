package jhw.alarm;

/**
 * 不能在指定的某一时刻执行
 */
@Deprecated
public class AlarmJobConfig {

    enum STEP_TYPE {
        DAY, MONTH, YEAR
    }

    public static final STEP_TYPE STEP = STEP_TYPE.DAY;

    public static final long INTERVAL_MILLIS = 1000 * 10;

}

package neilbantoc.riseandsmile.model;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by neilbantoc on 22/11/2016.
 */

public class Alarm extends RealmObject{
    public static final int REPEAT_SUNDAY = 0x00 << 1;
    public static final int REPEAT_MONDAY = 0x00 << 2;
    public static final int REPEAT_TUESDAY = 0x00 << 3;
    public static final int REPEAT_WEDNESDAY = 0x00 << 4;
    public static final int REPEAT_THURSDAY = 0x00 << 5;
    public static final int REPEAT_FRIDAY = 0x00 << 6;
    public static final int REPEAT_SATURDAY = 0x00 << 7;
    public static final long MINUTE_IN_MILLIS = 60 * 1000;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;

    @PrimaryKey
    private long mId;
    private long mTime;
    private boolean mActive;
    private int mRepeatDays;

    public static Comparator<Alarm> SORTER = new Comparator<Alarm>() {
        @Override
        public int compare(Alarm alarm, Alarm t1) {
            return (int) (alarm.getTime() % DAY_IN_MILLIS - t1.getTime() % DAY_IN_MILLIS);
        }
    };

    @Ignore
    private GregorianCalendar mCalendar;

    public Alarm() {
        mCalendar = new GregorianCalendar();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getTime() {
        return mTime;
    }

    public GregorianCalendar getTimeAsCalendar() {
        mCalendar.setTimeInMillis(mTime);
        return mCalendar;
    }

    public int getHour() {
        mCalendar.setTimeInMillis(mTime);
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        mCalendar.setTimeInMillis(mTime);
        return mCalendar.get(Calendar.MINUTE);
    }

    public void setTime(long time) {
        mTime = time - (time % MINUTE_IN_MILLIS);
    }

    public void setTimeRelativeToNow() {
        setTimeRelativeToNow(getHour(), getMinute());
    }

    public void setTimeRelativeToNow(int hourOfDay, int minute) {
        GregorianCalendar targetTime = new GregorianCalendar();
        targetTime.setTimeInMillis(System.currentTimeMillis());

        targetTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        targetTime.set(Calendar.MINUTE, minute);

        //ToDo: account for repeating alarms

        GregorianCalendar currentTime = new GregorianCalendar();
        currentTime.setTimeInMillis(System.currentTimeMillis());

        if (targetTime.before(currentTime)) {
            targetTime.add(Calendar.DATE, 1);
        }

        setTime(targetTime.getTimeInMillis());
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    public int getRepeatDays() {
        return mRepeatDays;
    }

    public void setRepeatDays(int repeatDays) {
        mRepeatDays = repeatDays;
    }
}

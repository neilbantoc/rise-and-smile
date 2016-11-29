package neilbantoc.riseandsmile.model;

import io.realm.RealmObject;

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

    private long mId;
    private long mTime;
    private boolean mActive;
    private int mRepeatDays;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        this.mTime = mTime;
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

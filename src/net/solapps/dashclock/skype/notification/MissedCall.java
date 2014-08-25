package net.solapps.dashclock.skype.notification;

import android.app.Notification;
import android.service.notification.StatusBarNotification;

public class MissedCall {

    private long mTime;
    private String mFrom;

    public MissedCall(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        mTime = notification.when;
        if (notification.tickerText != null) {
            String[] parts = notification.tickerText.toString().split("\n");
            mFrom = parts[0];
        }
    }

    public String getFrom() {
        return mFrom;
    }

    public long getTime() {
        return mTime;
    }
}
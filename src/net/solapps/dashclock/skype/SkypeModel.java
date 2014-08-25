package net.solapps.dashclock.skype;

import java.util.HashMap;
import java.util.Map;

import net.solapps.dashclock.notification.service.IModelFactory.IModel;
import net.solapps.dashclock.skype.notification.GenericNotification;
import net.solapps.dashclock.skype.notification.MissedCall;
import net.solapps.dashclock.skype.notification.UnreadMessages;
import android.app.PendingIntent;

public class SkypeModel implements IModel {

    private final Map<Integer, MissedCall> mCalls = new HashMap<Integer, MissedCall>();
    private UnreadMessages mMessages;
    private GenericNotification mGenericNotification;
    private String mPackageName;
    private PendingIntent mContentIntent;

    @Override
    public PendingIntent contentIntent() {
        return mContentIntent;
    }

    public SkypeModel contentIntent(PendingIntent intent) {
        mContentIntent = intent;
        return this;
    }

    @Override
    public String packageName() {
        return mPackageName;
    }

    public SkypeModel packageName(String packageName) {
        mPackageName = packageName;
        return this;
    }

    public boolean hasMissedCalls() {
        return !mCalls.isEmpty();
    }

    public boolean hasUnreadMessages() {
        return mMessages != null;
    }

    public int getMissedCallsCount() {
        return mCalls.size();
    }

    public int getUnreadMessagesCount() {
        return mMessages != null ? mMessages.getUnread() : 0;
    }

    public MissedCall getLastCall() {
        MissedCall last = null;
        for (MissedCall notification : mCalls.values()) {
            if (last == null || notification.getTime() > last.getTime()) {
                last = notification;
            }
        }
        return last;
    }

    public UnreadMessages getMessages() {
        return mMessages;
    }

    public void addMessages(UnreadMessages notification) {
        mMessages = notification;

    }

    public void addCall(int id, MissedCall notification) {
        mCalls.put(id, notification);
    }

    public void addGenericNotification(GenericNotification notification) {
        mGenericNotification = notification;
    }

    public GenericNotification getGenericNotification() {
        return mGenericNotification;
    }

    public boolean hasGenericNotification() {
        return mGenericNotification != null;
    }
}
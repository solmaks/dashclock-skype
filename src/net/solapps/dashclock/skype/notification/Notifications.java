package net.solapps.dashclock.skype.notification;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;

public class Notifications {

    private final Map<Integer, MissedCall> mCalls = new HashMap<Integer, MissedCall>();
    private UnreadMessages mMessages;
    private GenericNotification mGenericNotification;
    private Intent mIntent;

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public void addGenericNotification(GenericNotification notification) {
        mGenericNotification = notification;
    }

    public void removeGenericNotification() {
        mGenericNotification = null;
    }

    public boolean hasGenericNotification() {
        return mGenericNotification != null;
    }

    public GenericNotification getGenericNotification() {
        return mGenericNotification;
    }

    public void addCall(int id, MissedCall notification) {
        mCalls.put(id, notification);
    }

    public void addMessages(UnreadMessages notification) {
        mMessages = notification;
    }

    public void removeMessages() {
        mMessages = null;
    }

    public void removeCall(int id) {
        mCalls.remove(id);
    }

    public boolean hasMissedCalls() {
        return !mCalls.isEmpty();
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

    public boolean hasUnreadMessages() {
        return mMessages != null;
    }

    public void clear() {
        mMessages = null;
        mCalls.clear();
    }
}